package cn.lary.module.raffle.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RaffleEventDTO {


    /**
     * 抽奖类型
     */
    private Integer category;


    /**
     * 抽奖人数
     */
    private Integer num;
    /**
     * 如果为现金抽奖,此字段不为空
     */
    private BigDecimal amount;

    /**
     * 抽奖标题
     */
    private String title;

    /**
     * 抽奖物品描述
     */
    private String content;

    /**
     * 物品数目
     */
    private Integer itemNum;


    private Boolean fan;

    private Integer level;
    /**
     * 持续时间
     */
    private Long duration;

    /**
     * 发送消息，如果为null 表示不发送
     */
    private String message;


}
