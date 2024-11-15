package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.common.constant.LARY;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DownLiveEventDTO extends AbstractEventData {

    private long uid;

    @JSONField(format="stream_id")
    private int streamId;

    @JSONField(format="channel_id")
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
