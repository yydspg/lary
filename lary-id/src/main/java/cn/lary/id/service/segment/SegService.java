package cn.lary.id.service.segment;

import cn.lary.api.id.Res;
import cn.lary.id.core.IdProd;
import cn.lary.id.core.segment.Buffer;
import cn.lary.id.core.segment.LaryCore;
import cn.lary.id.core.segment.Segment;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author paul 2024/4/25
 */
@Component
public class SegService implements IdProd {
    private static final long TAG_NOT_EXISTS = -2;
    private static final long SEGMENTS_NULL = -3;
    private static final int MAX_STEP = 1000000;
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;
    private static final byte ok = 1;
    private static final byte fail = 0;

    private final SegSql exec = new SegSql();
    private ExecutorService service = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new UpdateThreadFactory());
    private Map<String, Buffer> cache = new ConcurrentHashMap<String, Buffer>();

    @Override
    public void init() {
        loadCacheTags();
        updateCacheTags();
    }
    @Override
    public Res get(String tag) {
        if (cache.containsKey(tag)) {
            Buffer buffer = cache.get(tag);
            if (!buffer.isInitOk()) {
                synchronized (buffer) {
                    if (!buffer.isInitOk()) {
                            updateBufferSegment(tag, buffer.getNow());
                            buffer.setInitOk(true);
                    }
                }
            }
            return getId(cache.get(tag));
        }
        return new Res(TAG_NOT_EXISTS,fail);
    }

    private void updateCacheTags() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("Check-Cache-thread");
            t.setDaemon(true);
            return t;
        });
        service.scheduleWithFixedDelay(this::loadCacheTags, 60, 60, TimeUnit.SECONDS);
    }
    private void loadCacheTags(){
        List<String> queryTags = exec.getAllTag();
        if(queryTags == null || queryTags.isEmpty()) return ;
        List<String> cacheTags = cache.keySet().stream().toList();
        Set<String> addCacheTags = new HashSet<>();
        Set<String> delCacheTags = new HashSet<>();

        // add cache tags
        for (String t : queryTags) {
            if(!cacheTags.contains(t)) addCacheTags.add(t);
        }
        for (String t : addCacheTags) {
            Buffer buffer = new Buffer();
            buffer.setTag(t);
            Segment now = buffer.getNow();
            now.setV(new AtomicLong(0));
            now.setMax(0);
            now.setStep(0);
            cache.put(t,buffer);
        }
        // remove expired tag
        for (String t : cacheTags) {
            if(!queryTags.contains(t)) delCacheTags.add(t);
        }
        for (String t : delCacheTags) {
            cache.remove(t);
        }
    }
    public Res getId(final Buffer buffer) {
        while(true) {
            // read lock
            buffer.rLock().lock();
            try {
                final Segment now = buffer.getNow();
                // condition explain: [1] next segment is not available [2] current segment free space less than its step [3] return true if update thread status from disable into running
                if(!buffer.isNextOk() && (now.getIdle() < 0.9 * now.getStep()) && buffer.isRunning().compareAndSet(false,true)){
                    // async update next segment status
                    service.execute(()->{
                        Segment next = buffer.getSegments()[buffer.nextIndex()];
                        boolean ok = false;
                        try {
                            updateBufferSegment(buffer.getTag(),next);
                            ok = true;
                        }finally {
                            if(ok) {
                                buffer.wLock().lock();
                                buffer.setNextOk(true);
                                buffer.isRunning().set(true);
                                buffer.wLock().unlock();
                            } else {
                                buffer.isRunning().set(false);
                            }
                        }
                    });
                }
                // get id
                long v = now.getV().getAndIncrement();
                //check and return res,if v > now.getMax() ,it means that its time to change segment
                if(v < now.getMax()) {
                    return new Res(v,ok);
                }
            }finally {
                buffer.rLock().unlock();
            }
            threadSleep(buffer);
            //write lock , try to change segment
            buffer.wLock().lock();
            try {
                Segment now = buffer.getNow();
                // get id
                long v = now.getV().getAndIncrement();
                //check and return res
                if(v < now.getMax()) {
                    return new Res(v,ok);
                }
                if (buffer.isNextOk()) {
                    buffer.switchIndex();
                    buffer.setNextOk(false);
                } else {
                    return new Res(SEGMENTS_NULL,fail);
                }
            }finally {
                buffer.wLock().unlock();
            }
        }
    }
    private void updateBufferSegment(String tag,Segment segment){
        Buffer buffer = segment.getBuffer();
        LaryCore laryCore;
        if(!buffer.isInitOk() || buffer.getUpdateTimestamp() == 0) {
            laryCore = exec.update(tag);
            buffer.setStep(laryCore.getStep());
            buffer.setMinStep(laryCore.getStep());
            buffer.setUpdateTimestamp(System.currentTimeMillis());
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            // automatic change next step according to duration

            // increase step size
            if(duration < SEGMENT_DURATION && nextStep << 1 < MAX_STEP) {
                nextStep = nextStep << 1;
                //decrease step size
            } else if(duration >= SEGMENT_DURATION << 1) {
                nextStep = (nextStep >> 1 >= buffer.getMinStep()) ? nextStep >> 1:nextStep;
            }
            // update custom step
            laryCore = exec.updateCustom(tag,nextStep);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setMinStep(laryCore.getStep());
        }
        long v = laryCore.getMaxId() - buffer.getStep();
        segment.getV().set(v);
        segment.setMax(laryCore.getMaxId());
        segment.setStep(buffer.getStep());
    }
    // sleep thread when thread is running
    private void threadSleep(Buffer buffer) {
        int t = 0;
        while(buffer.isRunning().get()) {
            t += 1;
            if(t > 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }
    }


}
