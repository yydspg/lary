package cn.lary.module.cache.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class LiveCache  extends AbstractCacheDTO{

    private long sid;
    private long rid;
    private long cid;
    private int level;
    private String ip;
    private String srsTcUrl;
    private String srsToken;
    private String srsSid;
    private String srsServerId;
    private String srsClientId;
    private String srsStreamUrl;
    private String identify;

}
