package com.yutak.hotzone.client.netty;

import com.yutak.hotzone.YUTAK;
import com.yutak.hotzone.client.event.NettyClientEventHandler;
import com.yutak.hotzone.client.worker.WorkerManager;
import com.yutak.hotzone.entry.YutakProcessMessage;
import com.yutak.hotzone.entry.YutakPushMessage;
import com.yutak.hotzone.kryo.KryoComponent;
import com.yutak.hotzone.message.ProcessorMessageDecode;
import com.yutak.hotzone.message.PushMessageEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class YutakNettyClient {

    private static final Logger log = LoggerFactory.getLogger(YutakNettyClient.class);
    public static int MAX_LENGTH = 2 * 1024 * 1024;
    private final static YutakNettyClient INSTANCE = new YutakNettyClient();
    private final  Bootstrap bootstrap;

    private YutakNettyClient() {
        bootstrap = build();
    }
    public static YutakNettyClient getInstance() {
        return INSTANCE;
    }

    private Bootstrap build(){
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        KryoComponent<YutakProcessMessage> processMessageComponent = new KryoComponent<>(YutakProcessMessage.class);
        KryoComponent<YutakPushMessage> pushMessageComponent = new KryoComponent<>(YutakPushMessage.class);
        NettyClientEventHandler handler = new NettyClientEventHandler();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ByteBuf delimiter = Unpooled.copiedBuffer(YUTAK.DELIMITER);
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(MAX_LENGTH, delimiter))
                                .addLast(new ProcessorMessageDecode(processMessageComponent))
                                .addLast(new PushMessageEncode(pushMessageComponent))
                                .addLast(handler);
                    }
                });
        return bootstrap;
    }

    public synchronized boolean connect(List<String> addresses) {
        boolean allSuccess = true;
        for (String address : addresses) {
            if (WorkerManager.getConnectStatus(address)) {
                continue;
            }
            String[] ss = address.split(":");
            try {
                ChannelFuture channelFuture = bootstrap.connect(ss[0], Integer.parseInt(ss[1])).sync();
                Channel channel = channelFuture.channel();
                WorkerManager.register(new WorkerManager.Server(address,channel));
            } catch (Exception e) {
                log.error(e.getMessage());
                allSuccess = false;
            }
        }
        return allSuccess;
    }
}
