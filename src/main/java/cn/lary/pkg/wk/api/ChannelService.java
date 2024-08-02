package cn.lary.pkg.wk.api;

import cn.lary.pkg.wk.entity.Request.channel.*;
import cn.lary.pkg.wk.entity.Response.channel.ChannelMaxMessageSeq;
import cn.lary.pkg.wk.entity.Response.channel.SyncMessageRes;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//当请求发生异常或者收到无效响应结果的时候，将HTTP相关信息解码到异常中，无效响应由业务自己判断
// 接口不可以直接返回 Response<Void> 类型，这个是 retrofit 框架的要求
@RetrofitClient(baseUrl = "${wk.base.url}/channel")
public interface ChannelService {
    @POST
    Response<Void> clearOrUpdate(@Body ChannelCreateReq channelCreateReq);
    @POST("info")
    Response<Void> updateOrAddInfo(@Body ChannelInfoReq channelInfoReq);
    @POST("delete")
    Response<Void> delete(@Body ChannelDeleteReq channelDeleteReq);
    @POST("subscribers/add")
    Response<Void> addSubscribers(@Body SubscribersAddReq subscribersAddReq);
    @POST("subscribers/del")
    Response<Void> removeSubscribers(@Body SubscribersRemoveReq subscribersDel);
    @POST("blacklist_add")
    Response<Void> addBlacklist(@Body BlacklistAddReq blacklistAddReq);
    @POST("blacklist_set")
    Response<Void> setBlacklist(@Body BlacklistAddReq blacklistAddReq);
    @POST("whitelist_remove")
    Response<Void> removeBlacklist(@Body WhitelistAddReq whitelistAddReq);
    @POST("whitelist_add")
    Response<Void> addWhitelist(@Body WhitelistAddReq whitelistAddReq);
    @POST("whitelist_set")
    Response<Void> setWhitelist(@Body WhitelistAddReq whitelistAddReq);
    @POST("whitelist_remove")
    Response<Void> removeWhitelist(@Body WhitelistAddReq whitelistAddReq);
    @POST("messagesync")
    Response<SyncMessageRes> syncMessage(@Body SyncMessageReq syncMessageReq);
    @GET("max_message_seq")
    Response<ChannelMaxMessageSeq> getMaxMessageSeq(@Query("channel_id") String channelId, @Query("channel_type") byte channelType);
}
