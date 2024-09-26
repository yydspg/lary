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
    private String id;

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
    private String toUid;

    @NotNull
    @JsonProperty("type")
    private Integer type;

    @NotNull
    @JsonProperty("pay_way")
    private Integer payWay;
}
