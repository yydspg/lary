package cn.lary.payment.statemachine;

public interface Visitor {

    char LF = '\n';

    String visitOnEntry(StateMachine<?, ?, ?> visitable);


    String visitOnExit(StateMachine<?, ?, ?> visitable);


    String visitOnEntry(State<?, ?, ?> visitable);


    String visitOnExit(State<?, ?, ?> visitable);
}
