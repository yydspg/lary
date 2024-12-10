package cn.lary.payment.statemachine;

public interface StateContext<S, E, C> {
    Transition<S, E, C> getTransition();
}
