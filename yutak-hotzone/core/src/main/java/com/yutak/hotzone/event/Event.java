package com.yutak.hotzone.event;

public abstract class Event {

    public int getIdentify() {
        return identify;
    }

    public void setIdentify(int identify) {
        this.identify = identify;
    }

    private int identify;

    public static void main(String[] args) {
        String event = new String("lary:event:channel:189005123424239401");
        System.out.println(event.getBytes().length);
    }
}
