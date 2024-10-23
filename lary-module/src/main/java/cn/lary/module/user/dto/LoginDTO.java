package cn.lary.module.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NotNull(message = "uid is null")
    private Integer uid;

    @NotNull(message = "password is null")
    private String password;
    /**
     * 登陆设备 id<br>
     * 如果为空,表示新设备登陆<br>
     * 如果是新设备登陆name和model一定不为空
     */
    @JsonProperty("id")
    @NotNull(message = "device id is null")
    private Integer id;
    /**
     * 登陆设备名称
     */
    @NotNull(message = "device_name is null")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "phone is null")
    private String phone;

    @JsonProperty("flag")
    @NotNull(message = "device flag is null")
    private Integer flag;


    /**
     * 如果在新设备上登陆<br>
     * 如返回cmd命令，需请求deviceAdd接口,获取验证码
     * 需要手机验证码验证,此项必填
     */
    private String code;
}
