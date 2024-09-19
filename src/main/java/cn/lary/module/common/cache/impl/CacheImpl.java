package cn.lary.module.common.cache.impl;

import cn.lary.module.common.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheImpl implements RedisCache {

    private final StringRedisTemplate str;

    @Override
    public String get(String k) {
        return str.opsForValue().get(k);
    }

    @Override
    public void set(String k, String v) {
        str.opsForValue().set(k, v);
    }

    @Override
    public void set(String k, String v, Long exp) {
        str.opsForValue().set(k, v, exp, TimeUnit.SECONDS);
    }

    @Override
    public void set(String k, String v, Long exp, TimeUnit timeUnit) {
        str.opsForValue().set(k, v, exp, timeUnit);
    }

    @Override
    public void mset(Map<String, String> map) {

    }

    @Override
    public List<String> mget(Collection<String> keys) {
        return List.of();
    }

    @Override
    public void del(String k) {
        str.delete(k);
    }

    @Override
    public void setHash(String k, String f, String v) {
        str.opsForHash().put(k, f, v);
    }

    @Override
    public void incrHash(String k, String f) {
        str.opsForHash().increment(k, f, 1);
    }

    @Override
    public void decrHash(String k, String f) {
        str.opsForHash().increment(k, f, -1);
    }

    @Override
    public void setHash(String k, Map map) {
        str.opsForHash().putAll(k, map);
    }

    @Override
    public void setHash(String k, Map map, Long exp) {
        str.opsForHash().putAll(k, map);
        str.expire(k, exp, TimeUnit.SECONDS);
    }

    @Override
    public void setHash(String k, Map map, Long exp, TimeUnit timeUnit) {
        str.opsForHash().putAll(k, map);
        str.expire(k, exp, timeUnit);
    }

    @Override
    public String getHash(String k, String f) {
        return str.opsForHash().get(k,f).toString();
    }

    @Override
    public Map<Object, Object> getHash(String k) {
        return str.opsForHash().entries(k);
    }

    @Override
    public Collection<String> getHashFieldNames(String k) {
        return null;
    }

    @Override
    public Collection<String> getHashFieldValues(String k) {
        return null;
    }

    @Override
    public void delHashFields(String k, Collection<String> fields) {
        str.opsForHash().delete(k, fields);
    }
}
