package cn.lary.rpc.server;

import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.codec.RpcRes;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/*
 rpc handler (rpc request processor)
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcReq> {

    private static final Logger log = LoggerFactory.getLogger(RpcServerHandler.class);

    private final Map<String,Object> handlerMap;
    private final ThreadPoolExecutor threadPoolExecutor;

    public RpcServerHandler(Map<String, Object> handlerMap, ThreadPoolExecutor threadPoolExecutor) {
        this.handlerMap = handlerMap;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcReq rpcReq) throws Exception {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                RpcRes rpcRes = new RpcRes();
                rpcRes.setReqId(rpcReq.getReqId());
                try {
                    Object res = handle(rpcReq);
                    rpcRes.setAppRes(res);
                }catch (Throwable t) {
                    rpcRes.setErrorMsg(t.toString());
                    log.error("Rpc Server handle request error");
                }
                // return rpc response
                ctx.writeAndFlush(rpcRes);
                ctx.writeAndFlush(rpcRes).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        log.info("Send message success");
                    }
                });
            }
        });

    }
    private Object handle(RpcReq rpcReq) throws Throwable {
        String className = rpcReq.getClassName();
        String version = rpcReq.getVersion();
        Object serviceBean = handlerMap.get(className + version);
        if(serviceBean == null ) {
            log.error("can not find service implement with interface name:{} and version{} ",className,version);
            return null;
        }
        Class<?> serviceBeanClass = serviceBean.getClass();
        String methodName = rpcReq.getMethodName();
        Class<?>[] parameterTypes = rpcReq.getParameterTypes();
        Object[] parameters = rpcReq.getParameters();


//        log.debug(serviceBeanClass.getName());
//        log.debug(methodName);
//        for (int i = 0; i < parameterTypes.length; ++i) {
//            log.debug(parameterTypes[i].getName());
//        }
//        for (int i = 0; i < parameters.length; ++i) {
//            log.debug(parameters[i].toString());
//        }
        FastClass fastClass = FastClass.create(serviceBeanClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex,serviceBean,parameters);

    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Server caught exception: " + cause.getMessage());
        ctx.close();
    }
}

