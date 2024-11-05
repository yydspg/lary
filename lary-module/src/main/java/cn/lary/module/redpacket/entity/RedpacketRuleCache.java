package cn.lary.module.redpacket.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Accessors(chain = true)
public class RedpacketRuleCache {

    private int category;

    private AtomicInteger limit;


}
