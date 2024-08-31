package cn.lary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class LaryApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LaryApplication.class, args);

    }

}
