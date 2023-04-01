package com.hakusai.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hakusai.rpc.entity.RpcRequest;
import com.hakusai.rpc.entity.RpcResponse;
import com.hakusai.rpc.enumeration.SerializerCode;
import com.hakusai.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo序列化器
 * Kryo 是一个快速高效的 Java 对象序列化框架，主要特点是高性能、高效和易用。
 * 最重要的两个特点，一是基于字节的序列化，对空间利用率较高，在网络传输时可以减小体积；
 * 二是序列化时记录属性对象的类型信息，这样在反序列化时就不会出现之前的问题了。
 *
 * @author hakusai22@qq.com
 */
public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    //这里 Kryo 可能存在线程安全问题，文档上是推荐放在 ThreadLocal 里，一个线程一个 Kryo。
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             // 在序列化时，先创建一个 Output 对象（Kryo 框架的概念）
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // 接着使用 writeObject 方法将对象写入 Output 中
            kryo.writeObject(output, obj);
            // 使用完进行释放
            kryoThreadLocal.remove();
            // 最后调用 Output 对象的 toByte() 方法即可获得对象的字节数组。
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             // 反序列化则是从 Input 对象中直接 readObject，这里只需要传入对象的类型，而不需要具体传入每一个属性的类型信息。
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
