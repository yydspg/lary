package com.yutak.hotzone.client.netty;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.client.worker.WorkerManager;
import com.yutak.hotzone.event.Subscriber;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ChannelInactiveSubscriber extends Subscriber<ChannelInactiveEvent> {

    private static final Logger log = LoggerFactory.getLogger(ChannelInactiveSubscriber.class);

    @Override
    public void process(ChannelInactiveEvent event) {
        Channel channel = event.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String address = socketAddress.getHostName() + ":" + socketAddress.getPort();
        log.warn("yutak : current channel inactive address:{},remove the connection",address);
        WorkerManager.unregister(address);
    }

    @Override
    public int getIdentify() {
        return YUTAK.CHANNEL_INACTIVE;
    }

}
