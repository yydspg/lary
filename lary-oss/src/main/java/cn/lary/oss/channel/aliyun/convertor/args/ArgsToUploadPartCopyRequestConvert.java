package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.channel.aliyun.define.args.ArgsToBucketConvert;
import cn.lary.oss.standard.args.fragmentation.UploadFragArgs;
import cn.lary.oss.standard.args.fragmentation.UploadFragCopyArgs;
import com.aliyun.oss.model.UploadPartCopyRequest;

/**
 * @author paul 2024/4/17
 */

public class ArgsToUploadPartCopyRequestConvert extends ArgsToBucketConvert<UploadFragCopyArgs, UploadPartCopyRequest> {
    @Override
    public UploadPartCopyRequest get(UploadFragCopyArgs source) {
        UploadPartCopyRequest target = new UploadPartCopyRequest();

        target.setSourceBucketName(source.getBucketName());
        target.setSourceKey(source.getObjectName());
        target.setUploadId(source.getUploadId());
        target.setPartNumber(source.getPartNumber());
        target.setBucketName(source.getDestinationBucketName());
        target.setKey(source.getDestinationObjectName());
        target.setMatchingETagConstraints(source.getMatchingETagConstraints());
        target.setNonmatchingETagConstraints(source.getNonmatchingEtagConstraints());
        target.setModifiedSinceConstraint(source.getModifiedSinceConstraint());
        target.setUnmodifiedSinceConstraint(source.getUnmodifiedSinceConstraint());

        return target;
    }
}
