package com.hakusai.rpc.codec;

import com.hakusai.rpc.entity.RpcRequest;
import com.hakusai.rpc.enumeration.PackageType;
import com.hakusai.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 在传输过程中，我们可以在发送的数据上加上各种必要的数据，形成自定义的协议，
 * 自定义协议与编解码器
 * 传输协议（MRF协议）
 * <p>
 * CommonEncoder 继承了MessageToByteEncoder 类，见名知义，就是把 Message（实际要发送的对象）转化成 Byte 数组。
 * CommonEncoder 的工作很简单，就是把 RpcRequest 或者 RpcResponse 包装成协议包。
 *
 * @author hakusai22@qq.com
 */


public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 首先是 4 字节魔数，表识一个协议包。
        out.writeInt(MAGIC_NUMBER);
        //接着是 Package Type，标明这是一个调用请求还是调用响应
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // Serializer Type 标明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；
        // serializer.getCode() 获取序列化器的编号，之后使用传入的序列化器将请求或响应包序列化为字节数组写入管道即可。
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        //Data Length 就是实际数据的长度，设置这个字段主要防止粘包
        out.writeInt(bytes.length);
        //，最后就是经过序列化后的实际数据，可能是 RpcRequest 也可能是 RpcResponse 经过序列化后的字节，取决于 Package Type。
        out.writeBytes(bytes);
    }

}
