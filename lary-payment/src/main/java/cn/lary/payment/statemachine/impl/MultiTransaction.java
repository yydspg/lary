package cn.lary.payment.statemachine.impl;

import cn.lary.payment.statemachine.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MultiTransaction<S,E,C> {

    private final HashMap<E, List<Transition<S,E,C>>> transitions;

    public MultiTransaction() {
        this.transitions = new HashMap<>();
    }

    public final void add(E event,Transition<S,E,C> transition) {
        transitions.getOrDefault(event, new ArrayList<>()).add(transition);
    }

    public final List<Transition<S,E,C>> get(E event) {
        return this.transitions.get(event);
    }

    public final List<Transition<S,E,C>> getAll() {
        return transitions.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
