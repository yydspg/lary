package cn.lary.advertisement.dubbo;

import cn.lary.api.advertisement.YutakAdService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class YutakAdServiceImpl implements YutakAdService {

    @Override
    public String hello() {
        return "hello world";
    }
}
