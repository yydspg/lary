package cn.lary.module.user.dto;

import cn.lary.common.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DeviceLoginCacheDTO extends DTO {

    @JsonProperty("id")
    private long id;

    @NotNull(message = "device_name is null")
    @JsonProperty("name")
    private String name;

    @JsonProperty("flag")
    @NotNull(message = "device flag is null")
    private int flag;

    private int level;

    public static DeviceLoginCacheDTO of(Map map) {
        DeviceLoginCacheDTO dto = new DeviceLoginCacheDTO();
        dto.setId((Integer) map.get("id"));
        dto.setName((String) map.get("name"));
        dto.setFlag((Integer) map.get("flag"));
        dto.setLevel((Integer) map.get("level"));
        return dto;
    }
}
