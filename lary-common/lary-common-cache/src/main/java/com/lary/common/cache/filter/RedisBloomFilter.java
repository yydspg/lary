package com.lary.common.cache.filter;

import com.google.common.hash.Funnel;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import com.lary.common.cache.config.bloom.BloomFilterProperty;
import com.lary.common.cache.impl.RedisCache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;



/**
 * @author paul 2023/12/31
 */
@Slf4j
@Component
public class RedisBloomFilter {
    /**
     * 二进制位数
     */
    private long numBits;
    /**
     * number of hash functions
     */
    private Integer numHashFunctions;


    private final Funnel<CharSequence> funnel = Funnels.stringFunnel(StandardCharsets.UTF_8);
    @Resource
    private BloomFilterProperty bfp;

    @Resource
    private RedisCache rs;
    public RedisBloomFilter() {

    }

    @PostConstruct
    public void initRedisBloomFilter() {
        this.numBits = optimalNumOfBits(bfp.getExpectedInsertions(), bfp.getFalsePositiveProbability());
        this.numHashFunctions = optimalNumOfHashFunctions(bfp.getExpectedInsertions(), numBits);
    }

    public void put(String k, String v) {
        byte[] bytes = Hashing.murmur3_128().hashObject(v, funnel).asBytes();
        long hash1 = lowerEight(bytes);
        long hash2 = upperEight(bytes);
        long combinedHash = hash1;
        for (int i = 0; i < numHashFunctions; i++) {
            long bit = (combinedHash & Long.MAX_VALUE) % numBits;
            rs.setBit(k,bit,true);
            combinedHash += hash2;
        }
    }

    public boolean isContain(String k, String v) {
        byte[] bytes = Hashing.murmur3_128().hashObject(v, funnel).asBytes();
        long hash1 = lowerEight(bytes);
        long hash2 = upperEight(bytes);
        long combinedHash = hash1;
        for (int i = 0; i < numHashFunctions; i++) {
            long bit = (combinedHash & Long.MAX_VALUE) % numBits;
            if (!rs.getBit(k,bit)) {
                return false;
            }
            combinedHash += hash2;
        }
        return true;
    }

    /*
        more detail: https://blog.csdn.net/hlzgood/article/details/109847282
     */
    public Long optimalNumOfBits(long expectedInsertions, double fpp) {
        if (expectedInsertions <= 0 || fpp < 0 || fpp > 1) {
            log.error("==> params error when create bloomFilter");
            return null;
        }
        if (fpp == 0) {
            fpp = Double.MAX_VALUE;
        }
        return (long) (-expectedInsertions * Math.log(fpp) / (Math.log(2) * Math.log(2)));
    }

    public static int optimalNumOfHashFunctions(long expectedInsertions, long numBits) {
        return Math.max(1, (int) Math.round((double) numBits / expectedInsertions * Math.log(2)));
    }

    private long lowerEight(byte[] bytes) {
        return Longs.fromBytes(bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
    }

    private long upperEight(byte[] bytes) {
        return Longs.fromBytes(bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]);
    }
}
