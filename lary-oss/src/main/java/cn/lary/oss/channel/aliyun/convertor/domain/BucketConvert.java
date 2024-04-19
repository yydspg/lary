package cn.lary.oss.channel.aliyun.convertor.domain;


import cn.lary.oss.standard.domain.bucket.SysBucket;
import cn.lary.oss.standard.domain.common.Owner;
import com.aliyun.oss.model.Bucket;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public class BucketConvert implements Converter<Bucket, SysBucket> {
    @Override
    public SysBucket convert(Bucket source) {
        SysBucket sysBucket = new SysBucket();
        sysBucket.setBucketName(source.getName());
        sysBucket.setCreateTime(source.getCreationDate());
        Owner owner = new Owner();
        owner.setId(source.getOwner().getId());
        owner.setDisplayName(source.getOwner().getDisplayName());
        sysBucket.setOwner(owner);
        return sysBucket;
    }
}
