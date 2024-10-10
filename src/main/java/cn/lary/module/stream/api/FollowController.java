package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.stream.core.FollowBizExecute;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowListDTO;
import cn.lary.module.stream.entity.Follow;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fan")
@RequiredArgsConstructor
public class FollowController {

    private final FollowBizExecute followBizExecute;

    @PostMapping("/foll")
    public SingleResponse<Void> apply(@Valid @RequestBody FollowDTO req) {
        int uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        ResPair<Void> res = followBizExecute.follow(uid, uidName, req);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    @GetMapping("/unfollow{toUid}")
    public SingleResponse<Void> unfollow(@RequestParam @NotNull int toUid) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = followBizExecute.unfollow(uid, toUid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    @PostMapping("/follows")
    public PageResponse<Follow> page(@RequestBody @Valid FollowListDTO req) {
        int uid = ReqContext.getLoginUID();
        ResPair<List<Follow>> res = followBizExecute.follows(uid, req);
        if (!res.isOk()) {
            return ResponseKit.pageFail(res.getMsg());
        }
        return ResponseKit.pageOk(res.getData(),req.getPageIndex(), req.getPageSize());
    }
    @GetMapping("/block")
    public SingleResponse<Void> block(@RequestParam @NotNull int toUid) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = followBizExecute.block(uid, toUid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/unblock")
    public SingleResponse<Void> unblock(@RequestParam @NotNull int toUid) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = followBizExecute.unblock(uid, toUid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
}

