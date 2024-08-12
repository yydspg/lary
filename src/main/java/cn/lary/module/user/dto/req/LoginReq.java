package cn.lary.module.user.dto.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginReq {
    @NotNull(message = "username is null")
    private String username;
    @NotNull(message = "password is null")
    private String password;
    @Min(value = 0,message = "flag can not less than 0 ")
    @Max(value = 1,message = "flag can not bigger than 1")
    private byte flag;
    @JsonProperty("device")
    private DeviceReq device;
}
