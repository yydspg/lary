package cn.lary.module.common.server;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lary.shortno")
public class ShortNoConfig {
    private boolean numOn; // 是否开启 数字短编号
    private int numLen; // 数字短编号的长度
    private boolean editOff; // 是否关闭短编号编辑
}
