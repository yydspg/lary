package cn.lary.id.core.segment;

/**
 * @author paul 2024/4/25
 */

public class LaryCore {
    private String k;
    private long maxId;
    private int step;
    private String updateTime;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
