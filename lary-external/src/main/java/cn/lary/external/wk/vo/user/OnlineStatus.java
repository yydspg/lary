package cn.lary.external.wk.vo.user;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnlineStatus {
    private Long uid;
    @JSONField(format="device_flag")
    private byte deviceFlag;
    private int online;
}
