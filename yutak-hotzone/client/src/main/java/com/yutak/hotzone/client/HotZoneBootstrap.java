package com.yutak.hotzone.client;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.client.core.CollectComponent;
import com.yutak.hotzone.client.core.SchedulePushComponent;

import com.yutak.hotzone.client.handler.DefaultKeyCollector;
import com.yutak.hotzone.client.handler.KeyHandlerFactory;
import com.yutak.hotzone.client.netty.YutakNettyClient;
import com.yutak.hotzone.client.subscriber.EntryLocalSubscriber;
import com.yutak.hotzone.client.subscriber.EntryRejectedSubscriber;
import com.yutak.hotzone.entry.YutakEntry;
import com.yutak.hotzone.event.EventBus;
import com.yutak.hotzone.event.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class HotZoneBootstrap {

    private static final Logger log = LoggerFactory.getLogger(HotZoneBootstrap.class);

    public Long getPushPeriod() {
        return pushPeriod;
    }

    public void setPushPeriod(Long pushPeriod) {
        this.pushPeriod = pushPeriod;
    }

    private Long pushPeriod;

    private HotZoneBootstrap() {

    }

    public static class Builder {
        public Long getPushPeriod() {
            return pushPeriod;
        }

        public Builder setPushPeriod(Long pushPeriod) {
            this.pushPeriod = pushPeriod;
            return this;
        }

        private Long pushPeriod;
        public Builder (){}

        public HotZoneBootstrap build(){
            HotZoneBootstrap bootstrap = new HotZoneBootstrap();
            bootstrap.pushPeriod = pushPeriod;
            return bootstrap;
        }


    }
    public  final void start(){
        log.info("yutak hotzone bootstrap start");
        YutakNettyClient instance = YutakNettyClient.getInstance();
        instance.connect(List.of("127.0.0.1:9001"));
        SchedulePushComponent.start(pushPeriod);
        register();
    }
    private void register(){
        List<Subscriber<?>> subscribers = new ArrayList<>();
        subscribers.add(new EntryLocalSubscriber());
        subscribers.add(new EntryRejectedSubscriber());
        EventBus.register(subscribers);
    }
    public static void main(String[] args) {
        HotZoneBootstrap strap = new Builder()
                .setPushPeriod(5000L)
                .build();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        for (int i = 0; i < 2056; i++) {
            YutakEntry entry = new YutakEntry();
            entry.setK("dddd");
            entry.setPolicy((byte)1);
            KeyHandlerFactory.getKeyCollector().put(new YutakEntry());
        }
        log.info("send data");
        executor.scheduleAtFixedRate(()->{

        },1,1, TimeUnit.SECONDS);

        strap.start();
    }
}
