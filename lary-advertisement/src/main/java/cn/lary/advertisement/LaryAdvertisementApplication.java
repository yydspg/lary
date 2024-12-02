package cn.lary.advertisement;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableDubbo()
public class LaryAdvertisementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaryAdvertisementApplication.class, args);
    }

}
