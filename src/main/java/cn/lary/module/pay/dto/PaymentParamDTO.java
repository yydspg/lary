package cn.lary.module.pay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public  class PaymentParamDTO {
    @NotNull
    private int businessSign;

    @NotNull
    private long payId;

    @NotNull
    private long amount;

    @NotNull
    private String subject;

    @NotNull
    private int clientType;

    @NotNull
    private Map<String,String> args;

}
