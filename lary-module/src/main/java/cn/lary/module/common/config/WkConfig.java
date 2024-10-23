package cn.lary.module.common.config;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RetrofitScan(value = "cn.lary.external.wk.api")
public class WkConfig {

}
