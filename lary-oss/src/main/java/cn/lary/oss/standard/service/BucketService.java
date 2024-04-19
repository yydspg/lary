package cn.lary.oss.standard.service;

import cn.lary.oss.standard.args.bucket.CreateBucketArgs;
import cn.lary.oss.standard.args.bucket.DelBucketArgs;
import cn.lary.oss.standard.domain.bucket.SysBucket;
import cn.lary.oss.standard.domain.bucket.SysBucketStat;

import java.util.List;

/**
 * @author paul 2024/4/19
 */

public interface BucketService {
    public boolean exist(String bucketName);
    public List<SysBucket> list();
    public SysBucket create(String bucketName);
    public SysBucket create(CreateBucketArgs args);
    public void del(String buckName);
    public void del(DelBucketArgs args);
    SysBucketStat state(String bucketName);
}
