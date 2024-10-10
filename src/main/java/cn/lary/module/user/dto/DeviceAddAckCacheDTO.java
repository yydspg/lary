package cn.lary.module.user.dto;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DeviceAddAckCacheDTO extends DTO {
    @JsonProperty("name")
    private String name;
    @JsonProperty("model")
    private String model;
    @NotNull
    @JsonProperty("code")
    private String code;

    public static DeviceAddAckCacheDTO of(Map map) {

        DeviceAddAckCacheDTO dto = new DeviceAddAckCacheDTO();
        dto.setName(map.get("name").toString());
        dto.setModel(map.get("model").toString());
        dto.setCode(map.get("code").toString());
        return dto;
    }
}
