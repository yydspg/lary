package cn.lary.payment.statemachine.builder;

import cn.lary.payment.statemachine.State;
import cn.lary.payment.statemachine.TransitionCategory;
import cn.lary.payment.statemachine.impl.StateHelper;

import java.util.List;
import java.util.Map;

public abstract class AbstractMultiBuilder<S,E,C> implements MultiFrom<S,E,C> ,On<S,E,C>,To<S,E,C> {
    final Map<S, State<S, E, C>> stateMap;

    protected List<State<S, E, C>> targets;

    final TransitionCategory category;

    public AbstractMultiBuilder(Map<S, State<S, E, C>> stateMap, TransitionCategory category) {
        this.stateMap = stateMap;
        this.category = category;
    }

    @Override
    public To<S, E, C> mulitTo(List<S> states) {
        targets = StateHelper.getMultiState(stateMap, states);
        return this;
    }

}
