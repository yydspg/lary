package com.yutak.hotzone.client.netty;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.event.Event;
import io.netty.channel.Channel;

public class ChannelInactiveEvent extends Event {

    private Channel channel;

    public ChannelInactiveEvent(Channel channel) {
        this.channel = channel;
        setIdentify(YUTAK.CHANNEL_INACTIVE);
    }
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
