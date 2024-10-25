package cn.lary.module.wallet.dto;

import cn.lary.module.pay.dto.BusinessPaymentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RechargeDTO extends BusinessPaymentDTO {

    @NotNull
    @Min(value = 0,message = "recharge amount not less than 0")
    @Max(value = Integer.MAX_VALUE,message = "recharge amount over limit")
    private Long amount;

    @NotNull
    private Integer client;

    @NotNull
    @JsonProperty("plugin")
    private Integer plugin;

}
