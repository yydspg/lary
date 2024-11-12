package com.yutak.hotzone.client.handler;

import com.yutak.hotzone.entry.YutakEntry;

import java.util.List;

public interface KeyPusher {

    void push(List<YutakEntry> data);

}
