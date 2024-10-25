package cn.lary.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "cn.lary.module")
public class LaryStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaryStarterApplication.class, args);
    }

}
