package cn.lary.id.core.segment;

import cn.lary.id.core.segment.Buffer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author paul 2024/4/25
 */

public class Segment {
    private AtomicLong v = new AtomicLong(0);
    private volatile long max;
    private volatile int step;
    private Buffer buffer;

    public Segment(Buffer buffer) {
        this.buffer = buffer;
    }

    public AtomicLong getV() {
        return v;
    }

    public void setV(AtomicLong v) {
        this.v = v;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }
    public long getIdle() {
        return this.getMax() - getV().get();
    }
    @Override
    public String toString() {
         final StringBuilder sb = new StringBuilder("Segment(");
        sb.append("value:");
        sb.append(v);
        sb.append(",max:");
        sb.append(max);
        sb.append(",step:");
        sb.append(step);
        sb.append(")");
        return sb.toString();
    }
}
