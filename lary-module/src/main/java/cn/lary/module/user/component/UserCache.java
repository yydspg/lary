package cn.lary.module.user.component;

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
    private long rid;
    /**
     * device id
     */
    private long did;

    /**
     * srs token
     */
    private String token;

    /**
     * srs stream id
     */
    private long ssid;

    /**
     * identify
     */
    private String identify;

    /**
     * ip
     */
    private String ip;
    private String srsStreamId;
    private String srsServerId;
    private String srsClientId;

    public UserCache() {}

    public UserCache(UserCache cache) {
        this.uid = cache.uid;
        this.rid = cache.rid;
        this.did = cache.did;
        this.token = cache.token;
        this.ssid = cache.ssid;
        this.identify = cache.identify;
        this.ip = cache.ip;
        this.srsStreamId = cache.srsStreamId;
        this.srsServerId = cache.srsServerId;
        this.srsClientId = cache.srsClientId;

    }
}
