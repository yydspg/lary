package cn.lary.module.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class YutakCaffeine<K,V> {

    public static Map<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();

//    public static Cache<K,V> build(LocalCacheOptions<K,V> options) {
//        if (!process(options)) {
//            log.error("yutak:build cache failed");
//            return null;
//        }
//        Cache<K, V> build = Caffeine.newBuilder()
//                .maximumSize(options.getMaximumSize())
//                .expireAfterWrite(options.getExpireAfterWrite(), options.getExpireAfterWriteTimeUnit())
//                .recordStats()
//                .maximumSize(options.getMaximumSize())
//                .build();
//        caches.put(options.getInstanceName(),build);
//        return build;
//    }

//   public static boolean process(LocalCacheOptions<K,V> options) {
//        if (StringKit.isEmpty( options.getInstanceName())) {
//            log.error("yutak:build caffeine without instance name");
//            return false;
//        }
//        if(options.getMaximumSize() <= 0 || options.getMaximumSize() > 1000) {
//            log.error("yutak:build caffeine maximum size invalid");
//            return false;
//        }
//        if(options.getExpireAfterWrite() <= 0 ) {
//            log.error("yutak:build caffeine expireAfterWrite invalid");
//            return false;
//        }
//        if(options.getExpireAfterWriteTimeUnit() == TimeUnit.NANOSECONDS
//                || options.getExpireAfterWriteTimeUnit() == TimeUnit.MICROSECONDS
//        || options.getExpireAfterWriteTimeUnit() == TimeUnit.MILLISECONDS ){
//            log.error("yutak:build caffeine expireAfterWrite time unit invalid,use this level no support");
//            return false;
//        }
//        return true;
//    }
    public static Cache<?,?> getCacheByName(String instanceName) {
        return caches.get(instanceName);
    }

    public static YutakCacheStatus getCacheStatus(String instanceName){
        Cache<?, ?> cache = getCacheByName(instanceName);
        return new YutakCacheStatus(cache.stats())
                .setTimestamp(System.currentTimeMillis())
                .setInstanceName(instanceName);
    }
    public static void clearCache(String instanceName){
        caches.get(instanceName).invalidateAll();
    }
}
