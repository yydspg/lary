package cn.lary.module.user.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.kit.StringKit;
import cn.lary.module.app.entity.AppConfigRes;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.server.LaryServer;
import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.common.service.SmsService;
import cn.lary.module.user.dto.req.RegisterReq;
import cn.lary.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final RegisterConfig registerConfig;
    private final AppConfigService appConfigService;
    private final UserService userService;
    private final SmsService smsService;
    @GetMapping
    public SingleResponse get(@RequestParam(value = "uid", required = true) String uid) {

        return null;
    }
    @PostMapping("/register")
    public SingleResponse register(@RequestBody RegisterReq req) {
        if(registerConfig.isOffe()) {
            return ResKit.fail("注册通道暂不开放");
        }
        // query app config
        // TODO  :  按理来说这里是缓存在内存或者写死，但是这里直接数据库查询，很不错
        AppConfigRes appConfig = appConfigService.getAppConfig();
        if (appConfig == null) {
            return ResKit.fail("查询应用设置错误");
        }
        int registerInviteOn =  appConfig.getRegisterInviteOn();
        // 是否开启业务入口模式
        if (registerInviteOn == Lary.RegisterMode.ON) {
            if(StringKit.isEmpty(req.getInviteCode())) {
                return ResKit.fail("邀请码不能为空");
            }
            // 这里做个hashMap就行了
            if (!LaryServer.checkBusinessCode(req.getCode())) {
                return ResKit.fail("邀请码不存在");
            }
        }
        // 这里存在链路追踪
        // TODO  :  链路追踪不太清楚
        if (userService.queryByUsername(req.getName())) {
            return ResKit.fail("用户已存在");
        }
        // 短信验证
        if (!smsService.verify(req.getCode(),req.getZone(), req.getPhone(), Lary.CodeType.Register)) {
            return ResKit.fail("验证码错误");
        }

        return null;
    }
}
