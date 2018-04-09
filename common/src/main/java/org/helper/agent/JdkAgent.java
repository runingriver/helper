package org.helper.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 实现jdk的动态代理
 * $Proxy0和$Proxy0.class都是jdk动态代理生成的类。
 * 动态代理原理就是生成一个代理类，把目标类作为成员变量，并获取目标类的接口方法，从而实现aop功能
 *
 * @author hzz 18-1-4
 */
public class JdkAgent {
    /**
     * 定义了一个接口
     */
    public interface Hello {
        String getInfos1();

        String getInfos2();

        void setInfo(String infos1, String infos2);

        void display();
    }

    /**
     * 定义它的实现类
     */
    public static class HelloImplements implements Hello {

        private volatile String infos1;

        private volatile String infos2;

        @Override
        public String getInfos1() {
            return infos1;
        }

        @Override
        public String getInfos2() {
            return infos2;
        }

        @Override
        public void setInfo(String infos1, String infos2) {
            this.infos1 = infos1;
            this.infos2 = infos2;
        }

        @Override
        public void display() {
            System.out.println("\t\t" + infos1 + "\t" + infos2);
        }
    }

    /**
     * 定义AOP的Agent
     */
    public static class AOPFactory implements InvocationHandler {

        private Object proxyed;

        public AOPFactory(Object proxyed) {
            this.proxyed = proxyed;
        }

        public void printInfo(String info, Object... args) {
            System.out.println(info);
            if (args == null) {
                System.out.println("\t空值。");
            } else {
                for (Object obj : args) {
                    System.out.println(obj);
                }
            }
        }

        @Override
        public Object invoke(Object proxyed, Method method, Object[] args) throws IllegalArgumentException,
                IllegalAccessException,
                InvocationTargetException {
            System.out.println("\n\n====>调用方法名：" + method.getName());
            Class<?>[] variables = method.getParameterTypes();
            for (Class<?> typevariables : variables) {
                System.out.println("=============>" + typevariables.getName());
            }
            printInfo("传入的参数为：", args);
            Object result = method.invoke(this.proxyed, args);
            printInfo("返回的参数为：", result);
            printInfo("返回值类型为：", method.getReturnType());
            return result;
        }
    }

    /**
     * 简单写法，封装了代理类的生成逻辑
     */
    public static Object getBean(String className) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Object obj = Class.forName(className).newInstance();
        InvocationHandler handler = new AOPFactory(obj);
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj
                .getClass().getInterfaces(), handler);
    }


    /**
     * 这里有两种写法，我们采用略微复杂的一种写法,理解生成原理
     */
    public static Object getBean2(String classsName) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        //1.生成一个Hello的代理类Proxy0,并加载到jvm
        Class<?> proxyClass = Proxy.getProxyClass(JdkAgent.class.getClassLoader(), Hello.class);
        //2.把HelloImplements当做InvocationHandler类的成员变量
        final InvocationHandler ih = new AOPFactory(new HelloImplements());
        //3.拿到Proxy0的构造函数
        final Constructor<?> cons = proxyClass.getConstructor(InvocationHandler.class);
        //4. 实例化Proxy0,返回
        return cons.newInstance(ih);
    }

    /**
     * 加上-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true参数可将生成的字节码保存到磁盘
     * idea中会放入根目录下,不是target目录下!
     */
    public static void main(String[] args) {
        try {
            //内部类要用$
            Hello hello = (Hello) getBean2("org.helper.agent.JdkAgent$HelloImplements");
            //Hello hello = (Hello) getBean("org.helper.agent.JdkAgent$HelloImplements");
            hello.setInfo("xieyu1", "xieyu2");
            hello.getInfos1();
            hello.getInfos2();
            hello.display();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
