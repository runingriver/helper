/**
 * 说明javac配合javap研究底层执行原理!
 * javac: 将java文件编译成class文件
 * javap: 将class文件反编译成可读的形式
 */
package org.helper.common.javap;

/*
* javac使用: cd到java文件目录,然后: javac app.java 注:class文件默认生成在当前目录!
* javap使用: cd到class文件目录
* 1. 查看反编译类:javap app.class
* 2. 查看字节码: javap -c app.class
* 3. 查看额外信息,比如常量池,方法栈等. javap -v app.class
* */

/**
 * 关于枚举的几个结论:
 * 1. 枚举(EnumPracties)编译后是一个抽象类,继承java.lang.Enum,Enum帮助我们实现了name,tostring,ordinal等基本方法.
 * 2. 每一个枚举对应会生产一个类,实现抽象类(EnumPracties).
 * 3. 枚举index从0开始.
 * 4. 每一个枚举在EnumPracties中代表一个变量:public static final org.helper.common.javap.EnumPracties WEDNESDAY;
 *    上面的对象会在静态方块中被实例化!
 * 5. 编译器帮助我们在EnumPracties中实现了valueOf,values方法.
 * 6. 编译器会自动在构造函数前面增加两个形参,String name, int ordinal,用于Enum的构造函数赋值.
 */




