package cn.lary.stream.api;

import cn.lary.external.srs.dto.OnPlayDTO;
import cn.lary.external.srs.dto.OnPublishDTO;
import cn.lary.external.srs.dto.OnStopDTO;
import cn.lary.external.srs.dto.OnUnpublishDTO;
import cn.lary.stream.callback.SRSCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 此模块目的是 srs 回调，尽量减少回调后的业务逻辑
 */
@Slf4j
@RestController("/v1/srs/callback")
@RequiredArgsConstructor
public class SrsCallbackController  {

    private final SRSCallback srsCallback;

    @PostMapping("/publish")
    public int onPublish(OnPublishDTO dto) {
        return  srsCallback.onPublish(dto);
    }

    @PostMapping("/unPublish")
    public int onUnPublish(OnUnpublishDTO dto) {
        return srsCallback.onUnPublish(dto);
    }

    @PostMapping("/onPlay")
    public int onPlay(OnPlayDTO dto) {
        return srsCallback.onPlay(dto);
    }

    @PostMapping("/onStop")
    public int onStop(OnStopDTO dto) {
        return srsCallback.onStop(dto);
    }
}
