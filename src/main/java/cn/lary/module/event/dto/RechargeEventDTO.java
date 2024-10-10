package cn.lary.module.event.dto;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.event.convert.EventConvert;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RechargeEventDTO implements EventConvert {

    private Integer uid;

    private Long cost;

    private Long rechargeId;

    @Override
    public EventData of() {
        return new EventData()
                .setEvent(Lary.Event.recharge)
                .setType(Lary.EventType.cmd)
                .setData(JSONKit.toJSON(this));
    }
}
