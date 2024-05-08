package cn.lary.rpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class SystemProperties {
    private static String host;

    static {
        try {
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Value("${local.server.port}")
    private static int port;

    public static String getHost() {
        return host;
    }



    public static int getPort() {
        return port;
    }

}
