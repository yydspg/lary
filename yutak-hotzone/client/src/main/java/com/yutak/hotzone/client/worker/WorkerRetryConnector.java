package com.yutak.hotzone.client.worker;


import com.yutak.hotzone.client.netty.ChannelInactiveSubscriber;
import com.yutak.hotzone.client.netty.YutakNettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WorkerRetryConnector {

    private static final Logger log = LoggerFactory.getLogger(ChannelInactiveSubscriber.class);
    public static void retry(){
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        thread.setDaemon(true);
                        thread.setName("yutak hotzone client retry");
                        return thread;
                    }
                })
                .scheduleAtFixedRate(WorkerRetryConnector::retry,30,30, TimeUnit.SECONDS);
    }

    private static void reconnect(){
        List<String> nonConnectedWorkers = WorkerManager.getNonConnectedWorkers();
        if(nonConnectedWorkers.isEmpty()){
            return ;
        }
        log.info("Reconnecting to {} workers", nonConnectedWorkers.size());
        YutakNettyClient.getInstance().connect(nonConnectedWorkers);
    }
}
