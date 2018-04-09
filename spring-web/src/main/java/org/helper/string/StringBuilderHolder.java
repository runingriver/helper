package org.helper.string;

/**
 * 参考Netty的InternalThreadLocalMap 与 BigDecimal, 放在threadLocal中重用的StringBuilder, 节约StringBuilder内部的char[]
 *
 * 不过仅在String对象较大时才有明显效果，否则抵不上访问ThreadLocal的消耗.
 *
 * <pre>
 * private static ThreadLocal<StringBuilderHolder> stringBuilderHolder = new ThreadLocal<StringBuilderHolder>() {
 * 	&#64;Override
 * 	protected StringBuilderHolder initialValue() {
 * 		return new StringBuilderHolder(512);
 * 	}
 * };
 *
 * StringBuilder sb = stringBuilderHolder.get().stringBuilder();
 *
 * </pre>
 *
 */
public class StringBuilderHolder {

    private final StringBuilder sb;

    public StringBuilderHolder(int capacity) {
        sb = new StringBuilder(capacity);
    }

    /**
     * 重置StringBuilder内部的writerIndex, 而char[]保留不动.
     */
    public StringBuilder stringBuilder() {
        sb.setLength(0);
        return sb;
    }
}
