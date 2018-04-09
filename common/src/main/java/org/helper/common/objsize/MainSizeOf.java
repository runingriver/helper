package org.helper.common.objsize;

/**
 * 环境：jdk1_8 64bit,8G RAM
 * Mark Word:8 byte; Class:4 byte; 数组头部:4 byte
 * new Object(): 8+4+字节对其 = 16
 * new Integer(1):8+4+4(int变量) = 16
 * new String():8+4+4(int hash变量) + 4(数组引用) + 字节对其 + {8+4+4(数组头部)}(char数组对象)+对其=40
 * new String("a"):24 + {8+4+4(数组头部)}(char数组对象)+2(java一个字符占2字节)+对其=48
 * new char[0]:8(Mark word)+4(class指针)+4(数组头)=16
 * new char[4]: 8+4+4 + 8(数组空间,每个cha占2byte)=24
 * new char[5]: 8+4+4 + 10(数组空间,每个cha占2byte) + 对其=32
 * new char[8]: 8+4+4 + 16(数组空间,每个cha占2byte)=32
 * A:16 B:24,C:24得出结论:C={8+4+8字节对其} + 1+1+1+对其=24;(与很多有出入,得考究)
 */
public class MainSizeOf {
    public static void main(String[] args) {
        long objSize = ObjectSizeOf.fullSizeOf(new Object());
        long intObjSize = ObjectSizeOf.fullSizeOf(new Integer(1));
        long strObjSize = ObjectSizeOf.fullSizeOf(new String());
        long strObjSize2 = ObjectSizeOf.fullSizeOf(new String("a"));
        long charSize = ObjectSizeOf.fullSizeOf(new char[4]);
        String format = String.format("full size:obj:%s intObjSize:%s,strObjSize:%s:%s,charSize:%s",
                objSize, intObjSize, strObjSize, strObjSize2, charSize);
        System.out.println(format);

        //仅计算当前对象大小,不递归到引用对象,做参考
        objSize = ObjectSizeOf.sizeOf(new Object());
        intObjSize = ObjectSizeOf.sizeOf(new Integer(1));
        strObjSize = ObjectSizeOf.sizeOf(new String());
        strObjSize2 = ObjectSizeOf.sizeOf(new String("a"));
        charSize = ObjectSizeOf.sizeOf(new char[0]);
        format = String.format("size:obj:%s intObjSize:%s,strObjSize:%s:%s,charSize:%s",
                objSize, intObjSize, strObjSize, strObjSize2, charSize);
        System.out.println(format);

        long a = ObjectSizeOf.fullSizeOf(new A());
        long b = ObjectSizeOf.fullSizeOf(new B());
        long c = ObjectSizeOf.fullSizeOf(new C());
        format = String.format("A:%s B:%s,C:%s:", a, b, c);
        System.out.println(format);

    }

    public static class A { byte b;}

    public static class B extends A { byte b;}

    public static class C extends B { byte b;}
}
