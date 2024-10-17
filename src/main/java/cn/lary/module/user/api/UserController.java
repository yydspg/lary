package cn.lary.module.user.api;

import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.*;
import cn.lary.module.user.dto.RefreshTokenDTO;
import cn.lary.module.user.execute.UserBizExecute;
import cn.lary.module.user.dto.LoginDTO;
import cn.lary.module.user.dto.RegisterDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    /**
     * 注册
     * @param req {@link RegisterDTO}
     * @return token
     */
    @PostMapping("/register")
    public SingleResponse<String> register(@Valid @RequestBody RegisterDTO req) {
        ResponsePair<String> res = userBizExecute.register(req);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     *登陆
     * @param req {@link LoginDTO}
     * @return token
     */
    @PostMapping("/login")
    public SingleResponse<String> login(@Validated @RequestBody LoginDTO req) {
        ResponsePair<String> res = userBizExecute.loginByUid(req);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
    // TODO  : 排除这个接口的拦截
    // TODO  :  限流
    @GetMapping("/sms")
    public SingleResponse<Void> smsCode(@RequestParam(value = "phone") String phone) {
        ResponsePair<Void> res = userBizExecute.smsCode(phone);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 退出登陆
     * @param request {@link HttpServletRequest}
     * @param deviceId d
     * @param flag f
     * @return ok
     */
    @GetMapping("/logout")
    public SingleResponse<Void> logout(HttpServletRequest request,
                                       @RequestParam @NotBlank Integer deviceId,
                                       @RequestParam @NotBlank Integer flag) {
        ResponsePair<Void> res = userBizExecute.logout( flag, deviceId, request.getHeader("token"));
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 刷新token
     * @param dto {@link RefreshTokenDTO}
     * @param request {@link HttpServletRequest}
     * @return ok
     */
    public SingleResponse<Void> refresh(@RequestBody @Valid RefreshTokenDTO dto,HttpServletRequest request) {
        ResponsePair<Void> res = userBizExecute.refreshToken( request.getHeader("token"), dto);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

}
