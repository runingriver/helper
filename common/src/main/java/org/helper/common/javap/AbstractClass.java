package org.helper.common.javap;

/**
 * 抽象类编译后分析
 */
public abstract class AbstractClass implements InterfaceClass {
    public abstract int process(int a, int b);

    @Override
    public int add(int a, int b) {
        return process(a, b);
    }
}
