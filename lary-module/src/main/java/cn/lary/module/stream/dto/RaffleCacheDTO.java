package cn.lary.module.stream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class RaffleCacheDTO {

    private boolean isFan;

    private int fanLevel;

    /**
     * 抽奖类型
     */
    private int type;
    /**
     * 抽奖物品描述
     */
    private String content;
    /**
     * 每份奖品物品数目，如果为vc,此项无意义
     */
    @JsonProperty("item_num")
    private int itemNum;
    /**
     * 抽奖标题
     */
    private String title;
    /**
     * 抽奖数量
     */
    private int num;
    /**
     * 抽奖持续时间,单位 s
     */
    private long duration;
    /**
     * 发送消息，如果为null 表示不发送
     */
    private String message;
    /**
     * 抽奖门槛
     */
    private long cost;

    /**
     * 抽奖流id
     */
    private int id;
    /**
     * 如果是虚拟货币，就不为0
     */
    private long totalAmount;

    public static RaffleCacheDTO of(Map map) {
        RaffleCacheDTO dto = new RaffleCacheDTO();
        dto.setType(Integer.parseInt(map.get("type").toString()));
        dto.setContent(map.get("content").toString());
        dto.setTitle(map.get("title").toString());
        dto.setNum(Integer.parseInt(map.get("num").toString()));
        dto.setDuration(Long.parseLong(map.get("duration").toString()));
        dto.setFanLevel(Integer.parseInt(map.get("fanLevel").toString()));
        dto.setFan(Boolean.parseBoolean(map.get("isFan").toString()));
        dto.setCost(Long.parseLong(map.get("cost").toString()));
        dto.setId(Integer.parseInt(map.get("id").toString()));
        dto.setTotalAmount(Long.parseLong(map.get("totalAmount").toString()));
        return dto;
    }
}
