package com.yutak.hotzone.message;

import com.yutak.hotzone.entry.YutakProcessMessage;
import com.yutak.hotzone.kryo.KryoComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProcessorMessageDecode extends ByteToMessageDecoder {
    private final KryoComponent<YutakProcessMessage> kryoComponent;

    public ProcessorMessageDecode(KryoComponent<YutakProcessMessage> component) {
        kryoComponent = component;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] body = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(body);
        list.add(kryoComponent.deserialize(body));
    }
}
