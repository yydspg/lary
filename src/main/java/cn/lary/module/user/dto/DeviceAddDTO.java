package cn.lary.module.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceAddDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("model")
    private String model;
}
