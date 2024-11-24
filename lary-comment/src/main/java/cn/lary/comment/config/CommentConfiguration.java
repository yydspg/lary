package cn.lary.comment.config;

import cn.lary.common.id.IdGenerationOptions;
import cn.lary.common.id.LaryIDBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentConfiguration {

    @Bean
    public LaryIDBuilder build() {
        return new LaryIDBuilder(new IdGenerationOptions());
    }
}
