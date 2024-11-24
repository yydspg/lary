package cn.lary.message.external.wk.api;

import cn.lary.message.external.wk.dto.message.MessageSendDTO;
import cn.lary.message.external.wk.dto.message.SyncAckDTO;
import cn.lary.message.external.wk.dto.message.SyncDTO;
import cn.lary.message.external.wk.vo.message.Message;
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
    Response<List<Message>> sync(@Body SyncDTO syncDTO);
    @POST("syncack")
    Response<Void> syncAck(@Body SyncAckDTO syncAckDTO);
}
