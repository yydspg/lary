package cn.lary.oss.channel.aliyun.convertor.domain;

import cn.lary.oss.standard.domain.bucket.SysBucket;
import cn.lary.oss.standard.domain.bucket.SysBucketStat;
import com.aliyun.oss.model.BucketStat;
import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/19
 */

public class BucketStatConvert implements Converter<BucketStat, SysBucketStat> {
    @Override
    public SysBucketStat convert(BucketStat source) {
        SysBucketStat res = new SysBucketStat();
        res.setStorageSize(source.getStorageSize());
        res.setObjectCount(source.getObjectCount());
        res.setMultipartUploadCount(source.getMultipartUploadCount());
        res.setLastModifiedTime(source.getLastModifiedTime());
        return res;
    }
}
