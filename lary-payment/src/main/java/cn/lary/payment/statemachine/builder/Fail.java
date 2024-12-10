package cn.lary.payment.statemachine.builder;

@FunctionalInterface
public interface Fail<S,E,C>{

    void when(S state, E event, C ctx);
}
