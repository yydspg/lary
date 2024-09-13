package cn.lary.pkg.srs.config;

import cn.lary.kit.CollectionKit;
import cn.lary.kit.StringKit;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "lary.srs")
public class SrsConfig {
    // sort by province
    private final HashMap<String,List<String>> origins = new HashMap<>();
    private final HashMap<String,List<String>> edges = new HashMap<>();

    // property di
    @JsonProperty("origin")
    private List<String> originServers;
    @JsonProperty("edge")
    private List<String> edgeServers;

    @PostConstruct
    public void init() {
        if (CollectionKit.isEmpty(originServers) || CollectionKit.isEmpty(edgeServers)) {
            log.error("srs server config error,please check your config");
            return;
        }
        originServers.forEach(s -> {
            String[] url = StringKit.split(s, "@");
            if (url == null || url.length < 2) {
                return;
            }
            if (origins.get(url[0]) == null) {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(url[1]);
                edges.put(url[0], tmp);
            }else {
                origins.get(url[0]).add(url[1]);
            }
        });
        edgeServers.forEach(s -> {
            String[] url = StringKit.split(s, "@");
            if (url == null || url.length < 2) {
                return;
            }
            if (edges.get(url[0]) == null) {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(url[1]);
                edges.put(url[0], tmp);
            }else{
                edges.get(url[0]).add(url[1]);
            }
        });
    }
    public List<String> getEdgeServerProvinces(){
        return edges.keySet().stream().toList();
    }
    public String getEdgeServerRandom(String province){
        List<String> servers = edges.get(province);
        Random r = new Random();
        int i = r.nextInt(servers.size());
        return servers.get(i);
    }
}
