package cn.lary.stream.api;

import cn.lary.common.dto.PageResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.stream.component.FollowBusinessExecute;
import cn.lary.stream.dto.FollowDTO;
import cn.lary.stream.dto.FollowPageQueryDTO;
import cn.lary.stream.vo.FollowVO;
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
    public SingleResponse<Void> unfollow(@RequestParam @NotNull long toUid) {
        ResponsePair<Void> response = followBusinessExecute.unfollow( toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    @PostMapping("/follows")
    public PageResponse<FollowVO> page(@RequestBody @Valid FollowPageQueryDTO dto) {
        ResponsePair<List<FollowVO>> response = followBusinessExecute.follows(dto);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),dto.getPageIndex(), dto.getPageSize());
    }
    @GetMapping("/block")
    public SingleResponse<Void> block(@RequestParam @NotNull long toUid) {
        ResponsePair<Void> response = followBusinessExecute.block( toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/unblock")
    public SingleResponse<Void> unblock(@RequestParam @NotNull long toUid) {
        ResponsePair<Void> response = followBusinessExecute.unblock( toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
}

