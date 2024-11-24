package cn.lary.stream.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RedPacketDTO {
    /**
     * 持续时间
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
    @Min(value = 1,message = "red packet num less than 0")
    private Integer num;
    /**
     * 口令消息，如果为null 表示不发送
     */
    private String message;
    /**
     * 每一份红包vc数目
     */
    private Long cost;
}
