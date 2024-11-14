package cn.lary.module.web.token.redis;

import cn.lary.common.context.RequestContext;
import cn.lary.common.kit.ResponseKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.web.token.Operation;
import cn.lary.common.context.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTokenOperation implements Operation {

    private final CacheComponent cacheComponent;

    private final KVBuilder kvBuilder;

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("token");
    }

    @Override
    public Profile process(String token, HttpServletResponse response) {
        if(StringKit.isEmpty(token)) {
            ResponseKit.responseFail(response, "login first");
            return null;
        }
        String value = getLoginTokenV(token);
        if(StringKit.isEmpty(value)) {
            ResponseKit.responseFail(response, "token empty");
            return null;
        }
        String[] args = StringKit.split(value, "@");
        if(args == null || args.length < 2) {
            ResponseKit.responseFail(response, "token error");
            return null;
        }
        Profile pair = new Profile();
        pair.setUid(Long.parseLong(args[0]));
        pair.setName(args[1]);
        if (args.length > 2) {
            pair.setRole( Integer.parseInt(args[2]));
        }
        return pair;
    }

    @Override
    public void store(Profile profile) {
        RequestContext.setCurrent(profile);
    }

    @Override
    public void remove() {
        RequestContext.removeCurrent();
    }

    private String getLoginTokenV(String token) {
        return cacheComponent.get(kvBuilder.userLoginTokenK(token));
    }
}