package cn.lary.payment.statemachine.impl;

import cn.lary.payment.statemachine.State;
import cn.lary.payment.statemachine.StateMachine;
import cn.lary.payment.statemachine.Transition;
import cn.lary.payment.statemachine.Visitor;

public class PlantUMLVisitor implements Visitor {

    @Override
    public String visitOnEntry(StateMachine<?, ?, ?> visitable) {
        return "@startuml" + LF;
    }

    @Override
    public String visitOnExit(StateMachine<?, ?, ?> visitable) {
        return "@enduml";
    }

    @Override
    public String visitOnEntry(State<?, ?, ?> state) {
        StringBuilder sb = new StringBuilder();
        for(Transition transition: state.getAllTransitions()){
            sb.append(transition.getSource().specify())
                    .append(" --> ")
                    .append(transition.getTarget().specify())
                    .append(" : ")
                    .append(transition.getEvent())
                    .append(LF);
        }
        return sb.toString();
    }

    @Override
    public String visitOnExit(State<?, ?, ?> visitable) {
        return "";
    }
}
