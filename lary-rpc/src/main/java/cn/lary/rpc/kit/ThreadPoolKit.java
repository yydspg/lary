package cn.lary.rpc.kit;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolKit {
    public static ThreadPoolExecutor createThreadPoolExecutor(String name,int corePoolSize, int maximumPoolSize) {

        return new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "netty-rpc-" + name + "-" + runnable.hashCode());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
