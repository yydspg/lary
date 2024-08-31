package cn.lary.module.danmaku.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.module.danmaku.service.DanmakuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/danmaku")
@RequiredArgsConstructor
public class DanmakuController {

    private final DanmakuService danmakuService;
    @GetMapping("/send")
    public SingleResponse send() {

        return null;
    }
}
