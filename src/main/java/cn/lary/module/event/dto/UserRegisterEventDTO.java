package cn.lary.module.event.dto;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.event.build.EventConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class UserRegisterEventDTO implements EventConvert {

    private Integer uid;

    private String phone;

    @JsonProperty("invite_code")
    private String inviteCode;

    @Override
    public EventData of() {
        return new EventData()
                .setEvent(Lary.Event.register)
                .setType(Lary.EventType.cmd)
                .setData(JSONKit.toJSON(this));
    }
}
