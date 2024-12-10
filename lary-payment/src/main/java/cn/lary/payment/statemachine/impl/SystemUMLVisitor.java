package cn.lary.payment.statemachine.impl;

import cn.lary.payment.statemachine.State;
import cn.lary.payment.statemachine.StateMachine;
import cn.lary.payment.statemachine.Visitor;

public class SystemUMLVisitor implements Visitor {
    @Override
    public String visitOnEntry(StateMachine<?, ?, ?> visitable) {
        return "";
    }

    @Override
    public String visitOnExit(StateMachine<?, ?, ?> visitable) {
        return "";
    }

    @Override
    public String visitOnEntry(State<?, ?, ?> visitable) {
        return "";
    }

    @Override
    public String visitOnExit(State<?, ?, ?> visitable) {
        return "";
    }
}
