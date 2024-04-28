package cn.lary.id.core.segment;

/**
 * @author paul 2024/4/25
 */

public class LaryCore {
    // Unique identification of different businesses
    private String tag;
    // max id
    private long maxId;
    // base step
    private int step;
    //update time
    private String updateTime;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
