package cn.lary.rpc.codec;

import cn.lary.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcEncoder extends MessageToByteEncoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private final Class<?> genericClass;
    private final  Serializer serializer;

    public RpcEncoder(Class<?> genericClass, Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)){
            try {
                byte[] bytes = serializer.serialize(o);
                byteBuf.writeInt(bytes.length);
                byteBuf.writeBytes(bytes);
            } catch (Exception e) {
                logger.error("Encode error: [{}]" ,e.getMessage());
            }
        }
    }
}
