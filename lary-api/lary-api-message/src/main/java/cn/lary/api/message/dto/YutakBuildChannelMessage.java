package cn.lary.api.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class YutakBuildChannelMessage {

    /**
     * 频道id
     */
    private long cid;

    /**
     * 频道类型
     */
    private int type;

    /**
     * 用户id
     */
    private long uid;

    /**
     * 直播间id
     */
    private long rid;

    /**
     * 直播id
     */
    private long sid;

    /**
     * 状态
     */
    private int status;

    private List<Long> subscribers;

}
