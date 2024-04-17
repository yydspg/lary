package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.common.Owner;
import cn.lary.oss.standard.domain.obj.Obj;
import com.aliyun.oss.model.OSSObjectSummary;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class ObjectSummaryConvert implements Converter<OSSObjectSummary, Obj> {
    private final String delimiter;
    public ObjectSummaryConvert(String delimiter) {
        this.delimiter = delimiter;
    }
    @Override
    public Obj convert(OSSObjectSummary source) {
        Obj target = new Obj();
        target.setBucketName(source.getBucketName());
        target.setObjectName(source.getKey());
        target.setETag(source.getETag());
        target.setSize(source.getSize());
        target.setLastModified(source.getLastModified());
        target.setStorageClass(source.getStorageClass());

        if (ObjectUtils.isNotEmpty(source.getOwner())) {
            Owner ownerAttributeDomain = new Owner();
            ownerAttributeDomain.setId(ownerAttributeDomain.getId());
            ownerAttributeDomain.setDisplayName(ownerAttributeDomain.getDisplayName());
            target.setOwner(ownerAttributeDomain);
        }

        target.setDir(StringUtils.isNotBlank(this.delimiter) && StringUtils.contains(source.getKey(), this.delimiter));

        return target;
    }
}
