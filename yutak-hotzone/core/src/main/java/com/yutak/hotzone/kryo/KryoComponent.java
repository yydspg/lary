package com.yutak.hotzone.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.util.Pool;
import com.yutak.hotzone.entry.YutakPushMessage;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class KryoComponent<T> {

    private final Class<T> clz;

    public KryoComponent(Class<T> clz) {
        this.clz = clz;
    }

    private final  Pool<KryoPair> kryoPool = new Pool<>(true, false, 512) {
        @Override
        protected KryoPair create() {
            Kryo kryo = new Kryo();
            kryo.register(clz);
            kryo.setReferences(false);
            kryo.setRegistrationRequired(true);
            Output output = new Output(512, 1024);
            Input input = new Input(512);
            return new KryoPair(kryo, output, input);
        }
    };


    public  T deserialize(byte[] data) {
        KryoPair pair = kryoPool.obtain();
        pair.getInput().setBuffer(data);
        T response = pair.getKryo().readObject(pair.getInput(), clz);
        kryoPool.free(pair);
        return response;
    }

    public  byte[] serialize(T message) {
        KryoPair pair = kryoPool.obtain();
        pair.getOutput().reset();
        pair.getKryo().writeObject(pair.getOutput(), message);
        pair.getOutput().flush();
        byte[] data = pair.getOutput().toBytes();
        kryoPool.free(pair);
        return data;
    }

    public static void main(String[] args) throws InterruptedException {
        KryoComponent<YutakPushMessage> data = new KryoComponent<>(YutakPushMessage.class);
        for (int i = 0; i < 10; i++) {
            CompletableFuture.runAsync(()->{
                YutakPushMessage m = new YutakPushMessage();
                m.setBody(String.valueOf(ThreadLocalRandom.current().nextLong()));
                byte[] b = data.serialize(m);
                YutakPushMessage message = data.deserialize(b);
                if (!Objects.equals(message.getBody(), m.getBody())){
                    System.out.println(message.getBody());
                }
                System.out.println(m.getBody());
                System.out.println(message.getBody());
            });
        }
        TimeUnit.SECONDS.sleep(100);
    }
}
