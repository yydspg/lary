package cn.lary.module.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = {
        "cn.lary.module.user.mapper",
        "cn.lary.module.group.mapper",
        "cn.lary.module.stream.mapper",
        "cn.lary.module.pay.mapper",
        "cn.lary.module.wallet.mapper",
        "cn.lary.module.gift.mapper",
        "cn.lary.module.report.mapper",
        "cn.lary.module.common.mapper",
        "cn.lary.module.goods.mapper",
        "cn.lary.module.store.mapper",
        "cn.lary.module.order.mapper",
        "cn.lary.module.raffle.mapper",
        "cn.lary.module.redpacket.mapper",
        "cn.lary.module.comment.mapper",
})
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
