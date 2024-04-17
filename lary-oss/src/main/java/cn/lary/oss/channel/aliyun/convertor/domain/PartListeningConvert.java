package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.common.Frag;
import cn.lary.oss.standard.domain.fragmentation.ListFrag;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PartSummary;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class PartListeningConvert implements Converter<PartListing, ListFrag> {
    private final Converter<List<PartSummary>, List<Frag>> toDomain = new PartSummaryConvert();

    @Override
    public ListFrag convert(PartListing source) {
        ListFrag domain = new ListFrag();
        domain.setStorageClass(source.getStorageClass());
        domain.setMaxParts(source.getMaxParts());
        domain.setPartNumberMarker(source.getPartNumberMarker());
        domain.setNextPartNumberMarker(source.getNextPartNumberMarker());
        domain.setTruncated(source.isTruncated());
        domain.setFrags(toDomain.convert(source.getParts()));
        domain.setUploadId(source.getUploadId());
        domain.setBucketName(source.getBucketName());
        domain.setObjectName(source.getKey());

        return domain;
    }
}
