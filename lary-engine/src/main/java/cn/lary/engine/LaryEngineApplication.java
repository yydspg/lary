package cn.lary.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {
})
public class LaryEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaryEngineApplication.class, args);
    }

}
