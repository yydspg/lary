package cn.lary.module.raffle.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RaffleCachePair {

    private boolean over;

    private boolean send;

    private List<Long> joiner;
}
