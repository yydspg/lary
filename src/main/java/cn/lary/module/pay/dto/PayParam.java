package cn.lary.module.pay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@ToString
@Accessors(chain = true)
public class PayParam {
    @NotNull
    private Integer biz;

    @NotNull
    private String sn;

    @NotNull
    private Double price;

    @NotNull
    private String subject;

    @NotNull
    private String clientType;

    @NotNull
    private Map<String,String> args;

}
