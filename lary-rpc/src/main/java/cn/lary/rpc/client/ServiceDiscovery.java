package cn.lary.rpc.client;

import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.client.route.impl.RoundRobinLoadBalance;
import cn.lary.rpc.core.SystemConfig;
import cn.lary.rpc.kit.ReflectKit;
import cn.lary.rpc.protocol.RpcProtocol;
import cn.lary.rpc.protocol.RpcServiceInfo;
import cn.lary.rpc.registry.NacosRegistryClient;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

// aim to get connection from server nodes
public class ServiceDiscovery {
    private static final Logger log = LoggerFactory.getLogger(ServiceDiscovery.class);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,8,600L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(100));
    private final Map<String,RpcClientHandler> serverNodes = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<String> rpcProtocolSet = new CopyOnWriteArraySet<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition connected = lock.newCondition();
    private final long waitTimeout = 5000;
    private final RpcLoadBalance loadBalance ;
    private volatile boolean isRunning = true;
    public static AtomicBoolean updated = new AtomicBoolean(false);
    private ServiceDiscovery() {
        loadBalance = new RoundRobinLoadBalance();
    }

    private static class SingletonHolder {
        private static final ServiceDiscovery INSTANCE = new ServiceDiscovery();
    }
    public static ServiceDiscovery getInstance() {
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
        // route to a server node
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
           for (String t : rpcProtocolSet) {
               if(! updateServerSet.contains(serverNodes.get(t))) removeAndCloseHandler(t);
           }
       } else {
           log.error("No available RPC protocol");
           rpcProtocolSet.forEach(this::removeAndCloseHandler);
       }
        updated.set(true);
    }

    private void connectServer(RpcProtocol rpcProtocol) {
        // rpcProtocol means an independent server node
        // hashset keep item not null
        if(rpcProtocol.getRpcServiceInfos() == null || rpcProtocol.getRpcServiceInfos().isEmpty()){
            log.info("No available RPC protocol");
            return;
        }
        // add server
        rpcProtocolSet.add(rpcProtocol.getHost()+":"+rpcProtocol.getPort());
        log.info("New server node,host:[{}],port:[{}]", rpcProtocol.getHost(),rpcProtocol.getPort());
        // log current server`s services
        rpcProtocol.getRpcServiceInfos().forEach(t->{ log.info("New service ,name:[{}],version:[{}]",t.getServiceName(),t.getVersion());});

        final InetSocketAddress remote = new InetSocketAddress(rpcProtocol.getHost(), rpcProtocol.getPort());
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap boot = new Bootstrap();
                boot.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer());
                // try to build connect with current server by netty
                ChannelFuture channelFuture = boot.connect(remote);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info("Connect server success,server address:[{}],port:[{}]",remote,rpcProtocol.getPort());
                            RpcClientHandler handler = future.channel().pipeline().get(RpcClientHandler.class);
                            handler.rpcProtocol = rpcProtocol;
                            serverNodes.put(rpcProtocol.getHost()+":"+rpcProtocol.getPort(),handler);
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
    public void removeAndCloseHandler(String server) {
        RpcClientHandler handler = serverNodes.get(server);
        // ensure data complete transmission
        if(handler != null) {handler.close();}
//        removeHandler(rpcProtocol);

    }
    public void removeHandler(String server) {
        rpcProtocolSet.remove(server);
        serverNodes.remove(server);
        updated.set(true);
        log.info("remove connection,{}", server);
    }
    public void stop() {
        isRunning = false;
        rpcProtocolSet.forEach(this::removeAndCloseHandler);
        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }
    // load all server data in memory
    public void loadData(){
        Set<Field> fields = ReflectKit.get().getFields(RpcInject.class);
        fields.forEach(f -> {
            RpcInject r = f.getAnnotation(RpcInject.class);
            String version = r.version();
            String serviceName = f.getType().getName();
            List<Instance> instances = NacosRegistryClient.getInstance().getAllInstances(serviceName, SystemConfig.group, version);
            updateOps(instances);
        });
        List<RpcProtocol> servers = new ArrayList<>(serverNodes.values()).stream().map(t -> t.rpcProtocol).collect(Collectors.toList());
        updateConnectServer(servers);
        // subscribe
        loadBalance.serviceMap.keySet().forEach(s->{
            String[] args = s.split(":");
            NacosRegistryClient.getInstance().subscribe(args[0],SystemConfig.group,args[1],event->{
                    if(event instanceof NamingEvent) {
                        NamingEvent namingEvent = (NamingEvent) event;
                        List<Instance> instances = namingEvent.getInstances();
                        // add node
                        updateOps(instances);
                        // remove node
                        removeOps(instances);
                    }
            });
        });
    }
    private void updateOps(List<Instance> instances){
        for (Instance i : instances) {
            String ip = i.getIp();
            int port = i.getPort();
            // contain server
            String[] args = i.getServiceName().split(":");

            if(rpcProtocolSet.contains(ip+":"+port)){
                RpcServiceInfo s = new RpcServiceInfo();
                s.setVersion(args[1]);
                s.setServiceName(args[0]);
                serverNodes.get(ip+":"+port).rpcProtocol.addRpcServiceInfo(s);
                loadBalance.addProtocol(i.getServiceName(),serverNodes.get(ip+":"+port).rpcProtocol);
            } else {
                RpcClientHandler handler = new RpcClientHandler();
                RpcProtocol rpcProtocol = new RpcProtocol();
                rpcProtocol.setHost(ip);
                rpcProtocol.setPort(port);
                RpcServiceInfo rpcServiceInfo = new RpcServiceInfo();
                rpcServiceInfo.setServiceName(args[0]);
                rpcServiceInfo.setVersion(args[1]);
                rpcProtocol.addRpcServiceInfo(rpcServiceInfo);
                handler.rpcProtocol = rpcProtocol;
                rpcProtocolSet.add(ip+":"+port);
                serverNodes.put(ip+":"+port,handler);
                loadBalance.addProtocol(i.getServiceName(),rpcProtocol);
            }
        }
    }
    private void removeOps(List<Instance> instances){
        List<String> currentNodes = instances.stream().map(t -> t.getIp() + ":" + t.getPort()).collect(Collectors.toList());
        rpcProtocolSet.forEach(t->{
            if(!currentNodes.contains(t)) {
                rpcProtocolSet.remove(t);
                serverNodes.remove(t);
                loadBalance.serviceMap.remove(t);
            }
        });
    }
}
