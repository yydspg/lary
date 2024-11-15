package cn.lary.module.web.token;


import cn.lary.common.context.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Operation {


     String getTokenFromRequest(HttpServletRequest request);

     Profile process(String token, HttpServletResponse response);

     void store(Profile profile);

     void remove();
}
