package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.constant.Oss;
import cn.lary.oss.standard.domain.fragmentation.UploadFragOps;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class CompleteMultipartUploadResultConvert implements Converter<CompleteMultipartUploadResult, UploadFragOps> {
    @Override
    public UploadFragOps convert(CompleteMultipartUploadResult source) {
        UploadFragOps uploadFragOps = new UploadFragOps();
        uploadFragOps.setEtag(source.getETag());
        uploadFragOps.setBucketName(source.getBucketName());
        uploadFragOps.setVersionId(source.getVersionId());
        uploadFragOps.setObjectName(source.getKey());
        uploadFragOps.setOps(Oss.FragProcessState.OVER);
        return uploadFragOps;
    }
}
