package cn.lary.payment.statemachine.builder;

import cn.lary.payment.statemachine.State;
import cn.lary.payment.statemachine.TransitionCategory;
import cn.lary.payment.statemachine.impl.StateHelper;

import java.util.List;
import java.util.Map;

public abstract class AbstractBuilder<S,E,C> implements From<S,E,C>,On<S,E,C>,To<S,E,C>{
    final Map<S, State<S, E, C>> stateMap;

    protected State<S, E, C> target;

    final TransitionCategory category;

    public AbstractBuilder(Map<S,State<S,E,C>> map,TransitionCategory  category) {
        this.stateMap = map;
        this.category = category;
    }

    @Override
    public To<S, E, C> to(S state) {
        target = StateHelper.getState(stateMap, state);
        return this;
    }
}
