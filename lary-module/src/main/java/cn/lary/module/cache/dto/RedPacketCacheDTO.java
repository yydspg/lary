package cn.lary.module.cache.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class RedPacketCacheDTO extends AbstractCacheDTO {
    /**
     * 开始时间
     */
    private String startAt;

    /**
     * 红包标题
     */
    @NotNull
    private String title;
    /**
     * 人数
     */
    @Min(value = 1,message = "red packet num less than 0")
    private int num;
    /**
     * 口令消息，如果为null 表示不发送
     */
    private String message;
    /**
     * 每一份红包vc数目
     */
    private long cost;

    public static RedPacketCacheDTO of(Map map) {
        RedPacketCacheDTO dto = new RedPacketCacheDTO();
        dto.setStartAt(map.get("startAt").toString());
        dto.setTitle(map.get("title").toString());
        dto.setNum(Integer.parseInt(map.get("num").toString()));
        dto.setMessage(map.get("message").toString());
        dto.setCost(Long.parseLong(map.get("cost").toString()));
        return dto;
    }
}
