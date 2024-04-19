package cn.lary.oss.channel.aliyun.define.pool;

import cn.lary.oss.channel.aliyun.define.AliyunProp;
import cn.lary.oss.standard.factory.AbstractClientFactory;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.STSAssumeRoleSessionCredentialsProvider;
import org.apache.commons.pool2.PooledObject;
import org.springframework.stereotype.Component;

/**
 * @author paul 2024/4/18
 */
@Component
public class AliyunClientFactory extends AbstractClientFactory<OSS> {
    public AliyunClientFactory(AliyunProp aliyunProp) {
        super(aliyunProp);
    }

    @Override
    public OSS create() throws Exception {
        AliyunProp prop = (AliyunProp) super.getProp();
        // TODO 2024/4/20 : 出现问题,拿不到连接
//        STSAssumeRoleSessionCredentialsProvider credentialsProvider = CredentialsProviderFactory
//                .newSTSAssumeRoleSessionCredentialsProvider(
//                        prop.getRegion(),
//                        prop.getAccessKey(),
//                        prop.getSecretKey(),
//                        prop.getRole());
//
//        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();

        return new OSSClientBuilder().build(getProp().getEndpoint(), getProp().getAccessKey(), getProp().getSecretKey());
    }

    @Override
    public void destroyObject(PooledObject<OSS> p) throws Exception {
        p.getObject().shutdown();
    }
}
