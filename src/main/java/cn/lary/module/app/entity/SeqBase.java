package cn.lary.module.app.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;

@Data
@Accessors(chain = true)
public class SeqBase {
    private int id;
    private AtomicLong currentSeq;
    private int step;
    private long maxSeq;
}
