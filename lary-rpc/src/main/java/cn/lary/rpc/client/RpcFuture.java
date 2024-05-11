package cn.lary.rpc.client;


import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.codec.RpcRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture implements Future<Object> {
    private static final Logger log = LoggerFactory.getLogger(RpcFuture.class);

    private final Sync sync ;
    private RpcReq rpcReq;
    private RpcRes rpcRes;
    private long startTime;
    private long responseTimeThreshold;
    private final List<AsyncRpcCallback> pendingCallbacks = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public RpcFuture(RpcReq rpcReq) {
        sync = new Sync();
        this.rpcReq = rpcReq;
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean cancel(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
         throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(1);
        if(rpcRes != null)
            return this.rpcRes.getAppRes();
        else return null;
    }
    public void done(RpcRes rpcRes) {
        this.rpcRes = rpcRes;
        sync.release(1);
        invokeCallback();
        // Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if(responseTime > responseTimeThreshold) {
            log.warn("service response over time,request id;[{}] response time:[{}] ",rpcReq.getReqId(),responseTime);
        }
    }
    private void invokeCallback() {
        lock.lock();
        try {
            for ( final AsyncRpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }
    public RpcFuture addCallback(AsyncRpcCallback callback) {
        lock.lock();
        try {
            if(isDone()) runCallback(callback);
            else this.pendingCallbacks.add(callback);
        } finally {
            lock.unlock();
        }
        return this;
    }
    private void runCallback(final AsyncRpcCallback callback) {

        final RpcRes rpcRes = this.rpcRes;
        RpcClient.submit(new Runnable() {
            @Override
            public void run() {
                if(!rpcRes.isError()) callback.success(rpcRes.getAppRes());
                else callback.fail(new RuntimeException("RpcResponse error",new Throwable(rpcRes.getErrorMsg())));
            }
        });
    }
    @Override
    public Object get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(1, timeUnit.toNanos(l));
        if(success) {
            if(this.rpcRes != null) { return rpcRes.getAppRes();}
            else return null;
        } else {
            throw new TimeoutException();
        }
    }

    // aqs operation
    static class Sync extends AbstractQueuedSynchronizer {
        @Serial
        private static final long serialVersionUID = 1L;
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if(getState() == pending) {
                return compareAndSetState(pending, done);
            }else return true;
        }
        protected boolean isDone() {return getState() == done;}
    }
}
