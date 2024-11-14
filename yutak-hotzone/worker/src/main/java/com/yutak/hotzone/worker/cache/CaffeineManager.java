package com.yutak.hotzone.worker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yutak.hotzone.worker.counter.SlidingWindow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CaffeineManager {

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    private static final Cache<String, SlidingWindow> ALL_CACHE = Caffeine.newBuilder()
            .initialCapacity(8192)
            .maximumSize(1000000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .executor(executor)
            .softValues()
            .build();

    private static final Cache<String,Void> HOT_CACHE = Caffeine.newBuilder()
            .initialCapacity(256)
            .maximumSize(50000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .executor(executor)
            .softValues()
            .build();

    public static void put(String K,SlidingWindow win) {
        ALL_CACHE.put(K,win);
    }

    public static void remove(String K) {
        ALL_CACHE.invalidate(K);
    }

}
