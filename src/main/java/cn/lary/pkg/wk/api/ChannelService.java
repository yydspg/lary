package cn.lary.pkg.wk.api;

import cn.lary.pkg.wk.entity.Request.*;
import cn.lary.pkg.wk.entity.Response.Status;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

@RetrofitClient(baseUrl = "${wk.base.url}")
public interface ChannelService {
    @POST("channel")
    Response<Status> clearOrUpdate(@Body ChannelCreate channelCreate);
    @POST("channel/info")
    Response<Status> updateOrAddInfo(@Body ChannelInfo channelInfo);
    @POST("channel/delete")
    Response<Status> delete(@Body ChannelDelete channelDelete);
    @POST("channel/subscribers/add")
    Response<Status> addSubscribers(@Body SubscribersAdd subscribersAdd);
    @POST("channel/subscribers/del")
    Response<Status> removeSubscribers(@Body SubscribersRemove subscribersDel);
}
