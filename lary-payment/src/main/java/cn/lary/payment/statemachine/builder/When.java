package cn.lary.payment.statemachine.builder;

import cn.lary.payment.statemachine.Action;

public interface When<S, E, C>{

    void perform(Action<S, E, C> action);
}