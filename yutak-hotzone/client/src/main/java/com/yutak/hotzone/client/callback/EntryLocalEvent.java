package com.yutak.hotzone.client.callback;


import com.yutak.hotzone.event.Event;


public class EntryLocalEvent extends Event {

    private final String K;
    private final String V;

    public EntryLocalEvent(String k,String v) {
        this.K = k;
        this.V = v;
    }

    public String getV() {
        return V;
    }

    public String getK() {
        return K;
    }


}
