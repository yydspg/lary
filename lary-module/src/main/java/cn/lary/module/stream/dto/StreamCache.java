package cn.lary.module.stream.dto;

import cn.lary.module.cache.dto.CacheDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class StreamCache extends CacheDTO {
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
