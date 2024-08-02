package cn.lary.test;

import cn.lary.pkg.wk.api.ChannelService;
import cn.lary.pkg.wk.api.TestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {
    @Resource
    private  ChannelService channelService;
    @Resource
    private TestService testService;
    @GetMapping("/test")
    public void test() {
            testService.test();
    }
    @GetMapping("/channel")
    public void channelTest() {

    }
}
