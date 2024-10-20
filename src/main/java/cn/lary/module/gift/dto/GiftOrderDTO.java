package cn.lary.module.gift.dto;

import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GiftOrderDTO  extends BusinessPaymentDTO {
    /**
     * gift id
     */
    @NotNull
    @JsonProperty("id")
    private Integer id;

    /**
     * gift buy num
     */
    @Min(value = 0,message = "gift num can not less than 0")
    @Max(value = Integer.MAX_VALUE,message = "gift num can not bigger than integer.Max")
    @JsonProperty("num")
    private Integer num;

    /**
     * anchor uid
     */
    @NotNull
    @JsonProperty("to_uid")
    private Integer toUid;

    @NotNull
    @JsonProperty("client")
    private Integer client;

    /**
     * 支付方式可以为空，若为空，即尝试从钱包扣款
     */
    @JsonProperty("plugin")
    private Integer plugin;


}
