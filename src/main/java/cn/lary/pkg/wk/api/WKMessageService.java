package cn.lary.pkg.wk.api;

import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.dto.message.SyncReq;
import cn.lary.pkg.wk.dto.message.SyncAckReq;
import cn.lary.pkg.wk.vo.message.Message;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

@RetrofitClient(baseUrl = "${wk.base.url}/message")
public interface WKMessageService {
    @POST("send")
    Response<Void> send(@Body MessageSendDTO message);
    @POST("sync")
    Response<List<Message>> sync(@Body SyncReq syncReq);
    @POST("syncack")
    Response<Void> syncAck(@Body SyncAckReq syncAckReq);
}
