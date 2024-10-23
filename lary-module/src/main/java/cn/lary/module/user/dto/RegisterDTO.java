package cn.lary.module.user.dto;

import cn.lary.common.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO extends DTO {

    private String name;
    @NotNull(message = "zone is null")
    private String zone;
    @NotNull(message = "phone is null")
    private String phone;
    @NotNull(message = "password is null")
    @Size(min = 6,message = "password len less than 6")
    private String password;
    /**
     * 验证码
     */
    @NotNull(message = "code is null")
    private String code;
    @JsonProperty("invite_code")
    private String inviteCode;

    private String bio;

    private String email;

    private String birthday;

    @JsonProperty("id")
    private int id;

    @NotNull(message = "device_name is null")
    @JsonProperty("name")
    private String deviceName;

    @JsonProperty("flag")
    @NotNull(message = "device flag is null")
    private int flag;

    private int level;

}
