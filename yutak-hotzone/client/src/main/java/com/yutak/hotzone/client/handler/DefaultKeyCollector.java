package com.yutak.hotzone.client.handler;

import com.yutak.hotzone.client.core.CollectComponent;
import com.yutak.hotzone.entry.YutakEntry;

import java.util.List;

public class DefaultKeyCollector implements KeyCollector {

    private final CollectComponent collectComponent = new CollectComponent();

    @Override
    public final List<YutakEntry> exec() {
        return collectComponent.read();
    }
    @Override
    public final void put(YutakEntry entry) {
        collectComponent.send(entry);
    }
}
