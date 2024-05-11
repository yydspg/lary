package cn.lary.rpc.client;

import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.client.route.impl.RoundRobin;
import cn.lary.rpc.protocol.RpcProtocol;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectManager.class);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,8,600L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(100));
    private final Map<RpcProtocol,RpcClientHandler> serverNodes = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<RpcProtocol> rpcProtocols = new CopyOnWriteArraySet<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition connected = lock.newCondition();
    private final long waitTimeout = 5000;
    private final RpcLoadBalance loadBalance = new RoundRobin();
    private volatile boolean isRunning = true;
    private ConnectManager() {}

    private static class SingletonHolder {
        private static final ConnectManager INSTANCE = new ConnectManager();
    }
    public static ConnectManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public RpcClientHandler getRpcClientHandler(String serviceKey) { return null; }
    public void removeRpcClientHandler(RpcProtocol rpcProtocol) {
        rpcProtocols.remove(rpcProtocol);

    }

}
