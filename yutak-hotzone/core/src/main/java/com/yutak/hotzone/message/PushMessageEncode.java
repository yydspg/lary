package com.yutak.hotzone.message;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.entry.YutakPushMessage;
import com.yutak.hotzone.kryo.KryoComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PushMessageEncode extends MessageToByteEncoder<YutakPushMessage> {

    private final KryoComponent<YutakPushMessage> kryoComponent;


    public PushMessageEncode(KryoComponent<YutakPushMessage> component) {
        kryoComponent = component;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, YutakPushMessage yutakPushMessage, ByteBuf byteBuf) throws Exception {
        byte[] data = kryoComponent.serialize(yutakPushMessage);
        byte[] delimiter = YUTAK.DELIMITER;
        byte[] total = new byte[data.length + delimiter.length];
        System.arraycopy(data, 0, total, 0, data.length);
        System.arraycopy(delimiter, 0, total, data.length, delimiter.length);
        byteBuf.writeBytes(total);
    }
}
