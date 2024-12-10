package cn.lary.payment.statemachine;

import java.util.Collection;

public interface StateMachine<S, E, C> extends Visitable {

    boolean verify(S source,E event);

    S send(S source,E event,C ctx);

    Collection<S> multiSend(S source,E event,C ctx);

    String view();

    String buildUML();

    String specify();
}
