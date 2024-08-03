package cn.lary.test;

import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.user.api.UserController;
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
    @Resource
    private RegisterConfig registerConfig;
    @Resource
    private UserController userController;
    @GetMapping("/test")
    public void test() {
        System.out.println("off"+registerConfig.offe);
        System.out.println("username"+registerConfig.usernameOn);
        System.out.println(registerConfig.onlyChina);
        System.out.println(registerConfig.stickerAddOff);
    }
    @GetMapping("/channel")
    public void channelTest() {

    }
    @GetMapping("r")
    public void registerTest() {
        userController.register(null);
    }
}
