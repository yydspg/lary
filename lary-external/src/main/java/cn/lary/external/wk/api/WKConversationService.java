package cn.lary.external.wk.api;

import cn.lary.external.wk.dto.conversation.ConversationDeleteDTO;
import cn.lary.external.wk.dto.conversation.ConversationUnreadClearDTO;
import cn.lary.external.wk.dto.conversation.SyncUserConversationDTO;
import cn.lary.external.wk.dto.message.SyncRecentMessagesReq;
import cn.lary.external.wk.vo.conversation.ConversationVO;
import cn.lary.external.wk.vo.conversation.SyncUserConversationVO;
import cn.lary.external.wk.vo.message.RecentMessage;
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
    Response<List<ConversationVO>> list(@Query(value = "uid") Long uid);
    @POST("clearUnread")
    Response<Void> clearUnread(@Body ConversationUnreadClearDTO conversationUnreadClearDTO);
    @POST("delete")
    Response<Void> delete(@Body ConversationDeleteDTO conversationDeleteDTO);
    @POST("sync")
    Response<SyncUserConversationVO> sync(@Body SyncUserConversationDTO syncUserConversationDTO);
    @POST("syncMessages")
    Response<List<RecentMessage>> syncRecentMessages(@Body SyncRecentMessagesReq syncRecentMessagesReq);

}
