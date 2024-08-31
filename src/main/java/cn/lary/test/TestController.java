package cn.lary.test;

import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.user.api.UserController;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.TestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {
    @Resource
    private WKChannelService WKChannelService;
    @Resource
    private TestService testService;
    @Resource
    private RegisterConfig registerConfig;
    @Resource
    private UserController userController;
    @Resource
    private GroupService groupService;

    @Autowired
    private UserService userService;
    @GetMapping("/test")
    public void test() {
        User user = userService.queryByUID("12124");
        System.out.println(user);
    }
    @GetMapping("/channel")
    public void channelTest() {

    }
    @GetMapping("r")
    public void registerTest() {
        userController.register(null);
    }
}
