package cn.lary.user.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NotNull(message = "uid is null")
    private Long uid;

    @NotNull(message = "password is null")
    private String password;
    /**
     * 登陆设备 id<br>
     * 如果为空,表示新设备登陆<br>
     * 如果是新设备登陆name和model一定不为空
     */
    @JSONField(format="id")
    @NotNull(message = "device id is null")
    private Long did;
    /**
     * 登陆设备名称
     */
    @NotNull(message = "device_name is null")
    @JSONField(format="name")
    private String name;

    @NotNull(message = "phone is null")
    private String phone;

    @JSONField(format="flag")
    @NotNull(message = "device flag is null")
    private Integer flag;


    /**
     * 如果在新设备上登陆<br>
     * 如返回cmd命令，需请求deviceAdd接口,获取验证码
     * 需要手机验证码验证,此项必填
     */
    private String code;

    private String ip;
}
