package com.yutak.hotzone.client.handler;

import com.yutak.hotzone.entry.YutakEntry;

import java.util.List;

public class DefaultKeyPusher implements KeyPusher{

    private final NettyKeyPushHandler keyPushHandler;

    public DefaultKeyPusher() {
        this.keyPushHandler = new NettyKeyPushHandler();
    }
    @Override
    public void push(List<YutakEntry> data) {
        keyPushHandler.send(data);
    }
}
