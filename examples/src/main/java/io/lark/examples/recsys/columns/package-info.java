/**
 * Typed column contracts for the recsys example.
 *
 * <p>These classes are <strong>ports</strong>, not feed nodes: they only define
 * a column's name and dtype. The behavior that produces a column lives in
 * {@code io.lark.examples.recsys.node} (for example {@code CtrNode.compute}).
 * Keeping the contract separate from the computation lets the same metric type
 * be reused by different sources or time windows, and keeps node lifecycle
 * (dependency wiring, timeout, fallback) out of a value class.
 */
package io.lark.examples.recsys.columns;
