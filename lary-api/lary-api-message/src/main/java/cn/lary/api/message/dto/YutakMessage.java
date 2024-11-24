package cn.lary.api.message.dto;


import lombok.Data;

import java.util.List;


@Data
public class YutakMessage {
    /**
     * 是否显示红点
     */
    private int redDot;
    /**
     * 是否同步
     */
    private int syncOnce;
    /**
     * 是否持久化
     */
    private int noPersist;
    /**
     * 频道id
     */
    private long cid;
    /**
     * 频道类型
     */
    private int type;
    /**
     * 来源id
     */
    private long fid;

    /**
     * 订阅者
     */
    private List<Long> subscribers;

    /**
     * 消息内容
     */
    private String payload;
    /**
     * 业务id
     */
    private int bid;

    public YutakMessage() {}
}
