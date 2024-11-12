package com.yutak.hotzone.client.subscriber;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.client.callback.EntryLocalEvent;
import com.yutak.hotzone.event.Subscriber;

public class EntryLocalSubscriber extends Subscriber<EntryLocalEvent> {


    @Override
    public int getIdentify() {
        return YUTAK.ENTRY_LOCAL;
    }

    @Override
    public void process(EntryLocalEvent event) {

    }

}
