package cn.lary.payment.statemachine.builder;

public interface ExternalBuilder<S, E, C>  {
    From<S, E, C> from(S state);
}
