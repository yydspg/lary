package cn.lary.module.cache.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class JoinLiveCacheDTO extends CacheDTO {

    private String ip;
    private String name;
    private int streamId;
    private String srsStreamId;
    private String srsServerId;
    private String srsClientId;
    private String srsToken;
    private String identify;

    public static JoinLiveCacheDTO of(Map map) {
        if (map == null) {
            return null;
        }
        JoinLiveCacheDTO dto = new JoinLiveCacheDTO();
        dto.setIp((String) map.get("ip"));
        dto.setStreamId(Integer.parseInt( map.get("streamId").toString()));
        dto.setSrsStreamId((String) map.get("srsStreamId"));
        dto.setSrsServerId((String) map.get("srsServerId"));
        dto.setSrsClientId((String) map.get("srsClientId"));
        dto.setSrsToken((String) map.get("srsToken"));
        dto.setIdentify((String) map.get("identify"));
        return dto;
    }
}
