package com.pixelcraft.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.pixelcraft.ui.MainController;

public class ReflectionUtil {

    private ReflectionUtil() {
        // Do not instantiate
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }

    public static void injectField(Object obj, String fieldName, Object value) throws Exception {
        Field field = MainController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
