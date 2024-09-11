package cn.lary.pkg.wk.api;

import cn.lary.pkg.wk.entity.Request.message.MessageSendReq;
import cn.lary.pkg.wk.entity.Request.message.SyncReq;
import cn.lary.pkg.wk.entity.Request.message.SyncAckReq;
import cn.lary.pkg.wk.entity.Response.message.Message;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

@RetrofitClient(baseUrl = "${wk.base.url}/message")
public interface WKMessageService {
    @POST("send")
    Response<Void> send(@Body MessageSendReq message);
    @POST("sync")
    Response<List<Message>> sync(@Body SyncReq syncReq);
    @POST("syncack")
    Response<Void> syncAck(@Body SyncAckReq syncAckReq);
}