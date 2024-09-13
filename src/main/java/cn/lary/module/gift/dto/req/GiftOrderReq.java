package cn.lary.module.gift.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GiftOrderReq {

    @NotNull
    @JsonProperty("gift_id")
    private String giftId;

    @Min(value = 0,message = "gift num can not less than 0")
    @Max(value = Integer.MAX_VALUE,message = "gift num can not bigger than integer.Max")
    @JsonProperty("gift_num")
    private Integer giftNum;

    @NotNull
    @JsonProperty("anchor_uid")
    private String anchorUid;

    @NotNull
    @JsonProperty("gift_buy_channel_id")
    private String giftBuyChannelId;
    @NotNull
    @JsonProperty("client_type")
    @Min(value = 0,message = "client type error")
    @Max(value = 1,message = "client type error")
    private Integer clientType;
}
