package cn.lary.pkg.wk.api;

import cn.lary.pkg.wk.entity.Request.user.DeviceQuitReq;
import cn.lary.pkg.wk.entity.Request.user.Uids;
import cn.lary.pkg.wk.entity.Request.user.UpdateTokenReq;
import cn.lary.pkg.wk.entity.Response.user.OnlineStatus;
import cn.lary.pkg.wk.entity.Response.user.UpdateTokenRes;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

@RetrofitClient(baseUrl = "${wk.base.url}/user")
public interface WKUserService {
    @POST("token")
    Response<UpdateTokenRes> updateToken(@Body UpdateTokenReq updateTokenReq);
    @POST("device_quit")
    Response<Void> deviceQuit(@Body DeviceQuitReq deviceQuitReq);
    @POST("systemuids_add")
    Response<Void> addSystemUIDS(@Body Uids uids);
    @POST("systemuids_remove")
    Response<Void> removeSystemUIDS(@Body Uids uids);
    @POST("onlinestatus")
    Response<List<OnlineStatus>> onlineStatus(@Body Uids uids);
}
