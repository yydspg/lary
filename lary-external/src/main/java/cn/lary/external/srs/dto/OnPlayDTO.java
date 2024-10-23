package cn.lary.external.srs.dto;

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
 *          "param":"?token=xxx&uid=xxx",
 *          "pageUrl": "http://www.test.com/live.html",
 *          "server_id": "vid-werty",
 *          "stream_url": "video.test.com/live/livestream",
 *          "stream_id": "vid-124q9y3"
 *      }
 */
@Data
public class OnPlayDTO  extends SrsDTO{
    @JsonProperty("server_id")
    private String serverId;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("action")
    private String action;

    private String ip;

    private String vhost;

    private String app;

    private String stream;

    private String pageUrl;

    private String param;

    @JsonProperty("stream_url")
    private String streamUrl;

    @JsonProperty("stream_id")
    private String streamId;


}