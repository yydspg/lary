package cn.lary.rpc.server;

import cn.lary.rpc.kit.ThreadPoolKit;
import cn.lary.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private Thread startThread;

    private String serverAddress;

    private ServiceRegistry serviceRegistry;

    Map<String ,Object> services= new HashMap<>();

    public NettyServer( String serverAddress) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = new ServiceRegistry(serverAddress);
    }
    public void addService(String serviceName,String version,Object serviceBean) {
        log.info("add service {},version {}",serviceName,version);
        services.put(serviceName+":"+version,serviceBean);
    }
    public void start() {
        startThread = new Thread(new Runnable() {
            ThreadPoolExecutor threadPoolExecutor = ThreadPoolKit.createThreadPoolExecutor(
                    NettyServer.class.getName(),16,32
            );
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try{
                    ServerBootstrap boot = new ServerBootstrap();
                    boot.group(bossGroup,workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new RpcServerInitializer(services,threadPoolExecutor))
                            .option(ChannelOption.SO_BACKLOG,128)
                            .childOption(ChannelOption.SO_KEEPALIVE,true);
                    // serverAddress example : 127.0.0.1:8080
                    String[] args = serverAddress.split(":");
                    String host = args[0];
                    int port = Integer.parseInt(args[1]);
                    // sync
                    ChannelFuture future = boot.bind(host, port).sync();
                    if(serviceRegistry != null ){
                        serviceRegistry.registerService(services);
                    }
                    // stater server
                    log.info("server start on port:[{}]",port);
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.info("Rpc server remoting server stop");
                }finally {
//                        serviceRegistry.deregisterService();
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                }
            }
        });
        startThread.start();
    }
    public void stop() {
        // destroy server thread
        if(startThread != null && startThread.isAlive()) {
            startThread.interrupt();
        }
    }
}
