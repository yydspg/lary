// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.exec.OpSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tiny expression DSL for module sub-graphs.
 *
 * <pre>{@code
 * scaled = x * 2
 * score  = scaled + y
 * mask   = x > 10
 * best   = max(select(mask, score, 0))
 * }</pre>
 *
 * <p>Statements are assignments; identifiers are columns, numbers are scalar
 * constants. Supported functions: sum, mean, count, max, min, stddev, abs, neg,
 * square, sqrt, exp, log, select, filter, cast, quantize, dequantize, dot.
 */
public final class ColumnDsl {

    private ColumnDsl() {
    }

    public static List<OpSpec> parse(String source) {
        return new Parser(source).parse();
    }

    private enum Kind {
        NUMBER, IDENT, SYMBOL, EOF
    }

    private record Token(Kind kind, String text, double number, int position) {
    }

    private record Value(boolean literal, double number, String name) {
        static Value lit(double value) {
            return new Value(true, value, "");
        }

        static Value named(String name) {
            return new Value(false, 0.0, name);
        }
    }

    private static final class Parser {
        private final List<Token> tokens;
        private int index;
        private final List<OpSpec> ops = new ArrayList<>();
        private int tempCounter;

        private Parser(String source) {
            this.tokens = tokenize(source);
        }

        private List<OpSpec> parse() {
            while (!atEnd()) {
                parseStatement();
            }
            return List.copyOf(ops);
        }

        private void parseStatement() {
            Token lhs = expect(Kind.IDENT, "statement must start with an identifier");
            expectSymbol("=");
            Value value = parseExpr(0);
            assign(lhs.text(), value);
        }

        private void assign(String lhs, Value value) {
            if (value.literal) {
                ops.add(new OpSpec("const", List.of(), List.of(lhs),
                        Map.of("value", format(value.number), "size", "1")));
                return;
            }
            if (value.name.equals(lhs)) {
                return; // identity / no-op
            }
            ops.add(new OpSpec("identity", List.of(value.name), List.of(lhs)));
        }

        private Value parseExpr(int minPrecedence) {
            Value left = parsePrimary();
            while (true) {
                Token token = peek();
                if (token.kind != Kind.SYMBOL) break;
                int precedence = precedence(token.text);
                if (precedence < minPrecedence) break;
                advance();
                String op = token.text;
                Value right = parseExpr(precedence + 1);
                left = makeBinary(left, right, op);
            }
            return left;
        }

        private Value parsePrimary() {
            Token token = advance();
            if (token.kind == Kind.NUMBER) {
                return Value.lit(token.number);
            }
            if (token.kind == Kind.IDENT) {
                if (peek().kind == Kind.SYMBOL && peek().text.equals("(")) {
                    advance();
                    return parseCall(token.text);
                }
                return Value.named(token.text);
            }
            if (token.kind == Kind.SYMBOL && token.text.equals("-")) {
                Value value = parsePrimary();
                if (value.literal) {
                    return Value.lit(-value.number);
                }
                String out = temp();
                ops.add(new OpSpec("neg", List.of(value.name), List.of(out)));
                return Value.named(out);
            }
            if (token.kind == Kind.SYMBOL && token.text.equals("(")) {
                Value value = parseExpr(0);
                expectSymbol(")");
                return value;
            }
            throw error("unexpected token", token);
        }

        private Value parseCall(String fn) {
            String out = temp();

            switch (fn) {
                case "select" -> {
                    Value mask = parseExpr(0);
                    expectSymbol(",");
                    Value a = parseExpr(0);
                    expectSymbol(",");
                    Value b = parseExpr(0);
                    expectSymbol(")");
                    ops.add(new OpSpec("select",
                            List.of(materialize(mask), materialize(a), materialize(b)),
                            List.of(out)));
                    return Value.named(out);
                }
                case "filter" -> {
                    Value data = parseExpr(0);
                    expectSymbol(",");
                    Value mask = parseExpr(0);
                    expectSymbol(")");
                    ops.add(new OpSpec("filter",
                            List.of(materialize(data), materialize(mask)),
                            List.of(out)));
                    return Value.named(out);
                }
                case "dot" -> {
                    Value a = parseExpr(0);
                    expectSymbol(",");
                    Value b = parseExpr(0);
                    expectSymbol(")");
                    ops.add(new OpSpec("dot",
                            List.of(materialize(a), materialize(b)),
                            List.of(out)));
                    return Value.named(out);
                }
                case "cast" -> {
                    Value a = parseExpr(0);
                    expectSymbol(",");
                    Token dtype = expect(Kind.IDENT, "cast expects a dtype name");
                    expectSymbol(")");
                    ops.add(new OpSpec("cast", List.of(materialize(a)), List.of(out),
                            Map.of("dtype", dtype.text)));
                    return Value.named(out);
                }
                default -> {
                    // handled below
                }
            }

            if (!isUnary(fn)) {
                throw error("unknown function '" + fn + "'", peek());
            }
            Value a = parseExpr(0);
            expectSymbol(")");
            ops.add(new OpSpec(fn, List.of(materialize(a)), List.of(out)));
            return Value.named(out);
        }

        private Value makeBinary(Value a, Value b, String op) {
            if (a.literal && b.literal) {
                double x = a.number;
                double y = b.number;
                return switch (op) {
                    case "+" -> Value.lit(x + y);
                    case "-" -> Value.lit(x - y);
                    case "*" -> Value.lit(x * y);
                    case "/" -> Value.lit(y == 0.0 ? 0.0 : x / y);
                    case ">" -> Value.lit(x > y ? 1.0 : 0.0);
                    case ">=" -> Value.lit(x >= y ? 1.0 : 0.0);
                    case "<" -> Value.lit(x < y ? 1.0 : 0.0);
                    case "<=" -> Value.lit(x <= y ? 1.0 : 0.0);
                    case "==" -> Value.lit(x == y ? 1.0 : 0.0);
                    case "!=" -> Value.lit(x != y ? 1.0 : 0.0);
                    default -> throw new IllegalArgumentException("unknown operator " + op);
                };
            }

            boolean comparison = isComparison(op);
            if (comparison) {
                if (a.literal) {
                    return scalarOp(base(invert(op)), b, a.number);
                }
                if (b.literal) {
                    return scalarOp(base(op), a, b.number);
                }
                return binaryOp(base(op), a, b);
            }

            if (a.literal) {
                if (op.equals("+") || op.equals("*")) {
                    return scalarOp(base(op), b, a.number);
                }
                return binaryOp(base(op), Value.named(materialize(a)), b);
            }
            if (b.literal) {
                return scalarOp(base(op), a, b.number);
            }
            return binaryOp(base(op), a, b);
        }

        private Value binaryOp(String type, Value a, Value b) {
            String out = temp();
            ops.add(new OpSpec(type, List.of(a.name, b.name), List.of(out)));
            return Value.named(out);
        }

        private Value scalarOp(String type, Value a, double value) {
            String out = temp();
            ops.add(new OpSpec(type + "_scalar", List.of(a.name), List.of(out),
                    Map.of("scalar", format(value))));
            return Value.named(out);
        }

        private String materialize(Value value) {
            if (!value.literal) {
                return value.name;
            }
            String name = temp();
            ops.add(new OpSpec("const", List.of(), List.of(name),
                    Map.of("value", format(value.number), "size", "1")));
            return name;
        }

        private String temp() {
            return "@t" + tempCounter++;
        }

        private Token expect(Kind kind, String what) {
            Token token = advance();
            if (token.kind != kind) {
                throw error("expected " + what, token);
            }
            return token;
        }

        private void expectSymbol(String symbol) {
            Token token = advance();
            if (token.kind != Kind.SYMBOL || !token.text.equals(symbol)) {
                throw error("expected '" + symbol + "'", token);
            }
        }

        private Token peek() {
            return tokens.get(index);
        }

        private Token advance() {
            Token token = tokens.get(index);
            if (index < tokens.size() - 1) {
                index++;
            }
            return token;
        }

        private boolean atEnd() {
            return peek().kind == Kind.EOF;
        }

        private static int precedence(String op) {
            return switch (op) {
                case "*", "/" -> 10;
                case "+", "-" -> 5;
                case ">", ">=", "<", "<=", "==", "!=" -> 2;
                default -> -1;
            };
        }

        private static boolean isComparison(String op) {
            return precedence(op) == 2;
        }

        private static String base(String op) {
            return switch (op) {
                case "+" -> "add";
                case "-" -> "sub";
                case "*" -> "mul";
                case "/" -> "div";
                case ">" -> "gt";
                case ">=" -> "ge";
                case "<" -> "lt";
                case "<=" -> "le";
                case "==" -> "eq";
                case "!=" -> "neq";
                default -> throw new IllegalArgumentException("unknown operator " + op);
            };
        }

        private static String invert(String op) {
            return switch (op) {
                case ">" -> "<";
                case ">=" -> "<=";
                case "<" -> ">";
                case "<=" -> ">=";
                case "==" -> "==";
                case "!=" -> "!=";
                default -> throw new IllegalArgumentException("not a comparison " + op);
            };
        }

        private static boolean isUnary(String fn) {
            return switch (fn) {
                case "sum", "mean", "count", "max", "min", "stddev",
                        "abs", "neg", "square", "sqrt", "exp", "log",
                        "quantize", "dequantize" -> true;
                default -> false;
            };
        }

        private static String format(double value) {
            if (Double.isFinite(value) && value == Math.rint(value)
                    && Math.abs(value) < 9.0e18) {
                return Long.toString((long) value);
            }
            return Double.toString(value);
        }

        private static IllegalArgumentException error(String message, Token token) {
            return new IllegalArgumentException(
                    "column DSL: " + message + " at position " + token.position);
        }

        private static List<Token> tokenize(String input) {
            List<Token> tokens = new ArrayList<>();
            int i = 0;
            int length = input.length();

            while (i < length) {
                char c = input.charAt(i);
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                if (c == '#' || (c == '/' && i + 1 < length && input.charAt(i + 1) == '/')) {
                    while (i < length && input.charAt(i) != '\n') i++;
                    continue;
                }
                if (Character.isDigit(c) || (c == '.' && i + 1 < length
                        && Character.isDigit(input.charAt(i + 1)))) {
                    int start = i;
                    while (i < length && Character.isDigit(input.charAt(i))) i++;
                    if (i < length && input.charAt(i) == '.') {
                        i++;
                        while (i < length && Character.isDigit(input.charAt(i))) i++;
                    }
                    if (i < length && (input.charAt(i) == 'e' || input.charAt(i) == 'E')) {
                        i++;
                        if (i < length && (input.charAt(i) == '+' || input.charAt(i) == '-')) i++;
                        while (i < length && Character.isDigit(input.charAt(i))) i++;
                    }
                    String text = input.substring(start, i);
                    tokens.add(new Token(Kind.NUMBER, text, Double.parseDouble(text), start));
                    continue;
                }
                if (Character.isJavaIdentifierStart(c)) {
                    int start = i++;
                    while (i < length && Character.isJavaIdentifierPart(input.charAt(i))) i++;
                    tokens.add(new Token(Kind.IDENT, input.substring(start, i), 0.0, start));
                    continue;
                }

                if (i + 1 < length) {
                    String pair = input.substring(i, i + 2);
                    if (pair.equals(">=") || pair.equals("<=")
                            || pair.equals("==") || pair.equals("!=")) {
                        tokens.add(new Token(Kind.SYMBOL, pair, 0.0, i));
                        i += 2;
                        continue;
                    }
                }
                if ("+-*/=(),<>!".indexOf(c) >= 0) {
                    tokens.add(new Token(Kind.SYMBOL, String.valueOf(c), 0.0, i));
                    i++;
                    continue;
                }
                throw new IllegalArgumentException(
                        "column DSL: unexpected character '" + c + "' at position " + i);
            }

            tokens.add(new Token(Kind.EOF, "", 0.0, length));
            return tokens;
        }
    }
}
