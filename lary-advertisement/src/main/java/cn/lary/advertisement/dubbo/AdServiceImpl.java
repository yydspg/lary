package cn.lary.advertisement.dubbo;

import cn.lary.api.advertisement.AdService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class AdServiceImpl implements AdService {

    @Override
    public String hello() {
        return "hello world";
    }
}
