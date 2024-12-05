package cn.lary.api.redpacket.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Accessors(chain = true)
public class RedpacketRuleCache {

    private int category;

    private AtomicInteger limit;


}
