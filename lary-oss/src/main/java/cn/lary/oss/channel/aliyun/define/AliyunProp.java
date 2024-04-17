package cn.lary.oss.channel.aliyun.define;

import cn.lary.oss.standard.prop.AbstractProp;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/18
 */
@Getter
@Setter
public class AliyunProp extends AbstractProp {
    private String region;
    private String role;
}
