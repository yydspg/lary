package com.yutak.hotzone.client.callback;


import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.event.Event;

public class EntryRejectedEvent extends Event {

    private final String K;
    private final String V;

    public EntryRejectedEvent(String k,String v) {
        K = k;
        V = v;
        setIdentify(YUTAK.ENTRY_REJECTED);
    }

    public String getK() {
        return K;
    }

    public String getV() {
        return V;
    }

}
