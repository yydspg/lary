package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.common.constant.LARY;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class GoLiveEventDTO extends AbstractEventData {

    private long uid;

    @JSONField(format="device_id")
    private long did;

    @JSONField(format="stream_id")
    private long sid;

    @JSONField(format="channel_id")
    private long cid;

    public GoLiveEventDTO() {

    }


    public String getData() {
        Map<Object,Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("streamId", sid);
        map.put("channelId", cid);
        map.put("deviceId", did);
        return JSONKit.toJSON(map);
    }

    @Override
    public int getCategory() {
        return LARY.EVENT.CATEGORY.GO_LIVE;
    }

    @Override
    public int getType() {
        return LARY.EVENT.TYPE.CMD;
    }
}
