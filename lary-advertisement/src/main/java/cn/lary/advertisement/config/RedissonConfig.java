package cn.lary.advertisement.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.Kryo5Codec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisson(){
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://192.168.1.104:6379")
                .addNodeAddress("redis://192.168.1.104:6380")
                .addNodeAddress("redis://192.168.1.104:6381");
        return  Redisson.create(config);
    }
}
