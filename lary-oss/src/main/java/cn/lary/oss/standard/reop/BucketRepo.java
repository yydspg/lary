package cn.lary.oss.standard.reop;

import cn.lary.oss.standard.args.bucket.CreateBucketArgs;
import cn.lary.oss.standard.args.bucket.DelBucketArgs;
import cn.lary.oss.standard.domain.bucket.SysBucket;

import java.util.List;

/**
 * @author paul 2024/4/17
 */

public interface BucketRepo {
    public boolean exist(String bucketName);
    public List<SysBucket> list();
    public SysBucket create(String bucketName);
    public SysBucket create(CreateBucketArgs bucketArgs);
    public void del(String bucketName);
    public void del(DelBucketArgs bucketArgs);
}
