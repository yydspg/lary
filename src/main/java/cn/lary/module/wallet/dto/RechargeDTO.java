package cn.lary.module.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RechargeDTO {

    @NotNull
    @Min(value = 0,message = "recharge amount not less than 0")
    @Max(value = Integer.MAX_VALUE,message = "recharge amount over limit")
    private Long sum;

    @NotNull
    private Integer clientType;
    @NotNull
    @JsonProperty("pay_way")
    private Integer payWay;
}
