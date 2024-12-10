package cn.lary.payment.statemachine.impl;

import cn.lary.common.kit.CollectionKit;
import cn.lary.payment.statemachine.State;
import cn.lary.payment.statemachine.StateMachine;
import cn.lary.payment.statemachine.Transition;
import cn.lary.payment.statemachine.Visitor;
import cn.lary.payment.statemachine.builder.Fail;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class StateMachineImpl<S, E, C> implements StateMachine<S, E, C> {

    @Setter
    private String specify;

    private final Map<S, State<S,E,C>> stateMap;

    private Fail<S,E,C> callback;

    public StateMachineImpl(Map<S,State<S,E,C>> map){
        this.stateMap = map;
    }

    @Override
    public boolean verify(S source, E event) {
        State<S, E, C> state = getState(source);
        return CollectionKit.isEmpty(state.getEventTransitions(event));
    }

    @Override
    public S send(S source, E event, C ctx) {
        Transition<S, E, C> transaction = routeTransition(source, event, ctx);
        if(transaction == null){
            callback.when(source, event, ctx);
            return source;
        }
        return transaction.transit(ctx,false).specify();
    }

    @Override
    public Collection<S> multiSend(S source, E event, C ctx) {
        List<Transition<S, E, C>> transitions = routeMultiTransition(source, event, ctx);
        List<S> result = new ArrayList<>();
        if(CollectionKit.isEmpty(transitions)){
            callback.when(source, event, ctx);
            result.add(source);
            return result;
        }
        result = transitions.stream()
                .map(t->t.transit(ctx,false).specify())
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public String view() {
        SystemUMLVisitor visitor = new SystemUMLVisitor();
        return accept(visitor);
    }


    @Override
    public String buildUML() {
        PlantUMLVisitor visitor = new PlantUMLVisitor();
        return accept(visitor);
    }

    @Override
    public String specify() {
          return specify;
    }

    @Override
    public String accept(Visitor visitor) {
        StringBuilder sb = new StringBuilder();
        sb.append(visitor.visitOnEntry(this));
        for (State state : stateMap.values()) {
            sb.append(state.accept(visitor));
        }
        sb.append(visitor.visitOnExit(this));
        return sb.toString();
    }
    private Transition<S,E,C> routeTransition(S source,E event,C ctx){
        State<S, E, C> state = getState(source);
        Collection<Transition<S, E, C>> transitions = state.getEventTransitions(event);
        if(CollectionKit.isEmpty(transitions)){
            return null;
        }
        Transition<S,E,C> result = null;
        for(Transition<S, E, C> transition : transitions){
            if (transition.getCondition() == null) {
                result = transition;
            } else if (transition.getCondition().satisfy(ctx)) {
                result = transition;
                break;
            }
        }
        return result;
    }
    private List<Transition<S,E,C>> routeMultiTransition(S source,E event,C ctx){
        State<S, E, C> state = getState(source);
        Collection<Transition<S, E, C>> transitions = state.getEventTransitions(event);
        if(CollectionKit.isEmpty(transitions)){
            return null;
        }
        List<Transition<S, E, C>> result = new ArrayList<>();
        for(Transition<S, E, C> transition : transitions){
            Transition<S, E, C> transit = null;
            if (transition.getCondition() == null) {
                transit = transition;
            } else if (transition.getCondition().satisfy(ctx)) {
                transit = transition;
            }
            result.add(transit);
        }
        return result;
    }
    private State<S,E,C> getState(S source){
        return StateHelper.getState(stateMap, source);
    }
}
