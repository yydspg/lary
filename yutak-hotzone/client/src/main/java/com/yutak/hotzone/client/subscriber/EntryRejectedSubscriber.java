package com.yutak.hotzone.client.subscriber;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.client.callback.EntryRejectedEvent;
import com.yutak.hotzone.event.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EntryRejectedSubscriber extends Subscriber<EntryRejectedEvent> {

    private static final Logger log = LoggerFactory.getLogger(EntryRejectedSubscriber.class);

    public EntryRejectedSubscriber() {

    }

    @Override
    public int getIdentify() {
        return YUTAK.ENTRY_REJECTED;
    }

    @Override
    public void process(EntryRejectedEvent event) {

    }

}
