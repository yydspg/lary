package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.app.service.AbstractEventData;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.convert.EventConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DownLiveEventDTO extends AbstractEventData {

    private long uid;

    @JsonProperty("stream_id")
    private int streamId;

    @JsonProperty("channel_id")
    private long channelId;

    public String getData() {
        Map<Object,Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("streamId", streamId);
        map.put("channelId", channelId);
        return JSONKit.toJSON(map);
    }

    @Override
    public int getCategory() {
        return LARY.EVENT.CATEGORY.DOWN_LIVE;
    }

    @Override
    public int getType() {
        return LARY.EVENT.TYPE.CMD;
    }
}
