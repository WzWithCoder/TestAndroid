package com.example.wangzheng.plugin;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectKit {
    /**
     * 获取pathList字段
     *
     * @param dexClassLoader 需要获取pathList字段的ClassLoader
     * @return 返回pathList字段
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Object getPathList(Object dexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return ReflectKit.getFieldValue(dexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 获取DexElements
     *
     * @param object 需要获取字段的对象
     * @return 返回获取的字段
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getDexElements(Object object)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return ReflectKit.getFieldValue(object, object.getClass(), "dexElements");
    }
    /**
     * 反射调用obj的methodName方法
     *
     * @param obj
     * @param cl
     * @param methodName
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeMethod(Object obj, Class<?> cl, String methodName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = cl.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(obj);
    }

    /**
     * 反射需要获取的字段
     *
     * @param obj       需要字段获取的对象
     * @param cl        需要获取字段的类
     * @param fieldName 需要获取的字段名称
     * @return 获取后的字段
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getFieldValue(Object obj, Class<?> cl, String fieldName)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        //反射需要获取的字段
        Field field = getField(cl, fieldName);
        return field.get(obj);
    }

    /**
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getFieldValue(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }

    /**
     * @param cl
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Field getField(Class<?> cl, String fieldName)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * 反射需要设置字段的类并设置新字段
     *
     * @param obj
     * @param cl
     * @param fieldName
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setField(Object obj, Class<?> cl, String fieldName,
                                Object value) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * 合成dex数组
     *
     * @param host   宿主应用的dex数组
     * @param target 插件应用的dex数组
     * @return
     */
    public static Object combineArray(Object host, Object target) {
        //获取原数组类型
        Class<?> localClass = host.getClass().getComponentType();
        //获取原数组长度
        int i = Array.getLength(host);
        //插件数组加上原数组的长度
        int j = i + Array.getLength(target);
        //创建一个新的数组用来存储
        Object array = Array.newInstance(localClass, j);
        //一个个的将dex文件设置到新数组中
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(array, k, Array.get(host, k));
            } else {
                Array.set(array, k, Array.get(target, k - i));
            }
        }
        return array;
    }
}
