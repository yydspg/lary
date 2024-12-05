package cn.lary.user.dubbo;

import cn.lary.api.user.YutakUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DubboService
public class YutakUserServiceImpl implements YutakUserService {

}
