package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.fragmentation.UploadFragOps;
import com.aliyun.oss.model.DeleteObjectsResult;
import org.springframework.core.convert.converter.Converter;

import java.util.Collections;
import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class DeleteObjectsResultConvert implements Converter<DeleteObjectsResult, List<UploadFragOps>> {
    @Override
    public List<UploadFragOps> convert(DeleteObjectsResult source) {
        List<String> items = source.getDeletedObjects();

        if (null != items) {
            return items.stream().map(UploadFragOps::new).toList();
        }
        return Collections.emptyList();
    }
}
