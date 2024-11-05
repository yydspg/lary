package cn.lary.module.event.dto;

import cn.lary.common.kit.JSONKit;
import cn.lary.module.common.constant.LARY;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class RafflePublishEventDTO extends AbstractEventData {

    private long uid;

    private long streamId;

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
