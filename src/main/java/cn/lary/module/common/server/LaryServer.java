package cn.lary.module.common.server;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LaryServer {
    private static final HashMap<String, Boolean> servers = new HashMap<>();
    @PostConstruct
    public void addBusinessInviteCode() {
        servers.put("test",true);
    }
    public static boolean checkBusinessCode(String key) {
        return null != servers.get(key);
    }
}
