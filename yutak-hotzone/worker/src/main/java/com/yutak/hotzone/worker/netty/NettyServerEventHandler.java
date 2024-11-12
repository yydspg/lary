package com.yutak.hotzone.worker.netty;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.entry.YutakProcessMessage;
import com.yutak.hotzone.entry.YutakPushMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerEventHandler extends SimpleChannelInboundHandler<YutakPushMessage> {


    private static final Logger log = LoggerFactory.getLogger(NettyServerEventHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, YutakPushMessage message) throws Exception {
        switch (message.getCategory()) {
            case  YUTAK.PING :

        }
    }
    private void doPong(ChannelHandlerContext ctx){
        YutakProcessMessage message = new YutakProcessMessage();
        message.setCategory(YUTAK.PONG);
        ctx.writeAndFlush(message);
    }
    private void doPush(ChannelHandlerContext ctx,YutakPushMessage message){

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("yutak hotzone client channel active");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("yutak hotzone client channel inactive");
        super.channelInactive(ctx);
    }

}
