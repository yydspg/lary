package cn.lary.module.stream.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class LiveCacheDTO {

    private int streamId;
    private int wkChannelId;
    private String ip;
    private String srsTcUrl;
    private String srsToken;
    private String srsStreamId;
    private String srsServerId;
    private String srsClientId;
    private String srsStreamUrl;
    private String identify;

    public static LiveCacheDTO of(Map<Object,Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        LiveCacheDTO dto = new LiveCacheDTO();
        dto.setStreamId(Integer.parseInt( map.get("streamId").toString()));
        dto.setWkChannelId(Integer.parseInt( map.get("wkChannelId").toString()));
        dto.setIp((String) map.get("ip"));
        dto.setSrsTcUrl((String) map.get("srsTcUrl"));
        dto.setSrsToken((String) map.get("srsToken"));
        dto.setSrsStreamId((String) map.get("srsStreamId"));
        dto.setSrsServerId((String) map.get("srsServerId"));
        dto.setSrsClientId((String) map.get("srsClientId"));
        dto.setSrsStreamUrl((String) map.get("srsStreamUrl"));
        dto.setIdentify((String) map.get("identify"));
        return dto;

    }

}
