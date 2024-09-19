package cn.lary.module.stream.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class JoinLiveCacheDTO {

    private String ip;
    private String streamId;
    private String srsStreamId;
    private String srsServerId;
    private String srsClientId;
    private String srsToken;
    private String stream;

    public static JoinLiveCacheDTO of(Map map) {
        if (map == null) {
            return null;
        }
        JoinLiveCacheDTO dto = new JoinLiveCacheDTO();
        dto.setIp((String) map.get("ip"));
        dto.setStreamId((String) map.get("streamId"));
        dto.setSrsStreamId((String) map.get("srsStreamId"));
        dto.setSrsServerId((String) map.get("srsServerId"));
        dto.setSrsClientId((String) map.get("srsClientId"));
        dto.setSrsToken((String) map.get("srsToken"));
        return dto;
    }
}
