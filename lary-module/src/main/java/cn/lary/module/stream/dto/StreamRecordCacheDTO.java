package cn.lary.module.stream.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StreamRecordCacheDTO {

    private Integer watchNum;
    private Integer newFansNum;
    private Integer starNum;
    private Integer watchFanNum;

    public static StreamRecordCacheDTO of(Map<Object,Object> map) {
        StreamRecordCacheDTO vo = new StreamRecordCacheDTO();
        vo.setWatchNum((Integer) map.get("watchNum"));
        vo.setNewFansNum((Integer) map.get("newFansNum"));
        vo.setStarNum((Integer) map.get("starNum"));
        vo.setWatchFanNum((Integer) map.get("watchFanNum"));
        return vo;
    }
}

