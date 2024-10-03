package cn.lary.module.stream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 抽奖流是 ： 中奖的每人所得：itemNum * vcCost
 * 主播所花费： 中奖每人所得 * num
 */
@Data
public class RaffleDTO {
    /**
     * 抽奖类型
     */
    @NotNull
    private Integer type;
    /**
     * 抽奖物品描述
     */
    private String content;
    /**
     * 抽奖标题
     */
    private String title;
    /**
     * 每份奖品物品数目，如果为vc,此项无意义
     */
    @JsonProperty("item_num")
    private Integer itemNum;

    /**
     * 抽奖数量
     */
    private Integer num;
    /**
     * 如果以虚拟币抽奖，此项不可为空
     */
    private Long vcCost;
    /**
     * 抽奖持续时间,单位 s
     */
    private Long duration;

    @JsonProperty("is_fan")
    private Boolean isFan;

    @JsonProperty("fan_level")
    private Integer fanLevel;
    /**
     * 发送消息，如果为null 表示不发送
     */
    private String message;
    /**
     * 抽奖门槛，如果为null,表示无需
     */
    private Long cost;

}
