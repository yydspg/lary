package cn.lary.module.stream.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class LiveCacheDTO {

    private String streamId;
    private String giftBuyChannelId;
    private String wkChannelId;
    private String ip;
    private String srsTcUrl;
    private String srsToken;
    private String srsStreamId;
    private String srsServerId;
    private String srsClientId;
    private String srsStreamUrl;
    private String stream;

    public static LiveCacheDTO of(Map<Object,Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        LiveCacheDTO dto = new LiveCacheDTO();
        dto.setStreamId((String) map.get("streamId"));
        dto.setGiftBuyChannelId((String) map.get("giftBuyChannelId"));
        dto.setWkChannelId((String) map.get("wkChannelId"));
        dto.setIp((String) map.get("ip"));
        dto.setSrsTcUrl((String) map.get("srsTcUrl"));
        dto.setSrsToken((String) map.get("srsToken"));
        dto.setSrsStreamId((String) map.get("srsStreamId"));
        dto.setSrsServerId((String) map.get("srsServerId"));
        dto.setSrsClientId((String) map.get("srsClientId"));
        dto.setSrsStreamUrl((String) map.get("srsStreamUrl"));
        dto.setStream((String) map.get("stream"));
        return dto;

    }

}
