package cn.lary.module.id;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SystemClock {

    private final int period;
    private final AtomicLong now;

    private static final String THREAD_NAME ="wienerClock";

    private static class InstanceHolder {
        private static final SystemClock INSTANCE = new SystemClock(1);
    }

    private SystemClock(int period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 供消费者调用，以替换原来的System.currentTimeMillis()
     */
    public static long now() {
        return instance().now.get();
    }
    private void scheduleClockUpdating() {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        // 每毫秒获取一次系统时间，并赋值给 AtomicLong now
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

}
