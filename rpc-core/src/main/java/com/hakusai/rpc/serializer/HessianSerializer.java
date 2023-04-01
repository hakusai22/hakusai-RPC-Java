package com.hakusai.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.hakusai.rpc.enumeration.SerializerCode;
import com.hakusai.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 基于Hessian协议的序列化器
 * 序列化为二进制
 *
 * @author hakusai22@qq.com
 */
public class HessianSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object obj) {
        //hessian输出
        HessianOutput hessianOutput = null;
        //字节数组输出流
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            //writeObject() 将传入的对象写入输出流。
            hessianOutput.writeObject(obj);
            //返回一个新分配的字节数组。它的大小是此输出流的当前大小，并且缓冲区的有效内容已复制到其中。
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭流时有错误发生:", e);
                }
            }
        }
    }

    //反序列化
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        //hessian输入流
        HessianInput hessianInput = null;
        // 将传入的字节数组 转换字节数组输入流
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            // readObject() 当类型为输入流时从输入流中读取任意Object对象
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianInput != null) hessianInput.close();
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
