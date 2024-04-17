package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.standard.args.fragmentation.TabulationFragArgs;
import com.aliyun.oss.model.ListPartsRequest;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class ArgsToListPartsRequestConvert implements Converter<TabulationFragArgs, ListPartsRequest> {
    @Override
    public ListPartsRequest convert(TabulationFragArgs source) {

        ListPartsRequest request = new ListPartsRequest(source.getBucketName(), source.getObjectName(), source.getUploadId());
        request.setMaxParts(source.getMaxParts());
        request.setPartNumberMarker(source.getPartNumberMarker());
        return request;
    }
}
