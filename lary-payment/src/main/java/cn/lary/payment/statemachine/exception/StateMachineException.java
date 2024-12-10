package cn.lary.payment.statemachine.exception;

public class StateMachineException extends RuntimeException{
    public StateMachineException(String message){
        super(message);
    }
}
