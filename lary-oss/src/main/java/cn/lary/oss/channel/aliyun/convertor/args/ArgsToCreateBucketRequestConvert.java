package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.channel.aliyun.define.args.ArgsToBucketConvert;
import cn.lary.oss.standard.args.bucket.CreateBucketArgs;
import com.aliyun.oss.model.CreateBucketRequest;

/**
 * @author paul 2024/4/17
 */

public class ArgsToCreateBucketRequestConvert  extends ArgsToBucketConvert<CreateBucketArgs, CreateBucketRequest> {
    @Override
    public CreateBucketRequest get(CreateBucketArgs source) {
        return new CreateBucketRequest(source.getBucketName());
    }
}
