package cn.lary;

import cn.lary.rpc.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        RpcServer rpcServer = new RpcServer("127.0.0.1:9002");

        rpcServer.start();
        System.out.println(logger);
        logger.debug("what can i say");
        System.out.println("Hello world!");
    }
}