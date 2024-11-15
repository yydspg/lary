package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.common.constant.LARY;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;


@Data
@Accessors(chain = true)
public class UserRegisterEventDTO extends AbstractEventData {

    private long uid;

    private String phone;

    @JSONField(format="invite_code")
    private String inviteCode;


    public String getData() {
        Map<Object,Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("phone", phone);
        map.put("inviteCode", inviteCode);
        return JSONKit.toJSON(map);
    }

    @Override
    public int getCategory() {
        return LARY.EVENT.CATEGORY.USER_REGISTER;
    }

    @Override
    public int getType() {
        return LARY.EVENT.TYPE.CMD;
    }
}
