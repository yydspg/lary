package cn.lary.module.cache.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeviceLoginCacheDTO extends CacheDTO {

    @JSONField(format="id")
    private long id;

    @NotNull(message = "device_name is null")
    @JSONField(format="name")
    private String name;

    @JSONField(format="flag")
    @NotNull(message = "device flag is null")
    private int flag;

    private int level;

}
