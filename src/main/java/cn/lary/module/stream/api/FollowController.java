package cn.lary.module.stream.api;

import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.stream.component.FollowBusinessExecute;
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

    private final FollowBusinessExecute followBusinessExecute;

    @PostMapping("/foll")
    public SingleResponse<Void> apply(@Valid @RequestBody FollowDTO dto) {
        ResponsePair<Void> response = followBusinessExecute.follow( dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    @GetMapping("/unfollow")
    public SingleResponse<Void> unfollow(@RequestParam @NotNull int toUid) {
        ResponsePair<Void> response = followBusinessExecute.unfollow( toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    @PostMapping("/follows")
    public PageResponse<Follow> page(@RequestBody @Valid FollowPageQueryDTO dto) {
        ResponsePair<List<Follow>> response = followBusinessExecute.follows(dto);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),dto.getPageIndex(), dto.getPageSize());
    }
    @GetMapping("/block")
    public SingleResponse<Void> block(@RequestParam @NotNull int toUid) {
        ResponsePair<Void> response = followBusinessExecute.block( toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/unblock")
    public SingleResponse<Void> unblock(@RequestParam @NotNull int toUid) {
        ResponsePair<Void> response = followBusinessExecute.unblock( toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
}

