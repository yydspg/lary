package cn.lary.module.user.dto.req;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterReq extends DTO {
    private String name;
    @NotNull(message = "zone is null")
    private String zone;
    @NotNull(message = "phone is null")
    private String phone;
    @NotNull(message = "password is null")
    @Size(min = 6,message = "password len less than 6")
    private String password;
    @NotNull(message = "code is null")
    private String code;
    // 注册设备标记
    private byte flag;
    @JsonProperty("invite_code")
    // 邀请码
    private String inviteCode;
    @JsonProperty("device")
    private DeviceReq device;

}
