package cn.lary.module.raffle.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Accessors(chain = true)
public class RaffleRuleCache {

    private int shard;


    private AtomicInteger stage = new AtomicInteger(0);

    private int limit;

    private List<Long> joiner;

    private Lock lock = new ReentrantLock();


    public RaffleCachePair add(long uid) {
        lock.lock();
        try {
            RaffleCachePair pair = new RaffleCachePair();
            joiner.add(uid);
            if (joiner.size() == 10) {
                pair.setSend(true);
                pair.setJoiner(joiner);
                joiner.clear();
            }
            stage.incrementAndGet();
            if (stage.get() <= limit /2) {
                pair.setOver(true);
            }
        }finally {
            lock.unlock();
        }
        return new RaffleCachePair();
    }
}
