package cn.lary.module.event.dto;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.event.build.EventConvert;
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
                .setEvent(Lary.Event.redpacket)
                .setType(Lary.EventType.cmd)
                .setData(JSONKit.toJSON(this));
    }
}
