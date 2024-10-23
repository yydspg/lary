package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.convert.EventConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class UserRegisterEventDTO implements EventConvert {

    private int uid;

    private String phone;

    @JsonProperty("invite_code")
    private String inviteCode;

    @Override
    public EventData of() {
        return new EventData()
                .setEvent(LARY.Event.register)
                .setType(LARY.EventType.cmd)
                .setData(JSONKit.toJSON(this));
    }
}
