package com.yutak.hotzone.client.handler;

import com.yutak.hotzone.client.worker.WorkerManager;
import com.yutak.hotzone.entry.YutakEntry;
import com.yutak.hotzone.entry.YutakPushMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NettyKeyPushHandler {

    private static final Logger log = LoggerFactory.getLogger(NettyKeyPushHandler.class);

    public void send(List<YutakEntry> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        Map<Channel,List<YutakEntry>> map = new HashMap<>();
        for (YutakEntry entry : data) {
            Channel channel = WorkerManager.getChannel(entry.getK());
            if(channel == null) {
                return ;
            }
            List<YutakEntry> response = map.computeIfAbsent(channel, k -> new ArrayList<>());
            response.add(entry);
        }
        map.keySet().forEach(channel -> {
            YutakPushMessage message = new YutakPushMessage();
            message.setEntries(map.get(channel));
            try {
                channel.writeAndFlush(message);
            } catch (Exception e) {
                String address = channel.remoteAddress().toString();
                log.error("yutak {} push error{}", address, e.getMessage());
                WorkerManager.unregister(address);
                channel.close();
            }
        });
    }
}
