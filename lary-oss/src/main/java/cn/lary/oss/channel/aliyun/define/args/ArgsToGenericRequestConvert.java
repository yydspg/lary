package cn.lary.oss.channel.aliyun.define.args;

import cn.lary.oss.standard.args.common.BucketArgs;
import com.aliyun.oss.model.GenericRequest;

/**
 * @author paul 2024/4/17
 */

public class ArgsToGenericRequestConvert<S extends BucketArgs> extends ArgsToBaseConvert<S, GenericRequest> {
    @Override
    public GenericRequest get(S source) {
        return new GenericRequest(source.getBucketName());
    }
}
