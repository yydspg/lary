package cn.lary.oss.channel.aliyun.service;

import cn.lary.oss.channel.aliyun.convertor.args.ArgsToCreateBucketRequestConvert;
import cn.lary.oss.channel.aliyun.convertor.domain.BucketConvert;
import cn.lary.oss.channel.aliyun.convertor.domain.BucketStatConvert;
import cn.lary.oss.channel.aliyun.define.pool.AliyunClientPool;
import cn.lary.oss.channel.aliyun.define.service.AliyunBaseService;
import cn.lary.oss.standard.args.bucket.CreateBucketArgs;
import cn.lary.oss.standard.args.bucket.DelBucketArgs;
import cn.lary.oss.standard.domain.bucket.SysBucket;
import cn.lary.oss.standard.domain.bucket.SysBucketStat;
import cn.lary.oss.standard.service.BucketService;
import cn.lary.oss.util.ConvertKit;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author paul 2024/4/19
 */
@Service
public class AliyunBucketService extends AliyunBaseService implements BucketService {
    public AliyunBucketService(AliyunClientPool objectPool) {
        super(objectPool);
    }
    @Override
    public boolean exist(String bucketName) {
        OSS cli = super.getCli();
        try {
            return cli.doesBucketExist(bucketName);
        }finally {
            super.close(cli);
        }
    }

    @Override
    public List<SysBucket> list() {
        OSS cli = super.getCli();
        try {
            return ConvertKit.convert(cli.listBuckets(),new BucketConvert());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public SysBucket create(String bucketName) {
        OSS cli = super.getCli();
        try {
            return ConvertKit.convert(cli.createBucket(bucketName),new BucketConvert());
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
        }finally {
            super.close(cli);
        }
    }

    @Override
    public void del(String bucketName) {
        OSS cli = super.getCli();
        try {
            cli.deleteBucket(bucketName);
        }finally {
            super.close(cli);
        }
    }
    public String getLocation(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketLocation(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setCapacity(SetBucketStorageCapacityRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketStorageCapacity(request);
        } finally {
            super.close(cli);
        }
    }

    @Override
    public void del(DelBucketArgs bucketArgs) {
        OSS cli = super.getCli();
        try {
            cli.deleteBucket(bucketArgs.getBucketName());
        }finally {
            super.close(cli);
        }
    }

    @Override
    public SysBucketStat state(String bucketName) {
        OSS cli = super.getCli();
        try {
            BucketStat bucketStat = cli.getBucketStat(bucketName);
            return ConvertKit.convert(bucketStat,new BucketStatConvert());
        } finally {
            super.close(cli);
        }
    }
    public UserQos getCapacity(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketStorageCapacity(request);
        } finally {
            super.close(cli);
        }
    }
    public VoidResult putBucketAccessMonitor(String bucketName, String status) {
        OSS cli = getCli();
        try {
            return cli.putBucketAccessMonitor(bucketName, status);
        } finally {
            super.close(cli);
        }
    }

    public AccessMonitor getBucketAccessMonitor(String bucketName) {
        OSS cli = getCli();
        try {
            return cli.getBucketAccessMonitor(bucketName);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketAcl(SetBucketAclRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketAcl(request);
        } finally {
            super.close(cli);
        }
    }

    public AccessControlList getBucketAcl(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketAcl(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketCallbackPolicy(SetBucketCallbackPolicyRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketCallbackPolicy(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketCallbackPolicyResult getBucketCallbackPolicy(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketCallbackPolicy(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketCallbackPolicy(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketCallbackPolicy(request);
        } finally {
            super.close(cli);
        }
    }

    public AddBucketCnameResult addBucketCname(AddBucketCnameRequest request) {
        OSS cli = getCli();
        try {
            return cli.addBucketCname(request);
        } finally {
            super.close(cli);
        }
    }

    public List<CnameConfiguration> getBucketCname(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketCname(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketCname(DeleteBucketCnameRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketCname(request);
        } finally {
            super.close(cli);
        }
    }

    public CreateBucketCnameTokenResult createBucketCnameToken(CreateBucketCnameTokenRequest request) {
        OSS cli = getCli();
        try {
            return cli.createBucketCnameToken(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketCnameTokenResult getBucketCnameToken(GetBucketCnameTokenRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketCnameToken(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketInfo getBucketInfo(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketInfo(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketStat getBucketStat(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketStat(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketCORS(SetBucketCORSRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketCORS(request);
        } finally {
            super.close(cli);
        }
    }

    public CORSConfiguration getBucketCORS(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketCORS(request);
        } finally {
            super.close(cli);
        }
    }

    public List<SetBucketCORSRequest.CORSRule> getBucketCORSRules(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketCORSRules(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketCORSRules(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketCORSRules(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketEncryption(SetBucketEncryptionRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketEncryption(request);
        } finally {
            super.close(cli);
        }
    }

    public ServerSideEncryptionConfiguration getBucketEncryption(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketEncryption(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketEncryption(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketEncryption(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult putBucketImage(PutBucketImageRequest request) {
        OSS cli = getCli();
        try {
            return cli.putBucketImage(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketImageResult getBucketImage(String bucketName, GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketImage(bucketName, request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketImage(String bucketName, GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketImage(bucketName, request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketInventoryConfiguration(SetBucketInventoryConfigurationRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketInventoryConfiguration(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketInventoryConfiguration(request);
        } finally {
            super.close(cli);
        }
    }

    public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest request) {
        OSS cli = getCli();
        try {
            return cli.listBucketInventoryConfigurations(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketInventoryConfiguration(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketLifecycle(SetBucketLifecycleRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketLifecycle(request);
        } finally {
            super.close(cli);
        }
    }

    public List<LifecycleRule> getBucketLifecycle(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketLifecycle(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketLifecycle(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketLifecycle(request);

        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketLogging(SetBucketLoggingRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketLogging(request);

        } finally {
            super.close(cli);
        }
    }

    public BucketLoggingResult getBucketLogging(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketLogging(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketLogging(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketLogging(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketPolicy(SetBucketPolicyRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketPolicy(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketPolicyResult getBucketPolicy(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketPolicy(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketPolicy(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketPolicy(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketProcess(SetBucketProcessRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketProcess(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketProcess getBucketProcess(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketProcess(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketQosInfo(SetBucketQosInfoRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketQosInfo(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketQosInfo getBucketQosInfo(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketQosInfo(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketQosInfo(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketQosInfo(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketReferer(SetBucketRefererRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketReferer(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketReferer getBucketReferer(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketReferer(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult addBucketReplication(AddBucketReplicationRequest request) {
        OSS cli = getCli();
        try {
            return cli.addBucketReplication(request);
        } finally {
            super.close(cli);
        }
    }

    public List<ReplicationRule> getBucketReplication(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketReplication(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketReplication(DeleteBucketReplicationRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketReplication(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketReplicationProgress getBucketReplicationProgress(GetBucketReplicationProgressRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketReplicationProgress(request);
        } finally {
            super.close(cli);
        }
    }

    public List<String> getBucketReplicationLocation(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketReplicationLocation(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketRequestPayment(SetBucketRequestPaymentRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketRequestPayment(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketRequestPaymentResult getBucketRequestPayment(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketRequestPayment(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketResourceGroup(SetBucketResourceGroupRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketResourceGroup(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketResourceGroupResult getBucketResourceGroup(String bucketName) {
        OSS cli = getCli();
        try {
            return cli.getBucketResourceGroup(bucketName);
        } finally {
            super.close(cli);
        }
    }

    public BucketMetadata getBucketMetadata(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketMetadata(request);
        } finally {
            super.close(cli);
        }
    }


    public VoidResult setBucketTagging(SetBucketTaggingRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketTagging(request);
        } finally {
            super.close(cli);
        }
    }

    public TagSet getBucketTagging(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketTagging(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketTagging(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketTagging(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketTransferAcceleration(String bucketName, boolean enable) {
        OSS cli = getCli();
        try {
            return cli.setBucketTransferAcceleration(bucketName, enable);
        } finally {
            super.close(cli);
        }
    }

    public TransferAcceleration getBucketTransferAcceleration(String bucketName) {
        OSS cli = getCli();
        try {
            return cli.getBucketTransferAcceleration(bucketName);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketTransferAcceleration(String bucketName) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketTransferAcceleration(bucketName);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketVersioning(SetBucketVersioningRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketVersioning(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketVersioningConfiguration getBucketVersioning(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketVersioning(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult createBucketVpcip(CreateBucketVpcipRequest request) {
        OSS cli = getCli();
        try {
            return cli.createBucketVpcip(request);
        } finally {
            super.close(cli);
        }
    }

    public List<VpcPolicy> getBucketVpcip(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketVpcip(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketVpcip(DeleteBucketVpcipRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketVpcip(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult setBucketWebsite(SetBucketWebsiteRequest request) {
        OSS cli = getCli();
        try {
            return cli.setBucketWebsite(request);
        } finally {
            super.close(cli);
        }
    }

    public BucketWebsiteResult getBucketWebsite(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketWebsite(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult deleteBucketWebsite(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.deleteBucketWebsite(request);
        } finally {
            super.close(cli);
        }
    }

    public InitiateBucketWormResult initiateBucketWorm(InitiateBucketWormRequest request) {
        OSS cli = getCli();
        try {
            return cli.initiateBucketWorm(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult abortBucketWorm(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.abortBucketWorm(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult completeBucketWorm(CompleteBucketWormRequest request) {
        OSS cli = getCli();
        try {
            return cli.completeBucketWorm(request);
        } finally {
            super.close(cli);
        }
    }

    public VoidResult extendBucketWorm(ExtendBucketWormRequest request) {
        OSS cli = getCli();
        try {
            return cli.extendBucketWorm(request);
        } finally {
            super.close(cli);
        }
    }

    public GetBucketWormResult getBucketWorm(GenericRequest request) {
        OSS cli = getCli();
        try {
            return cli.getBucketWorm(request);
        } finally {
            super.close(cli);
        }
    }
}
