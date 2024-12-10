package cn.lary.payment.statemachine.builder;

import java.util.List;

public interface MultiFrom<S, E, C> {

    To<S, E, C> mulitTo(List<S> states);

}
