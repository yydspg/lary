package cn.lary.payment.statemachine.builder;

public interface InternalBuilder <S, E, C> {

    To<S, E, C> within(S state);
}
