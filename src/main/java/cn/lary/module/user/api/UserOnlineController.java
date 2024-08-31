package cn.lary.module.user.api;

import cn.lary.module.user.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/online")
@RequiredArgsConstructor
public class UserOnlineController {
    private final UserOnlineService userOnlineService;
}
