package com.yutak.hotzone.client.worker;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.event.Event;

import java.util.List;

public class WorkerModifyEvent extends Event {

    private List<String> addresses;

    public WorkerModifyEvent() {
        setIdentify(YUTAK.WORKER_MODIFY);
    }
    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

}
