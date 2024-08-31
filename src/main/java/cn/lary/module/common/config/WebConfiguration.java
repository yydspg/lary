package cn.lary.module.common.config;

import cn.lary.module.user.interceptor.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("add interceptor");
        registry.addInterceptor(userInterceptor)
//                .addPathPatterns("/**")
                .addPathPatterns("/v1/group/**","/v1/friend/**","/v1/friend/**")
                .excludePathPatterns("/v1/user/login","/v1/user/register");
    }
}
