package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.standard.args.fragmentation.UploadFragArgs;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class ArgsToAbortMultipartUploadRequestConvert implements Converter<UploadFragArgs, AbortMultipartUploadRequest> {
    @Override
    public AbortMultipartUploadRequest convert(UploadFragArgs source) {
        return new AbortMultipartUploadRequest(source.getBucketName(), source.getObjectName(), source.getUploadId());
    }
}
