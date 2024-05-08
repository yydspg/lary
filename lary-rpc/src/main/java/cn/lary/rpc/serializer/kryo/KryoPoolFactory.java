package cn.lary.rpc.serializer.kryo;


import cn.lary.rpc.codec.RpcReq;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;

public class KryoPoolFactory {
    private static volatile KryoPoolFactory poolFactory = null;

    private  KryoPool kryoPool;
    private KryoPoolFactory(){}

    public static KryoPool getKryoPoolInstance(){
        if(poolFactory == null){
            synchronized (KryoPoolFactory.class){
                if(poolFactory == null){
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }
    public KryoPool getPool() {
        return kryoPool;
    }

    public class KryoPool extends Pool<Kryo>{

        public KryoPool(boolean threadSafe, boolean softReferences) {
            super(threadSafe, softReferences);
        }

        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(RpcReq.class);
            kryo.setReferences(false);
            return kryo;
        }
    }
}
