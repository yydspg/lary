package cn.lary.module;


import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMethodCache(basePackages = "cn.lary,module.cache")
@SpringBootApplication(scanBasePackages = "cn.lary.module")
public class LaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(LaryApplication.class, args);
    }
}
