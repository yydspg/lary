package cn.lary.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude = {
        cn.lary.module.cache.config.RedissonConfig.class,
        cn.lary.module.common.config.MybatisPlusConfig.class
},
        scanBasePackages ={
        "cn.lary.common",
        "cn.lary.module",
        "cn.lary.module"
})
public class LaryEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaryEngineApplication.class, args);
    }

}
