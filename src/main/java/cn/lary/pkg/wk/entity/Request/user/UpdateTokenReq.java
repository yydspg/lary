package cn.lary.pkg.wk.entity.Request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateTokenReq {
    private String uid;
    private String token;
    @JsonProperty("device_flag")
    private byte deviceFlag;
    @JsonProperty("device_level")
    private byte deviceLevel;
}
