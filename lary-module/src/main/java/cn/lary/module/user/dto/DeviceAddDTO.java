package cn.lary.module.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceAddDTO {

    @JSONField(format="name")
    private String name;

    @JSONField(format="flag")
    private int flag;

    private long uid;

    private String phone;
}
