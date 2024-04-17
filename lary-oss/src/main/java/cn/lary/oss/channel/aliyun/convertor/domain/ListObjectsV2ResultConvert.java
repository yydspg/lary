package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.obj.ListV2Obj;
import cn.lary.oss.standard.domain.obj.Obj;
import cn.lary.oss.util.ConvertKit;
import com.aliyun.oss.model.ListObjectsV2Result;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class ListObjectsV2ResultConvert implements Converter<ListObjectsV2Result, ListV2Obj> {
    @Override
    public ListV2Obj convert(ListObjectsV2Result source) {
        List<Obj> summaries = ConvertKit.convert(source.getObjectSummaries(), new ObjectSummaryConvert(source.getDelimiter()));

        ListV2Obj target = new ListV2Obj();
        target.setSummaries(summaries);
        target.setTruncated(source.isTruncated());
        target.setKeyCount(source.getKeyCount());
        target.setNextContinuationToken(source.getNextContinuationToken());
        target.setContinuationToken(source.getContinuationToken());
        target.setPrefix(source.getPrefix());
        target.setMarker(source.getStartAfter());
        target.setDelimiter(source.getDelimiter());
        target.setMaxKeys(source.getMaxKeys());
        target.setEncodingType(source.getEncodingType());
        target.setBucketName(source.getBucketName());


        return target;
    }
}
