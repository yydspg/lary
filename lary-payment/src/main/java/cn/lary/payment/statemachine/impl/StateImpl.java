package cn.lary.payment.statemachine.impl;

import cn.lary.payment.statemachine.State;
import cn.lary.payment.statemachine.Transition;
import cn.lary.payment.statemachine.TransitionCategory;
import cn.lary.payment.statemachine.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StateImpl<S,E,C> implements State<S,E,C> {

    private final S state;

    private final MultiTransaction<S,E,C> multiTransaction  = new MultiTransaction<>();

    public StateImpl(S state) {
        this.state = state;
    }
    @Override
    public S specify() {
        return state;
    }

    @Override
    public Transition<S, E, C> addTransition(E event, State<S, E, C> target, TransitionCategory category) {
        Transition<S,E,C> one = new TransitionImpl<>();
        one.setSource(this);
        one.setEvent(event);
        one.setCategory(category);
        one.setTarget(target);
        multiTransaction.add(event,one);
        return one;
    }

    @Override
    public Collection<Transition<S, E, C>> addTransitions(E event, Collection<State<S, E, C>> targets, TransitionCategory category) {
        List<Transition<S, E, C>> result = new ArrayList<>();
        for (State<S, E, C> target : targets) {
            Transition<S, E, C> secTransition = addTransition(event, target, category);
            result.add(secTransition);
        }

        return result;
    }

    @Override
    public Collection<Transition<S, E, C>> getEventTransitions(E event) {
        return multiTransaction.get(event);
    }

    @Override
    public Collection<Transition<S, E, C>> getAllTransitions() {
        return List.of();
    }

    @Override
    public String accept(Visitor visitor) {
        String entry = visitor.visitOnEntry(this);
        String exit = visitor.visitOnExit(this);
        return entry + exit;
    }
    @Override
    public boolean equals(Object anObject){
        if(anObject instanceof State){
            State other = (State)anObject;
            if(this.state.equals(other.specify()))
                return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return state.toString();
    }
}
