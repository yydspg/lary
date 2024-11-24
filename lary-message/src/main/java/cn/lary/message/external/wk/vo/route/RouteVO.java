package cn.lary.message.external.wk.vo.route;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class RouteVO {

    @JSONField(format="tcp_addr")
    private String tcpAddr;

    @JSONField(format="ws_addr")
    private String wsAddr;

    @JSONField(format="wss_addr")
    private String wssAddr;
}
