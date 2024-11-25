package cn.lary.stream.api;

import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.PageResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.IPKit;
import cn.lary.common.kit.ResponseKit;
import cn.lary.stream.component.RoomBusinessExecute;
import cn.lary.stream.component.StreamBusinessExecute;
import cn.lary.stream.dto.GoLiveDTO;
import cn.lary.stream.vo.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  直播间
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */

@Slf4j
@RestController
@RequestMapping("/v1/room/")
@RequiredArgsConstructor
public class RoomController {

    private final RoomBusinessExecute roomBusinessExecute;
    private final StreamBusinessExecute streamBusinessExecute;

    /**
     * 直播记录查询
     * @param page p
     * @param limit s
     * @return {@link StreamRecordVO}
     */
    @GetMapping("/page")
    public PageResponse<StreamRecordVO> records(@RequestParam @NotNull Integer page, @RequestParam @NotNull Integer limit) {
        ResponsePair<List<StreamRecordVO>> response = streamBusinessExecute.page( page, limit);
        if (response.isFail()) {
            return ResponseKit.pageFail(response.getMsg());
        }
        return ResponseKit.pageOk(response.getData(),page,limit);
    }

    /**
     * 直播首页
     * @return {@link RoomVO}
     */
    @GetMapping
    public MultiResponse<RoomVO> rooms(){
        ArrayList<String> data = new ArrayList<>();
        return null;
    }
    /**
     * 开启直播
     * @param dto {@link GoLiveDTO} 直播请求参数
     * @return {@link GoLiveVO}
     */
    @PostMapping("/go")
    public SingleResponse<GoLiveVO> start(@RequestBody @Valid GoLiveDTO dto, HttpServletRequest request) {

        String ip = IPKit.getIp(request);
        ResponsePair<GoLiveVO> response = roomBusinessExecute.go(ip, dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 加入直播间
     * @param toUid 主播id
     * @return {@link JoinLiveVO}
     */
    @GetMapping("/join")
    public SingleResponse<JoinLiveVO> join(@RequestParam(value = "toUid") @NotNull Long toUid,
                                           @RequestParam(value = "sid") @NotNull Long sid,
                                           HttpServletRequest dto) {
        String ip = IPKit.getIp(dto);
        ResponsePair<JoinLiveVO> response = roomBusinessExecute.join( toUid,sid,ip);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 结束直播
     * @return OK
     */
    @GetMapping("/end")
    public SingleResponse<DownLiveVO> end() {
        ResponsePair<DownLiveVO> response = roomBusinessExecute.end();
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 离开直播间
     * @return OK
     */
    @GetMapping("/leave")
    public SingleResponse<Void> leave() {
        ResponsePair<Void> response = roomBusinessExecute.leave();
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

}
