package cn.lary.oss.channel.aliyun.repo;

import cn.lary.core.constant.ApiCode;
import cn.lary.core.model.base.AbstractObjPool;
import cn.lary.oss.channel.aliyun.convertor.args.ArgsToCreateBucketRequestConvert;
import cn.lary.oss.channel.aliyun.convertor.domain.BucketConvert;
import cn.lary.oss.exception.OssException;
import cn.lary.oss.standard.args.bucket.CreateBucketArgs;
import cn.lary.oss.standard.args.bucket.DelBucketArgs;
import cn.lary.oss.standard.domain.bucket.SysBucket;
import cn.lary.oss.standard.reop.BucketRepo;
import cn.lary.oss.standard.service.OssService;
import cn.lary.oss.util.ConvertKit;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;


import java.util.List;

/**
 * @author paul 2024/4/17
 */
public class AliyunBucketRepo extends OssService<OSS> implements BucketRepo {

    public AliyunBucketRepo(AbstractObjPool<OSS> ossAbstractObjPool) {
        super(ossAbstractObjPool);
    }

    @Override
    public boolean exist(String bucketName) {
        OSS cli = super.getCli();
        try {
            return cli.doesBucketExist(bucketName);
        }catch(ClientException e) {
            throw new OssException(ApiCode.OSS_CLIENT_FAIl,e.getMessage());
        } catch (OSSException e) {
            throw new OssException(ApiCode.OSS_QUERY_FAIL,e.getMessage());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public List<SysBucket> list() {
        OSS cli = super.getCli();
        try {
            return ConvertKit.convert(cli.listBuckets(),new BucketConvert());
        }catch(ClientException e) {
            throw new OssException(ApiCode.OSS_CLIENT_FAIl,e.getMessage());
        } catch (OSSException e) {
            throw new OssException(ApiCode.OSS_QUERY_FAIL,e.getMessage());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public SysBucket create(String bucketName) {
        OSS cli = super.getCli();
        try {
            return ConvertKit.convert(cli.createBucket(bucketName),new BucketConvert());
        }catch(ClientException e) {
            throw new OssException(ApiCode.OSS_CLIENT_FAIl,e.getMessage());
        } catch (OSSException e) {
            throw new OssException(ApiCode.OSS_QUERY_FAIL,e.getMessage());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public SysBucket create(CreateBucketArgs bucketArgs) {
        OSS cli = super.getCli();
        try {
            ArgsToCreateBucketRequestConvert reqConvert = new ArgsToCreateBucketRequestConvert();
            return ConvertKit.convert(cli.createBucket(reqConvert.convert(bucketArgs)),new BucketConvert());
        }catch(ClientException e) {
            throw new OssException(ApiCode.OSS_CLIENT_FAIl,e.getMessage());
        } catch (OSSException e) {
            throw new OssException(ApiCode.OSS_QUERY_FAIL,e.getMessage());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public void del(String bucketName) {
        OSS cli = super.getCli();
        try {
            cli.deleteBucket(bucketName);
        }catch(ClientException e) {
            throw new OssException(ApiCode.OSS_CLIENT_FAIl,e.getMessage());
        } catch (OSSException e) {
            throw new OssException(ApiCode.OSS_QUERY_FAIL,e.getMessage());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public void del(DelBucketArgs bucketArgs) {
        OSS cli = super.getCli();
        try {
            cli.deleteBucket(bucketArgs.getBucketName());
        }catch(ClientException e) {
            throw new OssException(ApiCode.OSS_CLIENT_FAIl,e.getMessage());
        } catch (OSSException e) {
            throw new OssException(ApiCode.OSS_QUERY_FAIL,e.getMessage());
        }finally {
            super.close(cli);
        }
    }
}
