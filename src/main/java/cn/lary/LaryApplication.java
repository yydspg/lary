package cn.lary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class LaryApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LaryApplication.class, args);

    }

}
