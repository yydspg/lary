package cn.lary.api.message.vo;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class YutakMessageChannel {

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


    private long createAt;

    public YutakMessageChannel() {}

}
