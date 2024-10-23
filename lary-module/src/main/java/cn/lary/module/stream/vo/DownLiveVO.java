package cn.lary.module.stream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownLiveVO {

    private Integer watchNum;
    private Integer newFansNum;
    private Integer starNum;
    private Integer watchFanNum;
    private String duration;
    private String token;
    private int event;
}
