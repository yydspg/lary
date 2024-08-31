package cn.lary.test;

import cn.lary.pkg.wk.api.ChannelService;
import cn.lary.pkg.wk.api.TestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Resource
    private  ChannelService channelService;
    @Resource
    private TestService testService;
    @GetMapping("/test")
    public void test() {

        System.out.println(testService.test().body());
    }
}
