package cn.lary.module.pay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public abstract class PayParam {
    @NotNull
    private Integer biz;

    @NotNull
    private Long payId;

    @NotNull
    private Long price;

    @NotNull
    private String subject;

    @NotNull
    private Integer clientType;

    @NotNull
    private Map<String,String> args;

    public  abstract PayParam of(Long payId,Long price,int clientType, Map<String,String> args);
}
