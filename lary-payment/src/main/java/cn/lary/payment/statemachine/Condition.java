package cn.lary.payment.statemachine;

public interface Condition<C> {

    boolean satisfy(C ctx);
}
