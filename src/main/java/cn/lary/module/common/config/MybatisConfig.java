package cn.lary.module.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = {"cn.lary.module.gift.mapper","cn.lary.module.user.mapper",
        "cn.lary.module.group.mapper","cn.lary.module.stream.mapper",
        "cn.lary.module.app.mapper"})
public class MybatisConfig {
}
