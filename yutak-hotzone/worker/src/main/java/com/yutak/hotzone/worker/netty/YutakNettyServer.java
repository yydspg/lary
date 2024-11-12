package com.yutak.hotzone.worker.netty;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.entry.YutakProcessMessage;
import com.yutak.hotzone.entry.YutakPushMessage;
import com.yutak.hotzone.kryo.KryoComponent;
import com.yutak.hotzone.message.ProcessorMessageEncode;
import com.yutak.hotzone.message.PushMessageDecode;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class YutakNettyServer {

    private final static int MAX_LENGTH = 2 *1024;
    public Bootstrap build() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(3);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return null;
    }

    private class YutakChannelHandler extends ChannelInitializer<Channel> {
        private final KryoComponent<YutakProcessMessage> processMessageComponent;
        private final KryoComponent<YutakPushMessage> pushMessageComponent;

        public YutakChannelHandler(){
            processMessageComponent = new KryoComponent<>(YutakProcessMessage.class);
            pushMessageComponent = new KryoComponent<>(YutakPushMessage.class);
        }

        @Override
        protected void initChannel(Channel channel) throws Exception {
            ByteBuf buffer = Unpooled.copiedBuffer(YUTAK.DELIMITER);
            channel.pipeline()
                    .addLast(new DelimiterBasedFrameDecoder(MAX_LENGTH,buffer))
                    .addLast(new PushMessageDecode(pushMessageComponent))
                    .addLast(new ProcessorMessageEncode(processMessageComponent))
                    .addLast(new YutakChannelHandler());
        }
    }
}
