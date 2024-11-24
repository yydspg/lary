package cn.lary.message.external.wk.dto.user;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateTokenDTO {

    private long uid;

    private String token;

    @JSONField(format="device_flag")
    private int flag;

    @JSONField(format="device_level")
    private int level;
}
