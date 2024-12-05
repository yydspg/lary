package cn.lary.api.stream.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StreamCache {
    /**
     * 直播id
     */
    private long sid;
    /**
     * 弹幕id
     */
    private long cid;

    /**
     * 新增粉丝
     */
    private long newFan;
    /**
     * 点赞数目
     */
    private long star;

    /**
     * 粉丝观看数量
     */
    private long watch;

    /**
     * 送礼人数
     */
    private long gift;

    
}
