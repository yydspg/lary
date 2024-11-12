package com.yutak.hotzone.client.handler;

public class KeyHandlerFactory {

    private final static KeyCollector collector = new DefaultKeyCollector();
    private final static KeyPusher pusher = new DefaultKeyPusher();

    public static KeyCollector getKeyCollector() {
        return collector;
    }

    public static KeyPusher getKeyPusher() {
        return pusher;
    }
}
