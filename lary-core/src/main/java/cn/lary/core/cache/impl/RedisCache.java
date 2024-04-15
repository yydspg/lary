package cn.lary.core.cache.impl;
import cn.lary.core.cache.Cache;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/15
 */
@Component
@RequiredArgsConstructor
public class RedisCache implements Cache {

     private final StringRedisTemplate str;
    @Override
    public String getString(String k) {
        return str.opsForValue().get(k);
    }

    @Override
    public void setString(String k, String v) {
        str.opsForValue().set(k,v);
    }

    @Override
    public void setString(String k, String v, Long exp) {
        str.opsForValue().set(k,v,exp);
    }

    @Override
    public void setString(String k, String v, Long exp, TimeUnit timeUnit) {
        str.opsForValue().set(k,v,exp,timeUnit);
    }

    @Override
    public void setString(Map<String, String> map) {
        str.opsForValue().multiSet(map);
    }

    @Override
    public List<String> getString(Collection<String> keys) {
            return str.opsForValue().multiGet(keys);
    }

    @Override
    public void setHash(String k, String f, String v) {
        str.opsForHash().put(k,f,v);
    }

    @Override
    public void setHash(String k, Map<String, Object> map) {
        str.opsForHash().putAll(k,map);
    }

    @Override
    public String getHash(String k, String f) {
        return (String) str.opsForHash().get(k,f);
    }

    @Override
    public Map<Object, Object> getHash(String k) {
        return str.opsForHash().entries(k);
    }

    @Override
    public Collection<String> getHashFieldNames(String k) {
        List<String> res = new ArrayList<>();
        str.opsForHash().keys(k).forEach(t->res.add((String) t));
        return res;
    }

    @Override
    public Collection<String> getHashFieldValues(String k) {
        List<String> res = new ArrayList<>();
        str.opsForHash().values(k).forEach(t->res.add((String) t));
        return res;
    }

    @Override
    public void delHashFields(String k, Collection<String> fields) {
        str.opsForHash().delete(k,fields);
    }
}
