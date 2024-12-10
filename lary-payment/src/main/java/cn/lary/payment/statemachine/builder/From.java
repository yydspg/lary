package cn.lary.payment.statemachine.builder;

public interface From<S, E, C> {

    To<S, E, C> to(S state);

}
