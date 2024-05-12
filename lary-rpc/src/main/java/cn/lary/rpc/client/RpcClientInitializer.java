package cn.lary.rpc.client;

import cn.lary.rpc.codec.RpcDecoder;
import cn.lary.rpc.codec.RpcEncoder;
import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.codec.RpcRes;
import cn.lary.rpc.core.Beat;
import cn.lary.rpc.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        KryoSerializer serializer = KryoSerializer.getInstance();
        ChannelPipeline cp = ch.pipeline();
        cp.addLast(new IdleStateHandler(0, 0, Beat.BEAT_INTERVAL, TimeUnit.SECONDS));
        cp.addLast(new RpcEncoder(RpcReq.class, serializer));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcRes.class, serializer));
        cp.addLast(new RpcClientHandler());
    }
}
