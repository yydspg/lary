package cn.lary.payment.statemachine.builder;

public interface To<S, E, C> {

    On<S, E, C> on(E event);
}