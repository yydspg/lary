package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.standard.args.obj.ListObjArgs;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ListObjectsV2Request;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class ArgsToListObjectsRequestConvert implements Converter<ListObjArgs, ListObjectsRequest> {
    @Override
    public ListObjectsRequest convert(ListObjArgs source) {
        return new ListObjectsRequest(source.getBucketName(), source.getPrefix(), source.getMarker(), source.getDelimiter(), source.getMaxKeys());

    }
}
