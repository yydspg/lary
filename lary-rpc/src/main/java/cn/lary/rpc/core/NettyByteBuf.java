package cn.lary.rpc.core;

// maybe will use in future
public class NettyByteBuf extends AbstractByteBuf {

    @Override
    public byte[] array() {
        return new byte[0];
    }

    @Override
    public int readableBytes() {
        return 0;
    }

    @Override
    public boolean release() {
        return false;
    }
}
