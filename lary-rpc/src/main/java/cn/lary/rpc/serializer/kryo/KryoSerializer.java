package cn.lary.rpc.serializer.kryo;

import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.serializer.Serializer;
import cn.lary.rpc.test.Demo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import io.netty.handler.codec.json.JsonObjectDecoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer extends Serializer {

    private KryoPool pool = KryoPoolFactory.getKryoPoolInstance();

    private final static KryoSerializer kryoSerializer = new KryoSerializer();

    private KryoSerializer(){}

    public static KryoSerializer getInstance() {
        return kryoSerializer;
    }
    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = pool.borrow();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        try {
            kryo.writeClassAndObject(output, obj);
            output.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pool.release(kryo);
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = pool.borrow();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        try {
            Object o = kryo.readClassAndObject(input);
            input.close();
            return o;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                byteArrayInputStream.close();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            pool.release(kryo);
        }
    }

    public static void main(String[] args) {
        String faafaf = new String("faafaf");
        long l = System.currentTimeMillis();

        KryoSerializer s = getInstance();

            int i =0;
            while (i<12) {
                RpcReq rpcReq = new RpcReq();
                rpcReq.setVersion("fasddf");
                rpcReq.setMethodName("fasddf");
                rpcReq.setReqId("fwddef");
                byte[] binary = s.serialize(rpcReq);
                Object deserialize = null;
                deserialize = s.deserialize(binary, RpcReq.class);
                i++;
            }
            System.out.println("kryo cost time: " + (System.currentTimeMillis() - l));
        Demo demo = new Demo();
        Kryo kryo = new Kryo();
        kryo.register(RpcReq.class);


    }
}
