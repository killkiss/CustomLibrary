package com.push.jzb.utils;

import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * create：2022/5/18 9:31
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class ReflexTool {
    private static final ReflexTool ourInstance = new ReflexTool();

    public static ReflexTool getDefault() {
        return ourInstance;
    }

    private ReflexTool() {
    }

    /**
     * 获取成员变量
     */
    public Field getField(Class<?> clazz, String fieldName) {
        return getSecrecyField(clazz, fieldName);
    }

    /**
     * 获取非public类型变量
     */
    public Field getSecrecyField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    /**
     * 获取指定方法
     */
    public Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getSecrecyMethod(clazz, methodName, parameterTypes);
    }

    /**
     * 获取非public方法
     */
    public Method getSecrecyMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * 根据参数类型获取构造方法
     */
    public Constructor<?> getConstructor(Class<?> clazz, Class<?>... paramClazz) {
        return getSecrecyConstructor(clazz, paramClazz);
    }

    /**
     * 获取非public构造方法
     */
    public Constructor<?> getSecrecyConstructor(Class<?> clazz, Class<?>... paramClazz) {
        Constructor<?> declaredConstructor = null;
        try {
            declaredConstructor = clazz.getDeclaredConstructor(paramClazz);
            declaredConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return declaredConstructor;
    }

    /**
     * 获取匿名内部类
     */
    public Object getAnonymousInnerClass(Object object, String className) {
        Object o = null;
        Field field = getSecrecyField(object.getClass(), className);
        try {
            o = field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 获取静态内部类、成员内部类对象
     */
    public Object getInnerClass(Object object, String className) {
        Class<?>[] innerClasses = object.getClass().getDeclaredClasses();
        for (Class<?> innerClass : innerClasses) {
            if (TextUtils.equals(innerClass.getSimpleName(), className)) {
                String modifier = Modifier.toString(innerClass.getModifiers());
                if (modifier.contains("static")) {
                    return newInstance(innerClass);
                } else {
                    Constructor<?> constructor = getConstructor(innerClass, object.getClass());
                    return newInstance(constructor, object);
                }
            }
        }
        return null;
    }

    /**
     * 根据类全限定名获取Class对象
     */
    public Class<?> getClass(String className) {
        return getClass(className, ReflexTool.class.getClassLoader());
    }

    /**
     * 根据类全限定名和ClassLoader获取Class对象
     */
    public Class<?> getClass(String className, ClassLoader loader) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, false, loader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 通过class对象创建实例
     * 调用无参public构造方法
     */
    public Object newInstance(Class<?> clazz) {
        Object o = null;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 创建实例
     */
    public Object newInstance(Constructor<?> constructor, Object... args) {
        Object o = null;
        try {
            o = constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 执行方法
     */
    public Object invokeMethod(Method method, Object receiver, Object... args) {
        Object o = null;
        try {
            o = method.invoke(receiver, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return o;
    }
}
