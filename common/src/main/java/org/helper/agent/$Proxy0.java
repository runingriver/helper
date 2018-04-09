package org.helper.agent;

//生产的代理类在com.sun.proxy包下！
//package com.sun.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

import org.helper.agent.JdkAgent.Hello;

/**
 * idea反编译$Proxy0.class文件的结果,jdk动态代理生产的代理类!
 * 此类是加上-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true参数后生成的字节码反编译结果
 */
public final class $Proxy0 extends Proxy implements Hello {
    private static Method m1;
    private static Method m4;
    private static Method m6;
    private static Method m2;
    private static Method m5;
    private static Method m3;
    private static Method m0;

    /**
     * constructor.newInstance(invocationHandler),将包含了HelloImplements实例的对象传入Proxy中
     */
    public $Proxy0(InvocationHandler var1) throws Exception {
        super(var1);
    }

    public final boolean equals(Object var1) {
        try {
            return ((Boolean) super.h.invoke(this, m1, new Object[]{var1})).booleanValue();
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String getInfos1() {
        try {
            return (String) super.h.invoke(this, m4, (Object[]) null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void display() {
        try {
            super.h.invoke(this, m6, (Object[]) null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toString() {
        try {
            return (String) super.h.invoke(this, m2, (Object[]) null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String getInfos2() {
        try {
            return (String) super.h.invoke(this, m5, (Object[]) null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setInfo(String var1, String var2) {
        try {
            super.h.invoke(this, m3, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final int hashCode() {
        try {
            return ((Integer) super.h.invoke(this, m0, (Object[]) null)).intValue();
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m4 = Class.forName("org.helper.agent.JdkAgent$Hello").getMethod("getInfos1");
            m6 = Class.forName("org.helper.agent.JdkAgent$Hello").getMethod("display");
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m5 = Class.forName("org.helper.agent.JdkAgent$Hello").getMethod("getInfos2");
            m3 = Class.forName("org.helper.agent.JdkAgent$Hello").getMethod("setInfo", Class.forName("java.lang.String"), Class.forName("java.lang.String"));
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
