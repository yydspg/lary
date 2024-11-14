package cn.lary.module.common.cache.impl;

import cn.lary.module.common.cache.CacheComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheComponentImpl implements CacheComponent {

    private final StringRedisTemplate str;
    private final  RedisScript<List<Long>> simpleGetWinningUserScript = new DefaultRedisScript<>();
    private final  RedisScript<List<Long>> fullGetWinningUserScript = new DefaultRedisScript<>();


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

    }


    @Override
    public void delete(String k) {
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
    public void append(String k, String v) {
        str.opsForList().rightPush(k,v);
    }

    @Override
    public void append(String k, Collection<String> v) {
        str.opsForList().rightPushAll(k,v);
    }

    @Override
    public void renewal(String k, long exp) {
        str.expire(k, exp, TimeUnit.SECONDS);
    }

    @Override
    public void renewal(String k, long exp, TimeUnit timeUnit) {
        str.expire(k, exp, timeUnit);
    }

    @Override
    public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
        return str.execute(script, keys, args);
    }

    @Override
    public List<Long> simpleGetWinningUsers(long uid, int shardNum,int num) {

        try {
            return str.execute(simpleGetWinningUserScript,List.of("lary:raffle:"+uid),shardNum,num);
        } catch (Exception e) {
            log.error("simple get winning users error,uid:{},message:{}",uid, e.getMessage());
            return null;
        }
    }

    @Override
    public List<Long> fullGetWinningUsers(long uid, List<Integer> shardNums) {
        try {
            return str.execute(fullGetWinningUserScript,List.of("lary:raffle:"+uid),shardNums);
        } catch (Exception e) {
            log.error("full get winning users error,uid:{},message:{}",uid, e.getMessage());
            return null;
        }
    }

    @Override
    public void appendInvolvedUsers(long uid, List<Long> users) {
        str.opsForList()
                .rightPushAll("lary:raffle:"+uid,
                users.stream()
                        .map(Object::toString)
                        .toList());
    }

    @Override
    public int decrRedpacketCount(long uid) {
        return 0;
    }


}
