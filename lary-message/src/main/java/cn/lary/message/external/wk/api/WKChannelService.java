package cn.lary.message.external.wk.api;

import cn.lary.message.external.wk.dto.channel.*;
import cn.lary.message.external.wk.vo.channel.ChannelMaxMessageVO;
import cn.lary.message.external.wk.vo.channel.SyncMessageVO;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//当请求发生异常或者收到无效响应结果的时候，将HTTP相关信息解码到异常中，无效响应由业务自己判断
// 接口不可以直接返回 Response<Void> 类型，这个是 retrofit 框架的要求
@RetrofitClient(baseUrl = "${wk.base.url}/channel")
public interface WKChannelService {
    @POST
    Response<Void> createOrUpdate(@Body WKChannelCreateDTO WKChannelCreateDTO);
    @POST("info")
    Response<Void> updateOrAddInfo(@Body WKChannelInfoDTO channelInfoReq);
    @POST("delete")
    Response<Void> delete(@Body WKChannelDeleteDTO channelDeleteReq);
    @POST("subscribers_add")
    Response<Void> addSubscribers(@Body SubscribersAddDTO subscribersAddDTO);
    @POST("subscribers_remove")
    Response<Void> removeSubscribers(@Body SubscribersRemoveDTO subscribersDel);
    @POST("blacklist_add")
    Response<Void> addBlacklist(@Body BlacklistAddDTO blacklistAddDTO);
    @POST("blacklist_set")
    Response<Void> setBlacklist(@Body BlacklistAddDTO blacklistAddDTO);
    @POST("whitelist_remove")
    Response<Void> removeBlacklist(@Body WhitelistAddDTO whitelistAddDTO);
    @POST("whitelist_add")
    Response<Void> addWhitelist(@Body WhitelistAddDTO whitelistAddDTO);
    @POST("whitelist_set")
    Response<Void> setWhitelist(@Body WhitelistAddDTO whitelistAddDTO);
    @POST("whitelist_remove")
    Response<Void> removeWhitelist(@Body WhitelistAddDTO whitelistAddDTO);
    @POST("messagesync")
    Response<SyncMessageVO> syncMessage(@Body SyncMessageDTO syncMessageDTO);
    @GET("max_message_seq")
    Response<ChannelMaxMessageVO> getMaxMessageSeq(@Query("channel_id") String channelId, @Query("channel_type") byte channelType);
}
