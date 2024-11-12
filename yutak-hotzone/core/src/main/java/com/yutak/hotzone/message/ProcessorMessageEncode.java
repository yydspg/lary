package com.yutak.hotzone.message;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.entry.YutakProcessMessage;
import com.yutak.hotzone.kryo.KryoComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProcessorMessageEncode extends MessageToByteEncoder<YutakProcessMessage> {

    private final KryoComponent<YutakProcessMessage> kryoComponent;
    public ProcessorMessageEncode(KryoComponent<YutakProcessMessage> component) {
        this.kryoComponent = component;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, YutakProcessMessage yutakProcessMessage, ByteBuf byteBuf) throws Exception {
        byte[] data = kryoComponent.serialize(yutakProcessMessage);
        byte[] delimiter = YUTAK.DELIMITER;
        byte[] total = new byte[data.length + delimiter.length];
        System.arraycopy(data, 0, total, 0, data.length);
        System.arraycopy(delimiter, 0, total, data.length, delimiter.length);
        byteBuf.writeBytes(total);
    }
}
