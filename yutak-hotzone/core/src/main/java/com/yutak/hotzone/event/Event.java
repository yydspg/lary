package com.yutak.hotzone.event;

public abstract class Event {

    public int getIdentify() {
        return identify;
    }

    public void setIdentify(int identify) {
        this.identify = identify;
    }

    private int identify;
}
