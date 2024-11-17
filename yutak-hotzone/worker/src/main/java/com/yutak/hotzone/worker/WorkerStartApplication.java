package com.yutak.hotzone.worker;

import com.yutak.hotzone.worker.netty.YutakNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkerStartApplication {

    public static void main(String[] args) {

        SpringApplication.run(WorkerStartApplication.class, args);
        YutakNettyServer.build(9001);
    }
}
