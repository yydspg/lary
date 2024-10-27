package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.app.service.AbstractEventData;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.convert.EventConvert;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class RaffleEventDTO  extends AbstractEventData {

    private long uid;

    private int streamId;

    private long raffleId;


    public String getData() {
        Map<Object,Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("streamId", streamId);
        map.put("raffleId", raffleId);
        return JSONKit.toJSON(map);
    }

    @Override
    public int getCategory() {
        return LARY.EVENT.CATEGORY.RAFFLE;
    }

    @Override
    public int getType() {
        return LARY.EVENT.TYPE.CMD;
    }
}
