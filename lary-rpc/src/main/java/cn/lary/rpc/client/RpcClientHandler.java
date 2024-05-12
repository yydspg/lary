package cn.lary.rpc.client;

import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.codec.RpcRes;
import cn.lary.rpc.core.Beat;
import cn.lary.rpc.protocol.RpcProtocol;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcRes> {

    private static final Logger log = LoggerFactory.getLogger(RpcClientHandler.class);
    private final ConcurrentHashMap<String ,RpcFuture> pendingRpc = new ConcurrentHashMap<>();
    private volatile Channel channel;
    private RpcProtocol rpcProtocol;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) sendRpcReq(Beat.BEAT_PING);
        else super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ConnectManager.getInstance().removeHandler(rpcProtocol);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRes msg) throws Exception {
        String reqId = msg.getReqId();
        RpcFuture rpcFuture = pendingRpc.get(reqId);
        if(rpcFuture == null) {
            pendingRpc.remove(reqId);
            rpcFuture.done(msg);
        } else log.warn("Can not get the pending RpcFuture for reqId:{}", reqId);
    }
    public RpcFuture sendRpcReq (RpcReq rpcReq) {
        RpcFuture rpcFuture = new RpcFuture(rpcReq);
        pendingRpc.put(rpcReq.getReqId(),rpcFuture);
        try {
            ChannelFuture future = channel.writeAndFlush(rpcReq).sync();
            if(!future.isSuccess()) log.error("sendRpcReq error:{}",rpcReq);
        }catch (InterruptedException e){
            log.error("SendRpcReq error:{}",e.getMessage());
        }
        return rpcFuture;
    }
    public void close () {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    public void setRpcProtocol(RpcProtocol rpcProtocol) {
        this.rpcProtocol = rpcProtocol;
    }
}
