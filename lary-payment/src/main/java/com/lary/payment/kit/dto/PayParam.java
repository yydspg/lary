package com.lary.payment.kit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * @author paul 2024/1/29
 */
@Data
@ToString
public class PayParam {
    @NotNull
    @Schema(description = "交易类型",allowableValues = "ORDER,RECHARGE,TRADE")
    private String orderType;
    @NotNull
    @Schema(description = "订单号")
    private String sn;
    @NotNull
    @Schema(description = "客户端类型")
    private String clientType;
}
