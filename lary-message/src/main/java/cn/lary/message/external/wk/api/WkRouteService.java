package cn.lary.message.external.wk.api;

import cn.lary.message.external.wk.vo.route.RouteVO;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.GET;

@RetrofitClient(baseUrl = "${wk.base.url}")
public interface WkRouteService {
    @GET("route")
    Response<RouteVO> getRoute();
}
