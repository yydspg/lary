package cn.lary.id.core.segment;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Double layer cache , contains two segment ,which means now ,other is next
 * @author paul 2024/4/25
 */

public class Buffer {
    // Unique identification of different businesses
    private String tag;
    // double segments cache
    private Segment[] segments;
    // current step (self-adaption)
    private volatile int step;
    // database step
    private volatile int minStep;
    // it indicates which segment is using
    private volatile int nowIndex;
    // next segment status
    private volatile boolean isNextOk;
    // buffer init status
    private volatile boolean isInitOk;
    // update time mills
    private volatile long updateTimestamp;
    // thread running status
    private final AtomicBoolean isRunning;
    // control lock
    private final ReadWriteLock lock;

    // init segments
    public Buffer() {
        segments = new Segment[]{new Segment(this),new Segment(this)};
        nowIndex = 0;
        isNextOk = false;
        isInitOk = false;
        isRunning = new AtomicBoolean(false);
        lock = new ReentrantReadWriteLock();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMinStep() {
        return minStep;
    }

    public void setMinStep(int minStep) {
        this.minStep = minStep;
    }


    public boolean isNextOk() {
        return isNextOk;
    }

    public void setNextOk(boolean nextOk) {
        isNextOk = nextOk;
    }

    public boolean isInitOk() {
        return isInitOk;
    }

    public void setInitOk(boolean initOk) {
        isInitOk = initOk;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public AtomicBoolean isRunning() {
        return isRunning;
    }


    public Lock rLock() {
        return lock.readLock();
    }

    public Lock wLock() {
        return lock.writeLock();
    }
    public int nextIndex() {
        return (nowIndex ^ 1) & 1;
    }
    public void switchIndex() {
        nowIndex = nextIndex();
    }
    public Segment getNow() {return segments[nowIndex];}
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Buffer{");
        sb.append("tag='").append(tag).append('\'');
        sb.append(", segments=").append(Arrays.toString(segments));
        sb.append(", currentIndex=").append(nowIndex);
        sb.append(", isNextOk=").append(isNextOk);
        sb.append(", isInitOk=").append(isInitOk);
        sb.append(", isThreadRunning=").append(isRunning);
        sb.append(", step=").append(step);
        sb.append(", minStep=").append(minStep);
        sb.append(", updateTimestamp=").append(updateTimestamp);
        sb.append('}');
        return sb.toString();
    }
}
