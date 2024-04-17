package cn.lary.oss.channel.aliyun.convertor.domain;


import cn.lary.oss.standard.domain.common.Owner;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class OwnerConvert implements Converter<com.aliyun.oss.model.Owner, Owner> {
    @Override
    public Owner convert(com.aliyun.oss.model.Owner source) {
        Owner attribute = new Owner();
        attribute.setId(source.getId());
        attribute.setDisplayName(source.getDisplayName());
        return attribute;
    }
}
