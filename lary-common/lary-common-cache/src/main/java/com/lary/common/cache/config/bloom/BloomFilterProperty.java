package com.lary.common.cache.config.bloom;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author paul 2023/12/31
 * main properties:
 * 1.number of hash functions
 * 2.funnel
 * 3.strategy
 * 4.bit
 */
@Data
@Component
public class BloomFilterProperty {
    @Value("${bloom-filter.expected-insertions}")
    private int expectedInsertions;
    @Value("${bloom-filter.fpp}")
    private double falsePositiveProbability;
}
