package cn.lary.module.stream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DownLiveVO {

    private int watchNum;
    private int newFansNum;
    private int starNum;
    private int watchFanNum;
    private String duration;
    private String token;
    private long event;

    public DownLiveVO() {}


}
