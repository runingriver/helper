package org.helper.common.objsize2;

import org.helper.common.objsize.MainSizeOf;

/**
 * 经测试和基于Instrumentation的一致,可以在程序中直接测试,非常nice
 */
public class MainObjSize {
    public static void main(String[] args) throws IllegalAccessException {
        final ClassIntrospector ci = new ClassIntrospector();
        System.out.println("当前系统引用大小:" + ClassIntrospector.getObjectRefSize());

        ClassIntrospector.ObjectInfo res = ci.introspect(new Integer(1));
        System.out.println("new Integer(1):" + res.getDeepSize());

        res = ci.introspect(new String());
        System.out.println("new String():" + res.getDeepSize());

        res = ci.introspect(new String("a"));
        System.out.println("new String(\"a\"):" + res.getDeepSize());

        res = ci.introspect(new char[0]);
        System.out.println("new char[0]:" + res.getDeepSize());

        res = ci.introspect(new char[4]);
        System.out.println("new char[4]:" + res.getDeepSize());

        res = ci.introspect(new char[8]);
        System.out.println("new char[8]:" + res.getDeepSize());

        res = ci.introspect(new A());
        System.out.println("new A():" + res.getDeepSize());

        res = ci.introspect(new B());
        System.out.println("new B():" + res.getDeepSize());

        res = ci.introspect(new C());
        System.out.println("new C():" + res.getDeepSize());

        res = ci.introspect(new ObjectA());
        System.out.println("new ObjectA():" + res.getDeepSize());
    }


    public static class A {
        byte b;
    }

    public static class B extends MainSizeOf.A {
        byte b;
    }

    public static class C extends MainSizeOf.B {
        byte b;
    }

    private static class ObjectA {
        String str;  // 4 引用字节
        int i1; // 4
        byte b1; // 1
        byte b2; // 1
        int i2;  // 4
        ObjectB obj; //4 引用字节
        byte b3;  // 1
    }

    private static class ObjectB {

    }

}
