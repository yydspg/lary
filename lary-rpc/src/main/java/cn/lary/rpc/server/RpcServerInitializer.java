package cn.lary.rpc.server;

import cn.lary.rpc.codec.RpcDecoder;
import cn.lary.rpc.codec.RpcEncoder;
import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.codec.RpcRes;
import cn.lary.rpc.core.Beat;
import cn.lary.rpc.serializer.Serializer;
import cn.lary.rpc.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// construct Channel Pipeline
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {
    private final Map<String ,Object> handlerMap;
    private final  ThreadPoolExecutor threadPoolExecutor;

    public RpcServerInitializer(Map<String, Object> handlerMap, ThreadPoolExecutor threadPoolExecutor) {
        this.handlerMap = handlerMap;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        Serializer serializer = KryoSerializer.getInstance();
        ch.pipeline().addLast(new IdleStateHandler(0,0, Beat.BEAT_TIMEOUT, TimeUnit.SECONDS))
                .addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0))
                .addLast(new RpcDecoder(RpcReq.class,serializer))
                .addLast(new RpcEncoder(RpcRes.class,serializer))
                .addLast(new RpcServerHandler(handlerMap,threadPoolExecutor));
    }
}
