package cn.lary.module.stream.dto;

import cn.lary.module.cache.dto.CacheDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LiveCache  extends CacheDTO {

    /**
     * 直播间id
     */
    private long rid;
    /**
     * 直播id
     */
    private long sid;
    /**
     * 弹幕id
     */
    private long cid;
    /**
     * 直播等级
     */
    private int level;

    /**
     * 直播ip
     */
    private String ip;

    /**
     * srs property
     */
    private String srsTcUrl;
    private String srsToken;
    private String srsStreamId;
    private String srsServerId;
    private String srsClientId;
    private String srsStreamUrl;
    /**
     * 直播辨识符
     */
    private String identify;

}
