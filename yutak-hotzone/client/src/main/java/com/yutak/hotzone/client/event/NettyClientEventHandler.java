package com.yutak.hotzone.client.event;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.client.callback.EntryLocalEvent;
import com.yutak.hotzone.client.callback.EntryRejectedEvent;
import com.yutak.hotzone.entry.YutakProcessMessage;
import com.yutak.hotzone.event.EventBus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientEventHandler extends SimpleChannelInboundHandler<YutakProcessMessage> {

    private static final Logger log = LoggerFactory.getLogger(NettyClientEventHandler.class);

    public NettyClientEventHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, YutakProcessMessage message) throws Exception {
        int category = message.getCategory();
        log.info("received process message: {}", message);
        switch (category) {
            case YUTAK.LOCAL : EventBus.getInstance().send(new EntryLocalEvent(message.getK(), message.getV()));
            case YUTAK.REJECTED: EventBus.getInstance().send(new EntryRejectedEvent(message.getK(), message.getV()));
            default: break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("yutak hotzone worker channel {} active",ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("yutak hotzone worker channel {} inactive",ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

}
