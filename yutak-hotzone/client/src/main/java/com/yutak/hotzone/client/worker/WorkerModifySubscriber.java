package com.yutak.hotzone.client.worker;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.event.Subscriber;

public class WorkerModifySubscriber extends Subscriber<WorkerModifyEvent> {


    @Override
    public void process(WorkerModifyEvent event) {

    }
    @Override
    public int getIdentify() {
        return YUTAK.WORKER_MODIFY;
    }
}
