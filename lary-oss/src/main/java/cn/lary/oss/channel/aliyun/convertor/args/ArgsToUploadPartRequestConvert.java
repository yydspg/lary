package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.channel.aliyun.define.args.ArgsToBucketConvert;
import cn.lary.oss.standard.args.fragmentation.UploadFragArgs;
import com.aliyun.oss.model.UploadPartRequest;

/**
 * @author paul 2024/4/17
 */

public class ArgsToUploadPartRequestConvert extends ArgsToBucketConvert<UploadFragArgs,UploadPartRequest> {

    @Override
    public UploadPartRequest get(UploadFragArgs source) {
        UploadPartRequest request = new UploadPartRequest();

        request.setBucketName(source.getBucketName());
        request.setKey(source.getObjectName());
        request.setUploadId(source.getUploadId());
        request.setPartNumber(source.getPartNumber());
        request.setPartSize(source.getPartSize());
        request.setInputStream(source.getInputStream());
        request.setMd5Digest(source.getMd5Digest());

        return request;
    }
}
