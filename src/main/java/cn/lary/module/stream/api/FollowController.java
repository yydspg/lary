package cn.lary.module.stream.api;

import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.stream.core.FollowBizExecute;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowPageQueryDTO;
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
    public SingleResponse<Void> apply(@Valid @RequestBody FollowDTO dto) {
        ResponsePair<Void> res = followBizExecute.follow( dto);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    @GetMapping("/unfollow")
    public SingleResponse<Void> unfollow(@RequestParam @NotNull int toUid) {
        ResponsePair<Void> res = followBizExecute.unfollow( toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    @PostMapping("/follows")
    public PageResponse<Follow> page(@RequestBody @Valid FollowPageQueryDTO dto) {
        ResponsePair<List<Follow>> res = followBizExecute.follows(dto);
        if (res.isFail()) {
            return ResponseKit.pageFail(res.getMsg());
        }
        return ResponseKit.pageOk(res.getData(),dto.getPageIndex(), dto.getPageSize());
    }
    @GetMapping("/block")
    public SingleResponse<Void> block(@RequestParam @NotNull int toUid) {
        ResponsePair<Void> res = followBizExecute.block( toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/unblock")
    public SingleResponse<Void> unblock(@RequestParam @NotNull int toUid) {
        ResponsePair<Void> res = followBizExecute.unblock( toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
}

