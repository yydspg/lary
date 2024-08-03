package cn.lary.module.common.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "register")
public class RegisterConfig {
    public boolean offe; //是否关闭注册
    public boolean onlyChina; // 是否仅中国手机号可注册
    public boolean stickerAddOff;
    public boolean usernameOn; //是否开启用户名注册
}
