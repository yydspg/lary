package cn.lary.payment.statemachine;

public interface Visitable {

    String accept(final Visitor visitor);
}
