package cn.lary.message.external.wk.vo.user;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class OnlineStatus {
    private Long uid;
    @JSONField(format="device_flag")
    private byte deviceFlag;
    private int online;
}
