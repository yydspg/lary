package cn.lary.payment.statemachine.impl;

import cn.lary.payment.statemachine.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StateHelper {

    public static <S, E, C> State<S, E, C> getState(Map<S, State<S, E, C>> stateMap, S stateId){
        return stateMap.computeIfAbsent(stateId, StateImpl::new);
    }


    public static <C, E, S> List<State<S,E,C>> getMultiState(Map<S, State<S,E,C>> stateMap, List<S> states) {
        List<State<S, E, C>> result = new ArrayList<>();
        for (S stateId : states) {
            State<S, E, C> state = getState(stateMap, stateId);
            result.add(state);
        }
        return result;
    }
}
