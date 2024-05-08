package cn.lary.rpc.serializer.kryo;

import cn.lary.rpc.serializer.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer extends Serializer {

    private KryoPoolFactory.KryoPool pool = KryoPoolFactory.getKryoPoolInstance();

    private final static KryoSerializer kryoSerializer = new KryoSerializer();

    private KryoSerializer(){}

    public static KryoSerializer getInstance() {
        return kryoSerializer;
    }
    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = pool.create();
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
            pool.free(kryo);
        }
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = pool.create();
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
        }
    }
}
