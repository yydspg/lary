package cn.lary.pkg.wk.api;

import cn.lary.pkg.wk.dto.conversation.ConversationDeleteReq;
import cn.lary.pkg.wk.dto.conversation.ConversationUnreadClearReq;
import cn.lary.pkg.wk.dto.conversation.SyncUserConversationReq;
import cn.lary.pkg.wk.dto.message.SyncRecentMessagesReq;
import cn.lary.pkg.wk.vo.conversation.ConversationRes;
import cn.lary.pkg.wk.vo.message.RecentMessage;
import cn.lary.pkg.wk.vo.conversation.SyncUserConversationRes;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

@RetrofitClient(baseUrl = "${wk.base.url}/conversations")
public interface WKConversationService {
    @GET("list")
    Response<List<ConversationRes>> list(@Query(value = "uid") String uid);
    @POST("clearUnread")
    Response<Void> clearUnread(@Body ConversationUnreadClearReq conversationUnreadClearReq);
    @POST("delete")
    Response<Void> delete(@Body ConversationDeleteReq conversationDeleteReq);
    @POST("sync")
    Response<SyncUserConversationRes> sync(@Body SyncUserConversationReq syncUserConversationReq);
    @POST("syncMessages")
    Response<List<RecentMessage>> syncRecentMessages(@Body SyncRecentMessagesReq syncRecentMessagesReq);

}
