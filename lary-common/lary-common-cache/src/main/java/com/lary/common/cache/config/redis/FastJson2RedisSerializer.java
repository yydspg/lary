package com.lary.common.cache.config.redis;

/**
 * @author paul 2023/12/30
 */

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.Objects;

/**
 * @author paul 2023/12/17
 */
@Slf4j
public class FastJson2RedisSerializer<T> implements RedisSerializer<T> {
    private static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(
            "com.***.***"
    );
    private final Class<T> clazz;
    public FastJson2RedisSerializer(Class<T> clazz){
        super();
        this.clazz = clazz;
    }
    @Override
    public byte[] serialize(T value) throws SerializationException {
        if(Objects.isNull(value)){
            return new byte[0];
        }
        try {
            return JSON.toJSONBytes(value, JSONWriter.Feature.WriteClassName);
        } catch (Exception e) {
            log.error("==> Serialize error: {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            return JSON.parseObject(bytes,clazz,AUTO_TYPE_FILTER);
        } catch (Exception e) {
            log.error("==> Deserialization error: {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
