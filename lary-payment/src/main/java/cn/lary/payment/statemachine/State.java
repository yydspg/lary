package cn.lary.payment.statemachine;

import java.util.Collection;
import java.util.List;

public interface  State<S,E,C> extends Visitable {

    S specify();

    Transition<S,E,C> addTransition(E event, State<S, E, C> target, TransitionCategory category);

    Collection<Transition<S,E,C>> addTransitions(E event, Collection<State<S, E, C>> targets, TransitionCategory category);

    Collection<Transition<S,E,C>> getEventTransitions(E event);

    Collection<Transition<S,E,C>> getAllTransitions();
}
