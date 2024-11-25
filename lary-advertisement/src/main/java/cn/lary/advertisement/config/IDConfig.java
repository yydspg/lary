package cn.lary.advertisement.config;

import cn.lary.common.id.IdGenerationOptions;
import cn.lary.common.id.LaryIDBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IDConfig {

    @Bean
    public LaryIDBuilder builder(){
        return new LaryIDBuilder(new IdGenerationOptions());
    }
}
