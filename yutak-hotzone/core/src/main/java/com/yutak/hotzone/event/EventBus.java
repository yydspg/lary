package com.yutak.hotzone.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EventBus {

    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    private final static Map<Integer,Subscriber> reactor = new HashMap<>();

    private final  static  EventBus INSTANCE = new EventBus();

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public static void  register(List<Subscriber<?>> subscribers) {
            if (subscribers == null || subscribers.isEmpty()) {
                log.info("yutak event bus subscribers empty");
                return;
            }
            subscribers.forEach(subscriber -> {
                subscriber.register(reactor);
            });
    }

    public final <T extends Event> void send(T event) {
        CompletableFuture.runAsync(() -> {
            reactor.get(event.getIdentify()).process(event);
        });
    }
}
