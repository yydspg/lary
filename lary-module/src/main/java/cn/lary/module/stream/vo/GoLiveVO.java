package cn.lary.module.stream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoLiveVO {

    public String goLiveToken;

    public String identify;

    public int event;

}
