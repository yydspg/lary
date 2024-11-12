package com.yutak.hotzone;

public interface YUTAK {

    /**
     * event type
     */
    int LOCAL = 1;
    int REMOTE = 2;
    int REJECTED = 3;
    int CHANNEL_INACTIVE = 4;
    int WORKER_MODIFY = 5;

    /**
     * client event type
     */
    int ENTRY_LOCAL = 10002;
    int ENTRY_REMOTE = 10003;
    int ENTRY_REJECTED = 10004;

    /**
     * worker event type
     */

    byte[] DELIMITER = "$#&".getBytes();


    /**
     * push message type
     */
    byte PING = 1;


    /**
     * process message type
     */
    byte PONG = 2;

}
