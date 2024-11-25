package cn.lary.common.interceptor.token.jwt;

import cn.lary.common.context.Profile;
import cn.lary.common.context.RequestContext;
import cn.lary.common.interceptor.token.TokenOperation;
import cn.lary.common.kit.ResponseKit;
import cn.lary.common.kit.StringKit;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JWTTokenOperation implements TokenOperation {


    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("token");
    }

    @Override
    public Profile process(String token, HttpServletResponse response) {
        if(StringKit.isEmpty(token)){
            ResponseKit.responseFail(response,"no token");
            return null;
        }
        Profile profile = JWTKit.verify(token);
        if(profile == null){
            ResponseKit.responseFail(response,"token invalid");
            return null;
        }
        return profile;
    }

    @Override
    public void store(Profile profile) {
        RequestContext.setCurrent(profile);
    }

    @Override
    public void remove() {
        RequestContext.removeCurrent();
    }
}
