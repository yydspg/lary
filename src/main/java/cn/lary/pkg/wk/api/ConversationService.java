package cn.lary.pkg.wk.api;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

@RetrofitClient(baseUrl = "${wk.base.url}")
public interface ConversationService {

}
