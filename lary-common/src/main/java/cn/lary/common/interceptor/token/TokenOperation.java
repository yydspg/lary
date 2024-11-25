package cn.lary.common.interceptor.token;


import cn.lary.common.context.Profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface TokenOperation {


     String getTokenFromRequest(HttpServletRequest request);

     Profile process(String token, HttpServletResponse response);

     void store(Profile profile);

     void remove();
}
