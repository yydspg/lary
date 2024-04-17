package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.channel.aliyun.define.args.ArgsToBucketConvert;
import cn.lary.oss.standard.args.fragmentation.UploadFragArgs;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;

/**
 * @author paul 2024/4/17
 */

public class ArgsToInitiateMultipartUploadRequestConvert extends ArgsToBucketConvert<UploadFragArgs, InitiateMultipartUploadRequest> {
    @Override
    public InitiateMultipartUploadRequest get(UploadFragArgs source) {
        return new InitiateMultipartUploadRequest(source.getBucketName(),source.getObjectName());
    }
}
