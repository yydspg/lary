package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.fragmentation.Upload;
import cn.lary.oss.standard.domain.fragmentation.UploadFrag;
import com.aliyun.oss.model.MultipartUpload;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class MultipartUploadConvert  implements Converter<List<MultipartUpload>, List<Upload>> {
    @Override
    public List<Upload> convert(List<MultipartUpload> source) {
        if (null != source && !source.isEmpty()) {
            return source.stream().map(this::map).toList();
        }
        return Collections.emptyList();
    }
    private Upload map(MultipartUpload source) {
        Upload domain = new Upload();
        domain.setKey(source.getKey());
        domain.setUploadId(source.getUploadId());
        domain.setStorageClass(source.getStorageClass());
        domain.setInitiated(source.getInitiated());
        return domain;
    }
}
