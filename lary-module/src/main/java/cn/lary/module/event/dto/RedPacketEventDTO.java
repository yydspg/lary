package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.convert.EventConvert;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedPacketEventDTO implements EventConvert {
    private int uid;
    private int streamId;
    private long redpacketId;
    @Override
    public EventData of() {
        return new EventData()
                .setEvent(LARY.Event.redpacket)
                .setType(LARY.EventType.cmd)
                .setData(JSONKit.toJSON(this));
    }
}
