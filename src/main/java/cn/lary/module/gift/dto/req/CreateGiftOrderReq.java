package cn.lary.module.gift.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateGiftOrderReq {
    @JsonProperty("gift_id")
    private String giftId;
    @JsonProperty("gift_num")
    private Integer giftNum;
    @JsonProperty("anchor_uid")
    private String anchorUid;
}
