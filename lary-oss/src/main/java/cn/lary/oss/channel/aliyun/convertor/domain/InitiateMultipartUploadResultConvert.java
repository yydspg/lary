package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.constant.Oss;
import cn.lary.oss.standard.domain.fragmentation.UploadFragOps;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class InitiateMultipartUploadResultConvert implements Converter<InitiateMultipartUploadResult, UploadFragOps> {
    @Override
    public UploadFragOps convert(InitiateMultipartUploadResult source) {
        UploadFragOps uploadFragOps = new UploadFragOps();
        uploadFragOps.setUploadId(source.getUploadId());
        uploadFragOps.setBucketName(source.getBucketName());
        uploadFragOps.setObjectName(source.getKey());
        uploadFragOps.setOps(Oss.FragProcessState.INIT);
        return uploadFragOps;
    }
}
