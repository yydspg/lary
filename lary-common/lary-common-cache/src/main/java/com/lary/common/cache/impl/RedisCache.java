package com.lary.common.cache.impl;

import com.lary.common.cache.Cache;
import com.lary.common.core.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author paul 2023/12/30
 */
@Slf4j
@Component
public class RedisCache implements Cache {
    @SuppressWarnings("unchecked")
    private static final RedisTemplate<Object, Object> REDIS_TEMPLATE = SpringContextUtil.getBean("redisTemplate",
            RedisTemplate.class);
    public static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringContextUtil.getBean("stringRedisTemplate",
            StringRedisTemplate.class);
    public RedisCache() {

    }
    @Override
    public List<Object> keys(String pattern) {
        ArrayList<Object> keys = new ArrayList<>();
        return null;
    }

    @Override
    public boolean remove(Object k) {
        return Boolean.TRUE.equals(REDIS_TEMPLATE.delete(k));
    }

    @Override
    public void vagueDel(Object key) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void put(Object k, Object o, long time) {
        REDIS_TEMPLATE.opsForValue().set(k,o,time);
    }

    @Override
    public void put(Object k, Object o) {
        REDIS_TEMPLATE.opsForValue().set(k,o);
    }

    @Override
    public void putHash(Object k, Object f, Object v) {
        REDIS_TEMPLATE.opsForHash().put(k,f,v);
    }

    @Override
    public void putAllHash(Object k, Map map) {
        REDIS_TEMPLATE.opsForHash().putAll(k,map);
    }

    @Override
    public List multiGet(Collection keys) {
        return null;
    }

    @Override
    public Object get(Object k) {
        return REDIS_TEMPLATE.opsForValue().get(k);
    }

    @Override
    public Object getHash(Object k, Object f) {
        return REDIS_TEMPLATE.opsForHash().get(k.toString(),f);
    }

    @Override
    public Map<Object, Object> getHash(Object k) {
        return REDIS_TEMPLATE.opsForHash().entries(k);
    }

    @Override
    public String getString(Object k) {
        return Objects.requireNonNull(REDIS_TEMPLATE.opsForValue().get(k)).toString();
    }

    @Override
    public boolean hasKey(Object k) {
        return REDIS_TEMPLATE.opsForValue().get(k) != null;
    }

    @Override
    public Long incr(String k, long liveTime) {
        return null;
    }

    @Override
    public Long incr(String key) {
        return null;
    }

    @Override
    public Long cumulative(Object k, Object v) {
        return REDIS_TEMPLATE.opsForHyperLogLog().add(k,v);
    }

    @Override
    public Long counter(Object k) {
        return REDIS_TEMPLATE.opsForHyperLogLog().size(k);
    }

    @Override
    public Long zRemove(String k, String... m) {
        return REDIS_TEMPLATE.opsForZSet().remove(k,m);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScore(String k, int from, long to) {
        return REDIS_TEMPLATE.opsForZSet().reverseRangeWithScores(k,from,to);
    }

    @Override
    public boolean zAdd(String k, long s, String m) {
        return Boolean.TRUE.equals(REDIS_TEMPLATE.opsForZSet().add(k, m, s));
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String k, Integer c) {
        return REDIS_TEMPLATE.opsForZSet().reverseRangeWithScores(k,0,c);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String k, Integer s, Integer e) {
        return REDIS_TEMPLATE.opsForZSet().reverseRangeWithScores(k,s,e);
    }

    @Override
    public void incrementScore(String k, String m) {
        REDIS_TEMPLATE.opsForZSet().incrementScore(k,m,1);
    }

    @Override
    public void incrementScore(String k, String m, Integer score) {
        REDIS_TEMPLATE.opsForZSet().incrementScore(k,m,score);
    }

    @Override
    public void setBit(Object k, long o, boolean v) {
        REDIS_TEMPLATE.opsForValue().setBit(k,o,v);
    }

    @Override
    public boolean getBit(Object k, long o) {
        return Boolean.TRUE.equals(REDIS_TEMPLATE.opsForValue().getBit(k, o));
    }

    private void scan(String pattern, Consumer<byte[]> consumer){

    }
}
