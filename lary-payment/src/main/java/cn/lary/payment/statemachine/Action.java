package cn.lary.payment.statemachine;

public interface Action<S, E, C> {

    void execute(S from, S to, E event, C context);
}
