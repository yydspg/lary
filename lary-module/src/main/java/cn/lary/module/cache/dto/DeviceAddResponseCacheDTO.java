package cn.lary.module.cache.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DeviceAddResponseCacheDTO extends CacheDTO {

    @JSONField(format="name")
    private String name;

    private int flag;

    @NotNull
    @JSONField(format="code")
    private String code;

    public static DeviceAddResponseCacheDTO of(Map map) {

        DeviceAddResponseCacheDTO dto = new DeviceAddResponseCacheDTO();
        dto.setName(map.get("name").toString());
        dto.setFlag((Integer) map.get("flag"));
        dto.setCode(map.get("code").toString());
        return dto;
    }
}
