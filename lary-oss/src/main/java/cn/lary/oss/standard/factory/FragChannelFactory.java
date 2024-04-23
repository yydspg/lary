package cn.lary.oss.standard.factory;

import cn.lary.core.util.SpringContextKit;
import cn.lary.oss.standard.service.BucketService;
import cn.lary.oss.standard.service.FragService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author paul 2024/4/23
 */
@Component
public class FragChannelFactory implements ApplicationRunner {
    private Map<String, FragService> fragServices ;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        fragServices = SpringContextKit.getBeansOfType(FragService.class);
    }
    public FragService get(String channelName) {return fragServices.get(channelName);}
}
