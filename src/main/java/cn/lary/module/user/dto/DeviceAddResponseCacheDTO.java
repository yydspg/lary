package cn.lary.module.user.dto;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DeviceAddResponseCacheDTO extends DTO {

    @JsonProperty("name")
    private String name;

    private int flag;

    @NotNull
    @JsonProperty("code")
    private String code;

    public static DeviceAddResponseCacheDTO of(Map map) {

        DeviceAddResponseCacheDTO dto = new DeviceAddResponseCacheDTO();
        dto.setName(map.get("name").toString());
        dto.setFlag((Integer) map.get("flag"));
        dto.setCode(map.get("code").toString());
        return dto;
    }
}
