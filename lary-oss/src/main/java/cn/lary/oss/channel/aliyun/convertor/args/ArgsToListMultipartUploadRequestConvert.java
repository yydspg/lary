package cn.lary.oss.channel.aliyun.convertor.args;


import cn.lary.oss.standard.args.fragmentation.TabulationUploadFragArgs;
import com.aliyun.oss.model.ListMultipartUploadsRequest;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class ArgsToListMultipartUploadRequestConvert implements Converter<TabulationUploadFragArgs,ListMultipartUploadsRequest> {

    @Override
    public ListMultipartUploadsRequest convert(TabulationUploadFragArgs source) {
        ListMultipartUploadsRequest target = new ListMultipartUploadsRequest(source.getBucketName());
        target.setDelimiter(source.getDelimiter());
        target.setPrefix(source.getPrefix());
        target.setMaxUploads(source.getMaxUploads());
        target.setKeyMarker(source.getKeyMarker());
        target.setUploadIdMarker(source.getUploadIdMarker());
        target.setEncodingType(source.getEncodingType());
        return target;
    }
}
