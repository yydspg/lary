package com.yutak.hotzone.worker.remote;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RemoteManager {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    public void groupPush(Object object) {
        channels.writeAndFlush(object);
    }

    public void add(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
    }

    public void remove(ChannelHandlerContext ctx) {
        channels.remove(ctx.channel());
    }


    public int size() {
        return channels.size();
    }
}
