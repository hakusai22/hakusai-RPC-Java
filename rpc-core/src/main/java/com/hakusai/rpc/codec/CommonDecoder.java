package com.hakusai.rpc.codec;

import com.hakusai.rpc.entity.RpcRequest;
import com.hakusai.rpc.entity.RpcResponse;
import com.hakusai.rpc.enumeration.PackageType;
import com.hakusai.rpc.enumeration.RpcError;
import com.hakusai.rpc.exception.RpcException;
import com.hakusai.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * 通用的解码拦截器
 * CommonDecoder 继承自 ReplayingDecoder ，与 MessageToByteEncoder 相反，它用于将收到的字节序列还原为实际对象。
 *
 * @author hakusai22@qq.com
 */

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);

    //魔数
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        // 魔术判断
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //读取包的code 判断是发送 还是响应
        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        // 判断序列化器
        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        // 发送的长度
        int length = in.readInt();
        // 读取的数据
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        // 根据数据和包的类型通过序列化器进行反编译
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }

}
