package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.stream.core.FollowBizExecute;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.req.FollowListReq;
import cn.lary.module.stream.entity.Follow;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/stream/fan")
@RequiredArgsConstructor
public class FollowController {

    private final FollowBizExecute followBizExecute;

    @PostMapping("/foll")
    public SingleResponse<Void> apply(@Valid @RequestBody FollowDTO req) {
        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        ResPair<Void> res = followBizExecute.follow(uid, uidName, req);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

    @GetMapping("/unfollow{toUid}")
    public SingleResponse<Void> unfollow(@PathVariable @NotNull String toUid) {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = followBizExecute.unfollow(uid, toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

    @PostMapping("/follows")
    public PageResponse<Follow> page(@RequestBody @Valid FollowListReq req) {
        String uid = ReqContext.getLoginUID();
        ResPair<List<Follow>> res = followBizExecute.follows(uid, req);
        if (!res.isOk()) {
            return ResKit.pageFail(res.getMsg());
        }
        return ResKit.pageOk(res.getData(),req.getPageIndex(), req.getPageSize(), 0);
    }
    @GetMapping("/block{toUid}")
    public SingleResponse<Void> block(@PathVariable @NotNull String toUid) {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = followBizExecute.block(uid, toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
    @GetMapping("/unblock{toUid}")
    public SingleResponse<Void> unblock(@PathVariable @NotNull String toUid) {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = followBizExecute.unblock(uid, toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
}

