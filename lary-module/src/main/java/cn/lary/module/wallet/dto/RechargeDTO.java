package cn.lary.module.wallet.dto;

import cn.lary.module.pay.dto.BusinessPaymentDTO;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeDTO extends BusinessPaymentDTO {

    @NotNull
    @Min(value = 0,message = "recharge amount not less than 0")
    @Max(value = Integer.MAX_VALUE,message = "recharge amount over limit")
    private BigDecimal amount;

    @NotNull
    private Integer client;

    @NotNull
    @JSONField(format="plugin")
    private Integer plugin;

}
