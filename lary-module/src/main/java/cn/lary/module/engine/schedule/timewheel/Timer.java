package cn.lary.module.engine.schedule.timewheel;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {
    /**
     * 调度定时任务
     */
    TimeFuture schedule(TimeTask task, long delay, TimeUnit unit);

    /**
     * 停止所有调度任务
     */
    Set<TimeTask> stop();
}
