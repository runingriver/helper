package org.helper.base;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.List;

/**
 * CharMatcher使用方法
 */
public class CharMatcherHelper {
    public static void main(String[] args) {
        //1. Splitter与CharMatcher合用
        String string = "hello world,how,are:you";
        System.out.println(splitAndCharMatcher(string));
        //2. 过滤小写字符
        String allButLowerCase = CharMatcher.javaLowerCase().negate().retainFrom("B-double E double R U-N beer run");
        //3. trim字符
        String l45 = CharMatcher.whitespace().trimLeadingFrom("       Some String       ");
        String r21 = CharMatcher.whitespace().trimTrailingFrom("       Some String       ");
        String t1 = CharMatcher.whitespace().trimFrom("       Some String       ");
        //4. 移除匹配字符
        String s12 = CharMatcher.is('*').or(CharMatcher.is(' ')).removeFrom("(* This is a comment. *)");
        //5. 范围匹配
        boolean f12 = CharMatcher.inRange('A', 'C').matches('B');
        //6. 范围筛选
        String t12 = CharMatcher.inRange('0', '9').retainFrom("123-456-7890");
        //7. 字符替换
        String address = "505 Williams Street";
        String addressWithDash = CharMatcher.whitespace().collapseFrom(address, '-');
        //8. 如果担心性能，就用下面的高效方法来做
        CharMatcher digits = CharMatcher.inRange('0', '9').precomputed();
        String teleNumber = digits.retainFrom("123-456-7890");

    }

    public static List<String> splitAndCharMatcher(String line) {
        final CharMatcher split_sign = CharMatcher.whitespace().or(CharMatcher.is(':')).or(CharMatcher.is(','));
        final Splitter line_splitter = Splitter.on(split_sign).omitEmptyStrings().trimResults();
        List<String> splitResult = line_splitter.splitToList(line);
        return splitResult;
    }

}
