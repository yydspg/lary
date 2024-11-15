package cn.lary.module.gift.dto;

import cn.lary.module.pay.dto.BusinessPaymentDTO;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GiftOrderDTO  extends BusinessPaymentDTO {
    /**
     * gift id
     */
    @NotNull
    @JSONField(format="id")
    private Integer id;

    /**
     * gift buy num
     */
    @Min(value = 0,message = "gift num can not less than 0")
    @Max(value = Integer.MAX_VALUE,message = "gift num can not bigger than integer.Max")
    @JSONField(format="num")
    private Integer num;

    /**
     * anchor uid
     */
    @NotNull
    @JSONField(format="to_uid")
    private Long toUid;

    @NotNull
    @JSONField(format="client")
    private Integer client;

    /**
     * 支付方式可以为空，若为空，即尝试从钱包扣款
     */
    @JSONField(format="plugin")
    private Integer plugin;


}
