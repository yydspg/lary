package cn.lary.oss.channel.aliyun.convertor.domain;


import cn.lary.oss.standard.domain.fragmentation.UploadFrag;
import com.aliyun.oss.model.UploadPartResult;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class UploadPartResultConvert  implements Converter<UploadPartResult, UploadFrag> {
    @Override
    public UploadFrag convert(UploadPartResult source) {
        UploadFrag target = new UploadFrag();
        target.setPartNumber(source.getPartNumber());
        target.setEtag(source.getETag());
        return target;
    }
}
