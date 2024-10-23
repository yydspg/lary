package cn.lary.external.wk.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateTokenDTO {

    private int uid;

    private String token;

    @JsonProperty("device_flag")
    private int flag;

    @JsonProperty("device_level")
    private int level;
}
