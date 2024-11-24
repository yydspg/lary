package cn.lary.module.order.dto;

import lombok.Data;

@Data
public class GoodsOrderDTO {

    private Long goods;

    private Integer num;

    private Long sku;

    private Integer client;

    private Integer plugin;

    private Long redpacket;
}
