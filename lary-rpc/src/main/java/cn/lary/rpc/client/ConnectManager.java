package cn.lary.rpc.client;

import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.client.route.impl.RoundRobinLoadBalance;
import cn.lary.rpc.protocol.RpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// aim to get connection from server nodes
public class ConnectManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectManager.class);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,8,600L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(100));
    private final Map<RpcProtocol,RpcClientHandler> serverNodes = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<RpcProtocol> rpcProtocolSet = new CopyOnWriteArraySet<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition connected = lock.newCondition();
    private final long waitTimeout = 5000;
    private final RpcLoadBalance loadBalance = new RoundRobinLoadBalance();
    private volatile boolean isRunning = true;
    public static AtomicBoolean updated = new AtomicBoolean(false);
    private ConnectManager() {}

    private static class SingletonHolder {
        private static final ConnectManager INSTANCE = new ConnectManager();
    }
    public static ConnectManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public RpcClientHandler getRpcClientHandler(String serviceKey) throws Exception {
        int size = serverNodes.size();
        while ( isRunning && size == 0) {
            try {
                waitingForHandler();
                size = serverNodes.size();
            }catch (InterruptedException e) {
                log.error("waiting for server handler error", e);
            }
        }
        // route
        RpcProtocol rpcProtocol = loadBalance.route(serviceKey, serverNodes);
        RpcClientHandler rpcClientHandler = serverNodes.get(rpcProtocol);
        // no available connection
        if(rpcClientHandler == null) { throw new Exception("Maximum number of connections");}
        return rpcClientHandler;
    }

    public void updateConnectServer(List<RpcProtocol> servers) {
       if (servers != null && servers.size() > 0) {
           HashSet<RpcProtocol> updateServerSet = new HashSet<>(servers.size());
           updateServerSet.addAll(servers);

           // update data through comparison
           // add new info
           for (RpcProtocol t : updateServerSet) {
               if( !rpcProtocolSet.contains(t)) connectServer(t);
           }
           // del invalid server
           for (RpcProtocol t : rpcProtocolSet) {
               if(! updateServerSet.contains(t)) removeAndCloseHandler(t);
           }
       } else {
           log.error("No available RPC protocol");
           rpcProtocolSet.forEach(this::removeAndCloseHandler);
       }
        updated.set(true);
    }
    public void updateConnectServer(RpcProtocol rpcProtocol) {

    }

    private void connectServer(RpcProtocol rpcProtocol) {
        // hashset keep item not null
        if(rpcProtocol.getRpcServiceInfos() == null || rpcProtocol.getRpcServiceInfos().isEmpty()){
            log.info("No available RPC protocol");
            return;
        }
        rpcProtocolSet.add(rpcProtocol);
        log.info("New server node,host:[{}],port:[{}]", rpcProtocol.getHost(),rpcProtocol.getPort());
        rpcProtocol.getRpcServiceInfos().forEach(t->{ log.info("New service ,name:[{}],version:[{}]",t.getServiceName(),t.getVersion());});

        final InetSocketAddress remote = new InetSocketAddress(rpcProtocol.getHost(), rpcProtocol.getPort());
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap boot = new Bootstrap();
                boot.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer());
                ChannelFuture channelFuture = boot.connect(remote);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info("Connect server success,server address:[{}],port:[{}]",remote,rpcProtocol.getPort());
                            RpcClientHandler handler = future.channel().pipeline().get(RpcClientHandler.class);
                            serverNodes.put(rpcProtocol,handler);
                            handler.setRpcProtocol(rpcProtocol);
                        }else {
                            log.error("Can not connect serverAddress:[{}],port:[{}]",remote,rpcProtocol.getPort());
                        }
                    }
                });
            }
        });
    }

    private void signalAvailableHandler(){
        lock.lock();
        try {
            connected.signalAll();
        }finally {
            lock.unlock();
        }
    }
    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            log.warn("Waiting for RPC protocol");
            return connected.await(this.waitTimeout,TimeUnit.MILLISECONDS);
        }finally {
            lock.unlock();
        }
    }
    public void removeAndCloseHandler(RpcProtocol rpcProtocol) {
        RpcClientHandler handler = serverNodes.get(rpcProtocol);
        // ensure data complete transmission
        if(handler != null) {handler.close();}
        removeHandler(rpcProtocol);

    }
    public void removeHandler(RpcProtocol rpcProtocol) {
        rpcProtocolSet.remove(rpcProtocol);
        serverNodes.remove(rpcProtocol);
        updated.set(true);
        log.info("remove connection,host:{},port:{}", rpcProtocol.getHost(),rpcProtocol.getPort());
    }
    public void stop() {
        isRunning = false;
        rpcProtocolSet.forEach(this::removeAndCloseHandler);
        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }
}
