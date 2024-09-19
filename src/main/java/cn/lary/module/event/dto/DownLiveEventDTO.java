package cn.lary.module.event.dto;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.event.build.EventConvert;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownLiveEventDTO implements EventConvert {
    private String uid;
    @JsonProperty("stream_id")
    private String streamId;
    @JsonProperty("wk_channel_id")
    private String wkChannelId;
    @JsonProperty("gift_buy_channel_id")
    private String giftBuyChannelId;

    @Override
    public EventData of() {
        return new EventData().setEvent(Lary.Event.downLive).setType(Lary.EventType.cmd).setEvent(JSONKit.toJSON(this));
    }
}
