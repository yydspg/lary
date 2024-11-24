package cn.lary.engine.schedule.timewheel;

public interface TimeFuture {

    TimeTask getTask();

    boolean cancel();

    boolean isCancelled();


    boolean isDone();
}
