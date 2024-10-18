package cn.lary.test;

import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.user.api.UserController;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
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
    private RegisterConfig registerConfig;
    @Resource
    private UserController userController;
    @Resource
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping("/channel")
    public void channelTest() {

    }
}
