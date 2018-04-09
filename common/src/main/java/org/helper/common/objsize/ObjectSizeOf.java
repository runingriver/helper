package org.helper.common.objsize;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

public class ObjectSizeOf {
    private static Instrumentation inst;

    public static void premain(String agentArgs, Instrumentation instP) {
        inst = instP;
    }

    /**
     * 简单计算对象的大小，不包含对象引用的对象大小
     */
    public static long sizeOf(Object o) {
        if (inst == null) {
            throw new IllegalStateException("Can not access instrumentation environment.");
        }
        return inst.getObjectSize(o);
    }

    /**
     * 计算对象的总大小,包含对象引用的对象的大小
     */
    public static long fullSizeOf(Object obj) {
        Map<Object, Object> visited = new IdentityHashMap<>();
        Stack<Object> stack = new Stack<>();
        long result = internalSizeOf(obj, stack, visited);
        //通过栈进行遍历
        while (!stack.isEmpty()) {
            result += internalSizeOf(stack.pop(), stack, visited);
        }
        visited.clear();
        return result;
    }

    /**
     * 判定哪些是需要跳过的
     */
    private static boolean skipObject(Object obj, Map<Object, Object> visited) {
        if (obj instanceof String) {
            if (obj == ((String) obj).intern()) {
                return true;
            }
        }
        return (obj == null) || visited.containsKey(obj);
    }

    private static long internalSizeOf(Object obj, Stack<Object> stack, Map<Object, Object> visited) {
        //跳过常量池对象、跳过已经访问过的对象
        if (skipObject(obj, visited)) {
            return 0;
        }
        //将当前对象放入栈中
        visited.put(obj, null);
        long result = 0;
        result += sizeOf(obj);
        Class<?> clazz = obj.getClass();
        //如果是数组
        if (clazz.isArray()) {
            // skip primitive type array
            if (clazz.getName().length() != 2) {
                int length = Array.getLength(obj);
                for (int i = 0; i < length; i++) {
                    stack.add(Array.get(obj, i));
                }
            }
            return result;
        }
        //计算非数组对象的大小
        return getNodeSize(clazz, result, obj, stack);
    }

    /**
     * 获取非数组对象自身的大小，并且可以向父类进行向上搜索
     */
    private static long getNodeSize(Class<?> clazz, long result, Object obj, Stack<Object> stack) {
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //这里抛开静态属性,抛开基本关键字（因为基本关键字在调用java默认提供的方法就已经计算过了）
                if (!Modifier.isStatic(field.getModifiers()) && !field.getType().isPrimitive()) {
                    field.setAccessible(true);
                    try {
                        Object objectToAdd = field.get(obj);
                        if (objectToAdd != null) {
                            //将对象放入栈中，一遍弹出后继续检索
                            stack.add(objectToAdd);
                        }
                    } catch (IllegalAccessException ex) {
                        assert false;
                    }
                }
            }
            //找父类class，直到没有父类
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}


