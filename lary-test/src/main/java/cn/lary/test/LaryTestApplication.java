package cn.lary.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Map;
@Slf4j
@SpringBootApplication(scanBasePackages = {"cn.lary.test","cn.lary.core.lock","cn.lary.core.ratelimiter"})
public class LaryTestApplication {

    public static ConfigurableApplicationContext applicationContext;
    public static void main(String[] args) throws Exception{
        SpringApplication app=new SpringApplication(LaryTestApplication.class);
        applicationContext=app.run(args);
        Environment env = applicationContext.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "External: \thttp://{}:{}\n\t"+
                        "Doc: \thttp://{}:{}/doc.html\n"+
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
}
