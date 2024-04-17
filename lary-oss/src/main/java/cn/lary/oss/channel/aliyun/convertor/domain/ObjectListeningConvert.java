package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.obj.ListObj;
import cn.lary.oss.standard.domain.obj.Obj;
import cn.lary.oss.util.ConvertKit;
import com.aliyun.oss.model.ObjectListing;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class ObjectListeningConvert implements Converter<ObjectListing, ListObj> {
    @Override
    public ListObj convert(ObjectListing source) {

        List<Obj> summaries = ConvertKit.convert(source.getObjectSummaries(), new ObjectSummaryConvert(source.getDelimiter()));

        ListObj target = new ListObj();
        target.setSummaries(summaries);
        target.setNextMarker(source.getNextMarker());
        target.setTruncated(source.isTruncated());
        target.setPrefix(source.getPrefix());
        target.setMarker(source.getMarker());
        target.setDelimiter(source.getDelimiter());
        target.setMaxKeys(source.getMaxKeys());
        target.setEncodingType(source.getEncodingType());
        target.setBucketName(source.getBucketName());

        return target;
    }
}
