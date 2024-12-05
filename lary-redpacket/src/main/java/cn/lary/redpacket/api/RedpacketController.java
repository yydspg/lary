package cn.lary.redpacket.api;

import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.redpacket.component.RedpacketExecute;
import cn.lary.api.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.api.redpacket.dto.RedpacketRecordPageQueryDTO;
import cn.lary.api.redpacket.vo.RedpacketEventVO;
import cn.lary.api.redpacket.vo.RedpacketRecordVO;
import cn.lary.api.redpacket.vo.RedpacketTokenVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/v1/redpacket")
//@RequiredArgsConstructor
public class RedpacketController {

//    private final RedpacketExecute redpacketExecute;
//
//    @GetMapping("/join")
//    public SingleResponse<RedpacketTokenVO> join(@RequestParam @NotNull Long toUid) {
//        ResponsePair<RedpacketTokenVO> response = redpacketExecute.join(toUid);
//        if (response.isFail()) {
//            return ResponseKit.fail(response.getMsg());
//        }
//        return ResponseKit.ok();
//    }
//    @GetMapping("/info")
//    public SingleResponse<RedpacketEventVO>  info(@RequestParam @NotNull Long toUid) {
//        ResponsePair<RedpacketEventVO> response =redpacketExecute.info(toUid);
//        if (response.isFail()) {
//            return ResponseKit.fail(response.getMsg());
//        }
//        return ResponseKit.ok(response.getData());
//    }
//
//    @PostMapping("/build")
//    public SingleResponse<Void> build(@RequestBody RedpacketEventBuildDTO dto) {
//        ResponsePair<Void> response = redpacketExecute.redpacket(dto);
//        if (response.isFail()) {
//            return ResponseKit.fail(response.getMsg());
//        }
//        return ResponseKit.ok();
//    }
//    @PostMapping("/my")
//    public MultiResponse<RedpacketRecordVO> my(@RequestBody @Valid RedpacketRecordPageQueryDTO dto) {
//        ResponsePair<List<RedpacketRecordVO>> response = redpacketExecute.my(dto);
//        if (response.isFail()) {
//            return ResponseKit.multiFail(response.getMsg());
//        }
//        return ResponseKit.multiOk(response.getData());
//    }
}
