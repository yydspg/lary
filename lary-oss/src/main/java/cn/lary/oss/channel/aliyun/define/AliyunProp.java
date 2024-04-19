package cn.lary.oss.channel.aliyun.define;

import cn.lary.oss.constant.Oss;
import cn.lary.oss.standard.prop.AbstractProp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author paul 2024/4/18
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = Oss.Channel.ALIYUN)
public class AliyunProp extends AbstractProp {
    private String region;
    private String role;
}
