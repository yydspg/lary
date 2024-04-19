package cn.lary.oss.channel.aliyun.define.pool;

import cn.lary.core.model.base.AbstractObjPool;
import com.aliyun.oss.OSS;
import org.springframework.stereotype.Component;

/**
 * @author paul 2024/4/18
 */
@Component
public class AliyunClientPool extends AbstractObjPool<OSS> {
    public AliyunClientPool(AliyunClientFactory factory) {
        super(factory,factory.getProp().getPool());
    }
}
