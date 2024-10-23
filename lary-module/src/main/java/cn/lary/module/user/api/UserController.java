package cn.lary.module.user.api;

import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.external.wk.vo.route.RouteVO;
import cn.lary.module.user.component.UserExecute;
import cn.lary.module.user.dto.LoginDTO;
import cn.lary.module.user.dto.RegisterDTO;
import cn.lary.module.user.dto.UserAvatarUploadDTO;
import cn.lary.module.user.dto.UserDestroyDTO;
import cn.lary.module.user.file.UserAvatarUploadProcessor;
import cn.lary.module.user.vo.UserRedDotVO;
import cn.lary.module.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserExecute userExecute;
    private final UserAvatarUploadProcessor userAvatarUploadProcessor;

    /**
     * 注册
     * @param dto {@link RegisterDTO}
     * @return token
     */
    @PostMapping("/register")
    public SingleResponse<String> register(@Valid @RequestBody RegisterDTO dto) {
        ResponsePair<String> response = userExecute.register(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     *登陆
     * @param dto {@link LoginDTO}
     * @return token
     */
    @PostMapping("/login")
    public SingleResponse<String> login(@Validated @RequestBody LoginDTO dto) {
        ResponsePair<String> response = userExecute.login(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    /**
     *注销
     * @param dto {@link UserDestroyDTO}
     * @return token
     */
    @PostMapping("/destroy")
    public SingleResponse<Void> login(@Validated @RequestBody UserDestroyDTO dto) {
        ResponsePair<Void> response = userExecute.destroy(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    @GetMapping("/register/code")
    public SingleResponse<Void> registerCode(@RequestParam(value = "phone") String phone) {
        ResponsePair<Void> response = userExecute.registerCode(phone);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/destroy/code")
    public SingleResponse<Void> destroyCode(@RequestParam(value = "phone") String phone) {
        ResponsePair<Void> response = userExecute.destroyCode(phone);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 退出登陆
     * @param request {@link HttpServletRequest}
     * @return ok
     */
    @GetMapping("/logout")
    public SingleResponse<Void> logout(HttpServletRequest request) {
        ResponsePair<Void> response = userExecute.logout( request);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 刷新token
     * @param request {@link HttpServletRequest}
     * @return ok
     */
    public SingleResponse<Void> refresh(HttpServletRequest request) {
        ResponsePair<Void> response = userExecute.refreshToken( request);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/my")
    public SingleResponse<UserVO> my() {
        ResponsePair<UserVO> response = userExecute.my();
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    @GetMapping("/redDots")
    public MultiResponse<UserRedDotVO> redDots() {
        ResponsePair<List<UserRedDotVO>> response = userExecute.getRedDot();
        if (response.isFail()) {
            return ResponseKit.multiFail(response.getMsg());
        }
        return ResponseKit.multiOk(response.getData());
    }
    @GetMapping("/route")
    public SingleResponse<RouteVO> route() {
        ResponsePair<RouteVO> response = userExecute.getRoute();
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    @PostMapping("/avatar")
    public SingleResponse<String> uploadAvatar(@RequestBody @Valid UserAvatarUploadDTO dto) {
        ResponsePair<String> response = userAvatarUploadProcessor.execute(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
}
