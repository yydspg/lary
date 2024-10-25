package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.app.service.AbstractEventData;
import cn.lary.module.common.constant.LARY;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class RechargeEventDTO extends AbstractEventData {

    private int uid;

    private long cost;

    private long rechargeId;

    @Override
    public String getData() {
        Map<Object,Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("cost", cost);
        map.put("rechargeId", rechargeId);
        return JSONKit.toJSON(map);
    }

    @Override
    public int getCategory() {
        return LARY.EVENT.CATEGORY.RECHARGE_DETECTION;
    }

    @Override
    public int getType() {
        return LARY.EVENT.TYPE.MESSAGE;
    }
}
