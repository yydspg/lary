package cn.lary.module.user.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.module.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public SingleResponse get(@RequestParam(value = "uid", required = true) String uid) {

        return null;
    }
}
