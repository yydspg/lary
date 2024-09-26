package cn.lary.pkg.wk.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
public class UpdateTokenDTO {
    private String uid;
    private String token;
    @JsonProperty("device_flag")
    private int deviceFlag;
    @JsonProperty("device_level")
    private int deviceLevel;
}
