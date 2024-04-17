package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.fragmentation.UploadFragCopy;
import com.aliyun.oss.model.UploadPartCopyResult;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class UploadPartCopyResultConvert  implements Converter<UploadPartCopyResult, UploadFragCopy> {
    @Override
    public UploadFragCopy convert(UploadPartCopyResult source) {
        UploadFragCopy domain = new UploadFragCopy();
        domain.setPartNumber(source.getPartNumber());
        domain.setEtag(source.getETag());
        return domain;
    }
}
