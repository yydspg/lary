package cn.lary.module.user.kit;

import cn.lary.kit.StringKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.user.dto.TokenPair;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenPairKit {

    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;


}
