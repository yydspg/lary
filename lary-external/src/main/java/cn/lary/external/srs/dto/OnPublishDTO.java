package cn.lary.external.srs.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Body:
 * {
 *   "server_id": "vid-0xk989d",
 *   "action": "on_publish",
 *   "client_id": "341w361a",
 *   "ip": "127.0.0.1",
 *   "vhost": "__defaultVhost__",
 *   "app": "live",
 *   "tcUrl": "rtmp://127.0.0.1:1935/live?vhost=__defaultVhost__",
 *   "stream": "livestream",
 *   "param": "?srsToken=xxx&uid=xxx&event=xxx",
 *   "stream_url": "video.test.com/live/livestream",
 *   "stream_id": "vid-124q9y3"
 * }
 */
@Data
public class OnPublishDTO extends SrsDTO {

    @JSONField(format="server_id")
    private String serverId;

    @JSONField(format="action")
    private String action;

    @JSONField(format="client_id")
    private String clientId;

    private String ip;

    private String vhost;

    private String app;

    private String tcUrl;

    private String stream;

    private String param;

    @JSONField(format="stream_url")
    private String streamUrl;

    @JSONField(format="stream_id")
    private String streamId;
}
