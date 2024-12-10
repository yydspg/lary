package cn.lary.api.advertisement.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProviderBuildDTO {

    /**
     * 名称
     */
    private String name;

    /**
     * 总投入
     */
    private BigDecimal amount;

}
