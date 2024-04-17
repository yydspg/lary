package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.channel.aliyun.define.args.ArgsToBucketConvert;
import cn.lary.oss.standard.args.fragmentation.UploadFragArgs;
import cn.lary.oss.standard.domain.common.Frag;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.PartETag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class ArgsToCompleteMultipartUploadRequestConvert extends ArgsToBucketConvert<UploadFragArgs, CompleteMultipartUploadRequest> {
    @Override
    public CompleteMultipartUploadRequest get(UploadFragArgs source) {
        return new CompleteMultipartUploadRequest(source.getBucketName(),source.getObjectName(),source.getUploadId(),this.map(source.getFrags()));
    }
    private List<PartETag> map(List<Frag> attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            return attributes.stream().map(item -> new PartETag(item.getPartNumber(), item.getEtag())).toList();
        }
        return Collections.emptyList();
    }
}
