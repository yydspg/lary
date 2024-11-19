package cn.lary.module.engine.schedule.auxiliary;

public interface TimingStrategy {

    void validate(String time);

    Long calculateNextTriggerTime(Long preTriggerTime, String timeExpression, Long startTime, Long endTime);


}
