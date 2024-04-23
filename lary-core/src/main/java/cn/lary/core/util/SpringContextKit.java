package cn.lary.core.util;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author paul 2024/4/23
 */
@Component
public class SpringContextKit implements ApplicationContextAware  {
    public static ApplicationContext app;
    public static Object getBean(String name){
        return app.getBean(name);
    }
    public static <T> T getBean(Class<T> clazz){
        return app.getBean(clazz);
    }
    public static <T> T getBean(String name,Class<T> clazz){
        return app.getBean(name, clazz);
    }
    public static <T> Map<String,T> getBeansOfType(Class<T> clazz) {return app.getBeansOfType(clazz);}

    @Override
    public void setApplicationContext(ApplicationContext app) throws BeansException {
        SpringContextKit.app = app;
    }
}
