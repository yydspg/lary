package cn.lary.external.srs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *     {
 *           "action": "on_unpublish",
 *           "client_id": "9308h583",
 *           "ip": "192.168.1.10",
 *           "vhost": "video.test.com",
 *           "app": "live",
 *           "stream": "livestream",
 *           "param":"?token=xxx&eventId=xxx",
 *           "server_id": "vid-werty",
 *           "stream_url": "video.test.com/live/livestream",
 *           "stream_id": "vid-124q9y3"
 *       }
 */
@Data
public class OnUnpublishDTO extends SrsDTO {

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

    private String param;

    @JSONField(format="stream_url")
    private String streamUrl;

    @JSONField(format="stream_id")
    private String streamId;
}
