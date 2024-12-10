package cn.lary.payment.statemachine.impl;

import cn.lary.payment.statemachine.*;
import cn.lary.payment.statemachine.exception.StateMachineException;

public class  TransitionImpl<S,E,C> implements Transition<S,E,C> {

    private State<S, E, C> source;

    private State<S, E, C> target;

    private E event;

    private Condition<C> condition;

    private Action<S,E,C> action;

    private TransitionCategory category = TransitionCategory.EXTERNAL;

    @Override
    public State<S, E, C> getSource() {
        return source;
    }

    @Override
    public void setSource(State<S, E, C> state) {
        this.source = state;
    }

    @Override
    public E getEvent() {
        return this.event;
    }

    @Override
    public void setEvent(E event) {
        this.event = event;
    }

    @Override
    public State<S, E, C> getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(State<S, E, C> state) {
        this.target = state;
    }

    @Override
    public Condition<C> getCondition() {
        return this.condition;
    }

    @Override
    public void setCondition(Condition<C> condition) {
        this.condition = condition;
    }

    @Override
    public Action<S, E, C> getAction() {
        return this.action;
    }

    @Override
    public void setAction(Action<S, E, C> action) {
        this.action = action;
    }

    @Override
    public State<S, E, C> transit(C ctx, boolean checkCondition) {
        verify();
        if (!checkCondition || condition == null || condition.satisfy(ctx)) {
            if(action != null){
                action.execute(source.specify(), target.specify(), event, ctx);
            }
            return target;
        }
        return source;
    }

    @Override
    public void verify() {
        if(category == TransitionCategory.INTERNAL && source != target) {
            throw new StateMachineException(String.format("Internal transition source state '%s' " +
                    "and target state '%s' must be same.", source, target));
        }
    }

    @Override
    public void setCategory(TransitionCategory category) {
        this.category = category;
    }
    @Override
    public final String toString() {
        return source + "-[" + event.toString() +", "+category+"]->" + target;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof Transition){
            Transition next = (Transition)other;
            if(this.event.equals(next.getEvent())
                    && this.source.equals(next.getSource())
                    && this.target.equals(next.getTarget())){
                return true;
            }
        }
        return false;
    }
}
