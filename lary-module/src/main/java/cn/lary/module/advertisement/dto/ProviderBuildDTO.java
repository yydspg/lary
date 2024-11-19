package cn.lary.module.advertisement.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProviderBuildDTO {

    /**
     * 名称
     */
    private Long name;

    /**
     * 总投入
     */
    private BigDecimal amount;

}
