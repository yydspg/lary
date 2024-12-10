package cn.lary.payment.statemachine.builder;

import java.util.List;

public interface ExternalMultiBuilder<S, E, C>  {

    From<S, E, C> multiFrom(List<S> states);
}
