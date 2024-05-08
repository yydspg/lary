package cn.lary.rpc.core;

// abstract byte Buf ,aim to isolate different byte Buf
public abstract class AbstractByteBuf {
    // data
    public abstract byte[] array();
    // get length of byte Buf
    public abstract int readableBytes();
    // release byte Buf
    public abstract boolean release();
}

