package cn.lary.api.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class YutakDropSubscriberMessage {
    /**
     * 频道id
     */
    private long cid;

    /**
     * 频道类型
     */
    private int type;

    /**
     * 是否重置
     */
    private int reset;

    /**
     * 频道订阅者
     */
    private List<Long> subscribers;
}
