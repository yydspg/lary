package cn.lary.module.order.dto;

import cn.lary.module.pay.dto.BusinessPaymentDTO;
import lombok.Data;

@Data
public class GoodsOrderDTO extends BusinessPaymentDTO {

    private Long goods;

    private Integer num;

    private Long sku;

    private Integer client;

    private Integer plugin;

    private Long redpacket;
}
