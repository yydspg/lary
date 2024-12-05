package cn.lary.raffle.api;

import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.raffle.component.RaffleExecute;
import cn.lary.api.raffle.dto.RaffleEventDTO;
import cn.lary.api.raffle.dto.RaffleRecordPageQueryDTO;
import cn.lary.api.raffle.vo.RaffleEventVO;
import cn.lary.api.raffle.vo.RaffleRecordVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/raffle")
@RequiredArgsConstructor
public class RaffleController {

    private final RaffleExecute raffleExecute;

    @GetMapping("/join")
    public SingleResponse<Void> join(@RequestParam @NotNull Long toUid) {
        ResponsePair<Void> response = raffleExecute.join(toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    @GetMapping("/info")
    public SingleResponse<RaffleEventVO> info(@RequestParam @NotNull Long toUid) {
        ResponsePair<RaffleEventVO> response = raffleExecute.info(toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    @PostMapping("/my")
    public MultiResponse<RaffleRecordVO> my(@RequestBody @Valid RaffleRecordPageQueryDTO dto){
        ResponsePair<List<RaffleRecordVO>> response = raffleExecute.my(dto);
        if (response.isFail()) {
            return ResponseKit.multiFail(response.getMsg());
        }
        return ResponseKit.multiOk(response.getData());
    }

    @PostMapping("/build")
    public SingleResponse<Void> build(@RequestBody @Valid RaffleEventDTO dto){
        ResponsePair<Void> response = raffleExecute.raffle(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
}
