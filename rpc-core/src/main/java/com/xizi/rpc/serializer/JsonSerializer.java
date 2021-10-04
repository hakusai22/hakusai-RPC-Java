package com.xizi.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.enumeration.SerializerCode;
import com.xizi.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * 使用JSON格式的序列化器  序列化工具我使用的是 Jackson
 * 序列化和反序列化都比较循规蹈矩，把对象翻译成字节数组，和根据字节数组和 Class 反序列化成对象。
 * JSON 的序列化器有一个毛病，就是在某个类的属性反序列化时，
 * 如果属性声明为 Object 的，就会造成反序列化出错，通常会把 Object 属性直接反序列化成 String 类型，就需要其他参数辅助序列化。
 * 并且，JSON 序列化器是基于字符串（JSON 串）的，占用空间较大且速度较慢。
 * @author xizizzz
 */
public class JsonSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public byte[] serialize(Object obj) {
        try {
            //writeValueAsBytes() 可用于将任何 Java 值序列化为字节数组的方法。
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }


    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            //readValue() 从给定的 JSON 内容字符串反序列化 JSON 内容的方法。
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    /**
        这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
        需要重新判断处理
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParamTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    //获得该序列化器的编号
    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }

}
