package cn.lary.message.external.wk.api;

import cn.lary.message.external.wk.dto.user.DeviceQuitDTO;
import cn.lary.message.external.wk.dto.user.UpdateTokenDTO;
import cn.lary.message.external.wk.vo.user.OnlineStatus;
import cn.lary.message.external.wk.vo.user.UpdateTokenVO;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

@RetrofitClient(baseUrl = "${wk.base.url}/user")
public interface WKUserService {
    @POST("token")
    Response<UpdateTokenVO> updateToken(@Body UpdateTokenDTO updateTokenDTO);
    @POST("device_quit")
    Response<Void> deviceQuit(@Body DeviceQuitDTO deviceQuitDTO);
    @POST("systemuids_add")
    Response<Void> addSystemUIDS(@Body List<String> uids);
    @POST("systemuids_remove")
    Response<Void> removeSystemUIDS(@Body List<String> uids);
    @POST("onlinestatus")
    Response<List<OnlineStatus>> onlineStatus(@Body List<String> uids);
}
