package com.hakusai.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装静态单例工厂
 *
 * @author hakusai22@qq.com
 */

public class SingletonFactory {

    private static Map<Class, Object> objectMap = new HashMap<>();
    private volatile static Object instance;

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz) {
        instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }

}
