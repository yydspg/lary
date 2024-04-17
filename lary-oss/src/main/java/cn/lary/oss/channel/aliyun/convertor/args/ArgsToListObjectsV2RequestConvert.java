package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.standard.args.obj.ListV2ObjArgs;
import com.aliyun.oss.model.ListObjectsV2Request;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class ArgsToListObjectsV2RequestConvert implements Converter<ListV2ObjArgs, ListObjectsV2Request> {

    @Override
    public ListObjectsV2Request convert(ListV2ObjArgs source) {
        return new ListObjectsV2Request(source.getBucketName(), source.getPrefix(), source.getContinuationToken(), source.getMarker(), source.getDelimiter(), source.getMaxKeys(), source.getEncodingType(), source.getFetchOwner());
    }
}
