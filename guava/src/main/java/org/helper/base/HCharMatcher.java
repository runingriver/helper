package org.helper.base;

/**
 * 模拟CharMatcher的实现,说不出是那种设计模式但是体现了fluent流式调用.
 * 该类将同一个类型的多个操作整合到一条代码中.
 */
public abstract class HCharMatcher {
    //首先定义一个抽象方法，任何子类继承我，都要实现我这个方法
    public abstract boolean matches(char c);

    //2. 定义一个静态方法,执行某一个操作
    public static HCharMatcher is(final char match) {
        return new HCharMatcher.Is(match);
    }

    //2.2 fluent调用另一个操作
    public HCharMatcher or(HCharMatcher other) {
        return new Or(this, other);
    }

    //操作方式一,操作实现abstract方法.
    private static final class Is extends HCharMatcher {
        private final char match;

        Is(char match) {
            this.match = match;
        }

        public boolean matches(char c) {
            return c == match;
        }

        //重写了父类的replaceFrom方法.
        public String replaceFrom(CharSequence sequence, char replacement) {
            return sequence.toString().replace(match, replacement);
        }

        public HCharMatcher or(HCharMatcher other) {
            return other.matches(match) ? other : super.or(other);
        }
    }

    //操作方式二. 操作实现abstract方法.
    private static final class Or extends HCharMatcher {
        final HCharMatcher first;
        final HCharMatcher second;

        Or(HCharMatcher a, HCharMatcher b) {
            first = a;
            second = b;
        }

        public boolean matches(char c) {
            return first.matches(c) || second.matches(c);
        }
    }

    //全局定义的方法
    public String removeFrom(CharSequence sequence) {
        String string = sequence.toString();
        int pos = indexIn(string, 0);
        if (pos == -1) {
            return string;
        }

        char[] chars = string.toCharArray();
        int spread = 1;

        OUT:
        while (true) {
            pos++;
            while (true) {
                if (pos == chars.length) {
                    break OUT;
                }
                if (matches(chars[pos])) {
                    break;
                }
                chars[pos - spread] = chars[pos];
                pos++;
            }
            spread++;
        }
        return new String(chars, 0, pos - spread);
    }

    //全局定义的方法
    public int indexIn(CharSequence sequence, int start) {
        int length = sequence.length();
        for (int i = start; i < length; i++) {
            if (matches(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    //全局定义的方法,此方法被子类Is所覆写
    public String replaceFrom(CharSequence sequence, char replacement) {
        String string = sequence.toString();
        int pos = indexIn(string,0);
        if (pos == -1) {
            return string;
        }
        char[] chars = string.toCharArray();
        chars[pos] = replacement;
        for (int i = pos + 1; i < chars.length; i++) {
            if (matches(chars[i])) {
                chars[i] = replacement;
            }
        }
        return new String(chars);
    }
}
