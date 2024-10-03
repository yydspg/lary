package cn.lary.module.gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GiftOrderDTO {
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
    @JsonProperty("type")
    private Integer type;

    /**
     * 支付方式可以为空，若为空，即尝试从钱包扣款
     */
    @JsonProperty("pay_way")
    private Integer payWay;
}
