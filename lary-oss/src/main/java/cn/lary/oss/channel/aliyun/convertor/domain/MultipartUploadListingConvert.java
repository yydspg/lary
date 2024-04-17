package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.fragmentation.ListFragUpload;
import cn.lary.oss.standard.domain.fragmentation.Upload;
import cn.lary.oss.standard.domain.fragmentation.UploadFrag;
import com.aliyun.oss.model.MultipartUpload;
import com.aliyun.oss.model.MultipartUploadListing;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class MultipartUploadListingConvert  implements Converter<MultipartUploadListing, ListFragUpload> {
    private final Converter<List<MultipartUpload>, List<Upload>> toDomain = new MultipartUploadConvert();

    @Override
    public ListFragUpload convert(MultipartUploadListing source) {
        return null;
    }
}
