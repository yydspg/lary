package cn.lary.module.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NotNull(message = "uid is null")
    private String uid;

    @NotNull(message = "password is null")
    private String password;

    @JsonProperty("id")
    private String id;

    @NotNull(message = "device_name is null")
    @JsonProperty("name")
    private String name;

    @JsonProperty("model")
    private String model;

    @JsonProperty("flag")
    @NotNull(message = "device flag is null")
    private Integer flag;

    private Integer level;
}
