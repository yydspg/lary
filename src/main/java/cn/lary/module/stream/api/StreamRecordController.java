package cn.lary.module.stream.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.module.stream.service.StreamRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@RestController
@RequestMapping("/v1/stream/record")
@RequiredArgsConstructor
public class StreamRecordController {
    private final StreamRecordService streamRecordService;

    public void endStream(String streamId,String giftBuyRecordId,String wkChannelId) {

    }
}
