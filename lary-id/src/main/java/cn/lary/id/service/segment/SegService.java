package cn.lary.id.service.segment;

import cn.lary.id.core.IdProd;
import cn.lary.id.core.segment.Buffer;
import cn.lary.id.core.segment.LaryCore;
import cn.lary.id.core.segment.Segment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author paul 2024/4/25
 */
@Component
public class SegService implements IdProd {

    private static final long ID_IDCACHE_INIT_FALSE = -1;
    private static final long ID_KEY_NOT_EXISTS = -2;
    private static final long ID_TWO_SEGMENTS_NULL = -3;
    private static final int MAX_STEP = 1000000;
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;
    private final SegSql segSql;
    private ExecutorService service = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new UpdateThreadFactory());
    private Map<String, Buffer> cache = new ConcurrentHashMap<String, Buffer>();

    @Autowired
    public SegService(SegSql segSql){
        this.segSql = segSql;
    }
    @Override
    public String get(String tag) {
        if (cache.containsKey(tag)) {
            Buffer buffer = cache.get(tag);
            if(!buffer.isInitOk()) {

            }
        }
    }

    private void updateBufferSegment(String tag,Segment segment){
        Buffer buffer = segment.getBuffer();
        LaryCore laryCore;
        if(!buffer.isInitOk() || buffer.getUpdateTimestamp() == 0) {
             laryCore = segSql.update(tag);
             buffer.setStep(laryCore.getStep());
             buffer.setMinStep(laryCore.getStep());
             buffer.setUpdateTimestamp(System.currentTimeMillis());
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int step = buffer.getStep();
        }
    }
    @Override
    public void init() {
       loadCacheTags();
       updateCacheTags();
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
        List<String> queryTags = segSql.getAllTag();
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
            buffer.setK(t);
            Segment current = buffer.getCurrent();
            current.setV(new AtomicLong(0));
            current.setMax(0);
            current.setStep(0);
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
