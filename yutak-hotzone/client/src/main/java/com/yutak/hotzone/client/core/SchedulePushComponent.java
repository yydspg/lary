package com.yutak.hotzone.client.core;

import com.yutak.hotzone.client.handler.KeyHandlerFactory;
import com.yutak.hotzone.entry.YutakEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchedulePushComponent {

    private final static AtomicBoolean done = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(SchedulePushComponent.class);


    public synchronized static void start(Long period){
        if (!done.get()) {
            done.set(true);
            if (period == null || period <= 0) {
                period = 500L;
            }

            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setName("yutak-hotzone-client-scheduled-thread");
                    return thread;
                }
            });
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                List<YutakEntry> data = KeyHandlerFactory.getKeyCollector().exec();
                KeyHandlerFactory.getKeyPusher().push(data);
                log.info("send");
            },1000L, period, TimeUnit.MILLISECONDS);
        }
    }
}
