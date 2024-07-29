package cn.lary.module.gift.dto;

import cn.lary.core.dto.DTO;
import cn.lary.module.gift.entity.Gift;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class GiftCreate extends DTO {

    // type name
    @NotNull(message = "type name is null")
    private String typeName;

    /**
     * virtual currency price
     */
    @Min(message = "price invalid",value = 0)
    private Integer price;

    /**
     * real pay price CNY
     */
    @Min(value = 0,message = "real price invalid")
    private Integer realPrice;

    @NotNull(message = "type name is null")
    private String name;

    private Long purchaseNum;
    @NotNull(message = "create user is null")
    private String createBy;

    /**
     * gift logo
     */
    @Pattern(regexp = "^(https?|ftp)://[\\w.-]+(?:/[\\w.-]*)*$", message = "URL invalid")
    private String pictureUrl;
}
