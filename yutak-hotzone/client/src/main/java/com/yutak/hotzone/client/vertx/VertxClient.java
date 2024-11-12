package com.yutak.hotzone.client.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxClient {

    private static final VertxClient INSTANCE = new VertxClient();

    private static final Logger log = LoggerFactory.getLogger(VertxClient.class);


    public static VertxClient getInstance() {return INSTANCE;}




}
