package cn.lary.api.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Accessors(chain = true)
public  class PaymentParamDTO {
    @NotNull
    private int businessSign;

    @NotNull
    private long payId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String subject;

    @NotNull
    private int clientType;

    @NotNull
    private Map<String,String> args;

}
