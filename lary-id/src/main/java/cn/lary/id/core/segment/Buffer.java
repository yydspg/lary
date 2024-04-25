package cn.lary.id.core.segment;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Double layer cache
 * @author paul 2024/4/25
 */

public class Buffer {
    private String tag;
    private Segment[] segments;
    private volatile int step;
    private volatile int minStep;
    private volatile int nowIndex;
    private volatile boolean isNextOk;
    private volatile boolean isInitOk;
    private volatile long updateTimestamp;
    private final AtomicBoolean isRunning;
    private final ReadWriteLock lock;

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

    public void setSegments(Segment[] segments) {
        this.segments = segments;
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

    public int getNowIndex() {
        return nowIndex;
    }

    public void setNowIndex(int nowIndex) {
        this.nowIndex = nowIndex;
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

    public AtomicBoolean getIsRunning() {
        return isRunning;
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public Segment getCurrent() {return segments[nowIndex];}
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
