package cn.lary.external.wk.vo.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RouteVO {

    @JsonProperty("tcp_addr")
    private String tcpAddr;

    @JsonProperty("ws_addr")
    private String wsAddr;

    @JsonProperty("wss_addr")
    private String wssAddr;
}
