package cn.lary.message.external.wk.dto.user;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class DeviceQuitDTO {

    private long uid;

    @JSONField(format="device_flag")
    private int deviceFlag;
}
