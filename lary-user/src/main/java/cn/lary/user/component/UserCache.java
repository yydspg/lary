package cn.lary.user.component;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserCache {
    /**
     * user id
     */
    private long uid;
    /**
     * room id
     */
    private long sid;
    /**
     * danmaku id
     */
    private long cid;
    /**
     * device id
     */
    private long did;
    /**
     * device flag
     */
    private int flag;

    private String username;


    /**
     * identify
     */
    private String identify;

    /**
     * ip
     */
    private String ip;
    /**
     * srs srsToken
     */
    private String srsToken;

    private String srsStreamId;

    private String srsServerId;

    private String srsClientId;

    public UserCache() {}

    public UserCache(UserCache cache) {
        this.uid = cache.uid;
        this.sid = cache.sid;
        this.did = cache.did;
        this.srsToken = cache.srsToken;
        this.identify = cache.identify;
        this.ip = cache.ip;
        this.srsStreamId = cache.srsStreamId;
        this.srsServerId = cache.srsServerId;
        this.srsClientId = cache.srsClientId;

    }
}
