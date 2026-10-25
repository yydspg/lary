// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiny tokenizer/parser for the op graph DSL:
 *
 * <pre>{@code
 * fetch:DataFetchOp -> select:SelectOp -> rank:RankOp
 * threshold:ConstOp  -> rank:RankOp
 * }</pre>
 *
 * <p>Statements are separated by {@code ;} or a new line. Reusing a node id
 * merges its dependencies (fan-in). {@code #} and {@code //} start a comment.
 */
final class DslParser {

    record Segment(String id, String type) {
    }

    private enum Kind {
        IDENT, SYMBOL, EOF
    }

    private record Token(Kind kind, String text, int position) {
    }

    private final List<Token> tokens;
    private int index;

    DslParser(String input) {
        this.tokens = tokenize(input);
    }

    boolean atEnd() {
        return peek().kind() == Kind.EOF;
    }

    boolean matchSymbol(String symbol) {
        Token token = peek();
        if (token.kind() == Kind.SYMBOL && token.text().equals(symbol)) {
            index++;
            return true;
        }
        return false;
    }

    /** Parse {@code id} or {@code id:Type}. A bare name means id == type. */
    Segment segment() {
        Token id = expectIdent("identifier");
        if (matchSymbol(":")) {
            Token type = expectIdent("type identifier");
            return new Segment(id.text(), type.text());
        }
        return new Segment(id.text(), id.text());
    }

    private Token expectIdent(String what) {
        Token token = peek();
        if (token.kind() != Kind.IDENT) {
            throw error("expected " + what, token);
        }
        index++;
        return token;
    }

    private Token peek() {
        return tokens.get(index);
    }

    private static IllegalArgumentException error(String message, Token token) {
        return new IllegalArgumentException(
                "Graph DSL: " + message + " at position " + token.position());
    }

    private static List<Token> tokenize(String input) {
        List<Token> result = new ArrayList<>();
        int i = 0;
        int length = input.length();

        while (i < length) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            if (c == '#' || (c == '/' && i + 1 < length && input.charAt(i + 1) == '/')) {
                while (i < length && input.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }
            if (c == '-' && i + 1 < length && input.charAt(i + 1) == '>') {
                result.add(new Token(Kind.SYMBOL, "->", i));
                i += 2;
                continue;
            }
            if (c == ':' || c == ';') {
                result.add(new Token(Kind.SYMBOL, String.valueOf(c), i));
                i++;
                continue;
            }
            if (Character.isJavaIdentifierStart(c)) {
                int start = i++;
                while (i < length && Character.isJavaIdentifierPart(input.charAt(i))) {
                    i++;
                }
                result.add(new Token(Kind.IDENT, input.substring(start, i), start));
                continue;
            }

            throw new IllegalArgumentException(
                    "Graph DSL: unexpected character '" + c + "' at position " + i);
        }

        result.add(new Token(Kind.EOF, "", length));
        return result;
    }
}
