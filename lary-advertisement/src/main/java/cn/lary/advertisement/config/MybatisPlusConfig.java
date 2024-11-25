package cn.lary.advertisement.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
    "cn.lary.advertisement.mapper"
})
public class MybatisPlusConfig {


}
