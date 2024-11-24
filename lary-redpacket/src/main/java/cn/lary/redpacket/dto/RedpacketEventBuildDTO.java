package cn.lary.redpacket.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RedpacketEventBuildDTO {
    /**
     * 持续时间,以s为单位
     */
    @NotNull
    private Long duration;
    /**
     * 红包标题
     */
    @NotNull
    private String title;
    /**
     * 人数
     */
    @Min(value = 1,message = "redpacket amount less than 0")
    @Max(value = 30000,message = "redpacket amount reach limit")
    private Integer num;

    /**
     * 口令消息，如果为null 表示不发送
     */
    private String message;

    /**
     * 红包总金额
     */
    private BigDecimal totalAmount;

    /**
     * 每份金额,需要结合category
     */
    private BigDecimal amount;


    /**
     * 开始时间
     */
    private LocalDateTime startAt;

}
