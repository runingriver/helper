package org.helper.agent;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * CGLIB原理：生成一个类，该类继承被代理的类，并重写其中相关方法！
 *
 * 反射慢的几点:1. checkcase类型检查,装箱,拆箱,参数处理;2. 反射是由JNI执行,调用本地方法，其中有一些C代码调用.
 * CGLIB的fastClass,fastMethod直接从jvm中调用方法,并对类的方法建立索引,直接通过索引调用方法!
 * 最新的jvm会优化反射的调用,如果发现一个反射方法经常被调用,jvm则会将改调用优化为本地调用的形式,
 * 可以通过`sun.reflect.inflationThreshold`参数控制JNI调用多少次后转化为本地字节码调用,默认为15.
 * cglib官方不建议在新的JVM上使用fastClass,不仅有在inflationThreshold的优化,新的JVM在反射上也有优化,但是老的JVM上可以提供更好的性能.
 * <p>
 * Parallel sorter,声称提供比jdk更快的排序方法.
 * Bean generator,运行时创建一个bean(只包含成员变量),cglib会帮助你创建get和set方法.用于动态创建一个entity类.
 * Bean copier,将一个bean中的属性值复制到另一个bean中的相同成员变量上.
 * Bulk bean,相对直接调用方法,cglib提供一个额外的方法调用方式
 * Bean map,将bean的成员变量映射为Map的形式,key为变量名的string形式,value为object.
 * Interface maker,动态创建一个接口类.
 * Mixin,将实现了两个不同接口(接口设为A,B)的类,糅合为一个对象,但是需要一个实现继承两个接口的接口(接口`C extends A,B`).
 * <p>
 * cglib还提供很多特别的功能实现,不一一列举了.
 */
public class CglibAgent {
    private static final Logger logger = LoggerFactory.getLogger(CglibAgent.class);

    public static class PersonService {
        public String sayHello(String name) {
            logger.info("====> running method");
            return "Hello " + name;
        }

        public Integer lengthOfName(String name) {
            return name.length();
        }
    }

    public static class CglibProxy implements MethodInterceptor {

        private Object srcTarget;

        private CglibProxy(Object o) {
            this.srcTarget = o;
        }

        public CglibProxy() {
        }

        /**
         * 类型T的转换会导致编译报unchecked,所以加上@SuppressWarnings
         */
        @SuppressWarnings("unchecked")
        public static <T> T proxyTarget(T t) {
            Enhancer en = new Enhancer();
            en.setSuperclass(t.getClass());
            en.setCallback(new CglibProxy(t));
            return (T) en.create();
        }

        /**
         * 推荐!!!这样封装更好,避免上述的多创建一个对象
         */
        public static <T> T proxyTarget(Class<T> c) {
            Enhancer en = new Enhancer();
            en.setSuperclass(c);
            en.setCallback(new CglibProxy());
            return c.cast(en.create());
        }


        @Override
        public Object intercept(Object obj, Method method, Object[] args,
                                MethodProxy proxy) throws Throwable {
            logger.info("proxy.getSignature().getName():{},proxy.getSuperName():{},args:{}",
                    proxy.getSignature().getName(), proxy.getSuperName(), Arrays.toString(args));
            logger.info("====> before invoke {}.", method.getName());
            Object invoke;
            if (srcTarget != null) {
                invoke = method.invoke(srcTarget, args);
            } else {
                invoke = proxy.invokeSuper(obj, args);
            }
            logger.info("====> after invoke {}.", method.getName());
            return invoke;
        }
    }


    public static void main(String[] args) {
        //PersonService personService = CglibProxy.proxyTarget(new PersonService());
        PersonService personService = CglibProxy.proxyTarget(PersonService.class);
        String hzz = personService.sayHello("hzz");
        logger.info("result:{}", hzz);

        //Enhancer enhancer = new Enhancer();
        //enhancer.setSuperclass(PersonService.class);
        //enhancer.setCallback(new MethodInterceptor() {
        //    @Override
        //    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //        logger.info("-----> before invoke {}.", method.getName());
        //        //注:method.invoke(obj, args);或proxy.invoke(obj, args); 会发生和java动态代理一样的无限循环调用
        //        Object invoke = proxy.invokeSuper(obj, args);
        //        logger.info("-----> after invoke {}.", method.getName());
        //        return invoke;
        //    }
        //});
        //PersonService proxy = (PersonService) enhancer.create();
        //String res = proxy.sayHello("hzz");
        //logger.info("res:{}", res);
    }

}
