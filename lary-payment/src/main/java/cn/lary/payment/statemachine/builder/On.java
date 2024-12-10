package cn.lary.payment.statemachine.builder;

import cn.lary.payment.statemachine.Condition;

public interface On<S, E, C> extends When<S, E, C>{

    When<S, E, C> when(Condition<C> condition);
}
