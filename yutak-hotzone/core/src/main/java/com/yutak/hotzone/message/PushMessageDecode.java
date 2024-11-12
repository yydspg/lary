package com.yutak.hotzone.message;

import com.yutak.hotzone.entry.YutakPushMessage;
import com.yutak.hotzone.kryo.KryoComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PushMessageDecode extends ByteToMessageDecoder {

    private final KryoComponent<YutakPushMessage> kryoComponent;

    public PushMessageDecode(KryoComponent<YutakPushMessage> component) {
        kryoComponent = component;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)  {

        byte[] body = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(body);
        list.add(kryoComponent.deserialize(body));
    }
}
