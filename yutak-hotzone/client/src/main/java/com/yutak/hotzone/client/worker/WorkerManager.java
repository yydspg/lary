package com.yutak.hotzone.client.worker;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkerManager {

    private static final List<Server> WORKER_HOLDER = new CopyOnWriteArrayList<>();
    private final static int MAX =  2147483647;
    private WorkerManager() {
    }

    public static List<Server> getWorkers() {
        return WORKER_HOLDER;
    }

    public static void register(Server server) {
        WORKER_HOLDER.add(server);
    }
    public static void unregister(String address) {
        WORKER_HOLDER.removeIf(server -> address.equals(server.address));
    }
    public static List<String> getNonConnectedWorkers() {
        return WORKER_HOLDER.stream()
                .filter(Server::getChannelStatus)
                .map(t->t.address)
                .toList();
    }
    public static boolean getConnectStatus(String address){
        Optional<Server> data = WORKER_HOLDER.stream()
                .filter(t -> t.address.equals(address))
                .findFirst();
        boolean exists = data.isEmpty();
        if (exists){
            return false;
        }
        return data.get().getChannelStatus();
    }

    public  static Channel getChannel(String K) {
        int index = spread(K.hashCode()) / WORKER_HOLDER.size();
        return WORKER_HOLDER.get(index).channel;
    }

    public static   int spread(int h){
        return (h ^ h >>> 16) & MAX;
    }

    public static long addressConvert(String address){
        String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid IP:Port format");
        }

        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);

        // Split the IP address into its components
        String[] ipParts = ip.split("\\.");
        if (ipParts.length != 4) {
            throw new IllegalArgumentException("Invalid IP address format");
        }
        long ipAsLong = 0;
        for (int i = 0; i < 4; i++) {
            ipAsLong = (ipAsLong << 8) | (Integer.parseInt(ipParts[i]) & 0xFF);
        }
        long portAsLong = (port & 0xFFFFL);
        return (ipAsLong << 16) | portAsLong;
    }
    public static class Server implements Comparable<Server> {

        private String address;
        private Channel channel;

        public Server(String address, Channel channel) {
            this.address = address;
            this.channel = channel;
        }

        public boolean getChannelStatus() {
            return channel != null && channel.isActive();
        }

        @Override
        public int compareTo(Server o) {
            return this.address.compareTo(o.address);
        }

        @Override
        public String toString() {
            return "Server{" +
                    "address='" + address + '\'' +
                    ", channel=" + channel +
                    '}';
        }
    }
}
