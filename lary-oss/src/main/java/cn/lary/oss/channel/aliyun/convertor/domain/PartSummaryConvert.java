package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.common.Frag;
import com.aliyun.oss.model.PartSummary;
import org.springframework.core.convert.converter.Converter;

import java.util.Collections;
import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class PartSummaryConvert implements Converter<List<PartSummary>, List<Frag>> {

    @Override
    public List<Frag> convert(List<PartSummary> source) {
        if (null != source && !source.isEmpty()) {
            return source.stream().map(this::map).toList();
        }
        return Collections.emptyList();
    }
    private Frag map(PartSummary source) {
        Frag domain = new Frag();
        domain.setPartSize(source.getSize());
        domain.setLastModifiedDate(source.getLastModified());
        domain.setPartNumber(source.getPartNumber());
        domain.setEtag(source.getETag());
        return domain;
    }
}
