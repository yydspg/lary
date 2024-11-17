package com.yutak.hotzone.client.worker;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.event.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerModifySubscriber extends Subscriber<WorkerModifyEvent> {


    private static final Logger log = LoggerFactory.getLogger(WorkerModifySubscriber.class);

    @Override
    public void process(WorkerModifyEvent event) {
        log.info("yutak : worker modify subscriber process event: {}", event);

    }
    @Override
    public int getIdentify() {
        return YUTAK.WORKER_MODIFY;
    }
}
