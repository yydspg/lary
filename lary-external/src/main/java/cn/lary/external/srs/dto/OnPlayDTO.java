package cn.lary.external.srs.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *      {
 *          "action": "on_play",
 *          "client_id": "9308h583",
 *          "ip": "192.168.1.10",
 *          "vhost": "video.test.com",
 *          "app": "live",
 *          "stream": "livestream",
 *          "param":"?srsToken=xxx&uid=xxx",
 *          "pageUrl": "http://www.test.com/live.html",
 *          "server_id": "vid-werty",
 *          "stream_url": "video.test.com/live/livestream",
 *          "stream_id": "vid-124q9y3"
 *      }
 */
@Data
public class OnPlayDTO  extends SrsDTO{
    @JSONField(format="server_id")
    private String serverId;

    @JSONField(format="client_id")
    private String clientId;

    @JSONField(format="action")
    private String action;

    private String ip;

    private String vhost;

    private String app;

    private String stream;

    private String pageUrl;

    private String param;

    @JSONField(format="stream_url")
    private String streamUrl;

    @JSONField(format="stream_id")
    private String streamId;


}
