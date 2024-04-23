package cn.lary.oss.standard.factory;

import cn.lary.core.util.SpringContextKit;
import cn.lary.oss.standard.service.BucketService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author paul 2024/4/23
 */
@Component
public class BucketChannelFactory implements ApplicationRunner {
    private Map<String, BucketService> bucketServices ;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bucketServices = SpringContextKit.getBeansOfType(BucketService.class);
    }
    public BucketService get(String channelName){
        return bucketServices.get(channelName);
    }
}
