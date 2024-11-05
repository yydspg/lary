package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.common.constant.LARY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketEventDTO extends AbstractEventData {

    private long uid;

    private long streamId;

    private long redpacketId;

    @Override
    public String getData() {
        Map<Object,Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("streamId", streamId);
        map.put("redpacketId", redpacketId);
        return JSONKit.toJSON(map);
    }

    @Override
    public int getCategory() {
        return LARY.EVENT.CATEGORY.RED_PACKET;
    }

    @Override
    public int getType() {
        return LARY.EVENT.TYPE.CMD;
    }
}
