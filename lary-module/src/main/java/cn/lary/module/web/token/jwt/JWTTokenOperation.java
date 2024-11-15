package cn.lary.module.web.token.jwt;

import cn.lary.common.context.Profile;
import cn.lary.module.web.token.Operation;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTTokenOperation implements Operation {

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("srsToken");
    }

    @Override
    public Profile process(String token, HttpServletResponse response) {
        Claims claims = JWTKit.getClaimsByToken(token);

        return null;
    }

    @Override
    public void store(Profile profile) {

    }

    @Override
    public void remove() {

    }
}
