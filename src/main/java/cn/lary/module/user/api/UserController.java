package cn.lary.module.user.api;

import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.*;
import cn.lary.module.user.core.UserBizExecute;
import cn.lary.module.user.dto.LoginDTO;
import cn.lary.module.user.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserBizExecute userBizExecute;

    @PostMapping("/register")
    public SingleResponse<Void> register(@Validated @RequestBody RegisterDTO req) {
        ResPair<Void> res = userBizExecute.register(req);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
    @PostMapping("/login")
    public SingleResponse<Void> login(@Validated @RequestBody LoginDTO req) {
        ResPair<String> res = userBizExecute.login(req);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
}
