package cn.lary.pkg.wk.api;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.GET;

@RetrofitClient(baseUrl = "http://127.0.0.1:9009")
public interface TestService {
    @GET("test")
    Response<Void> test();
}
