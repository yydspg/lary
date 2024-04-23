package cn.lary.oss.standard.factory;

import cn.lary.core.util.SpringContextKit;
import cn.lary.oss.standard.service.ObjService;
import cn.lary.oss.standard.service.OssService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author paul 2024/4/23
 */
@Component
public class ObjChannelFactory implements ApplicationRunner {
    private Map<String, ObjService> objServices;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        objServices = SpringContextKit.getBeansOfType(ObjService.class);
    }
    public ObjService get(String channelName) {return objServices.get(channelName);}
}
