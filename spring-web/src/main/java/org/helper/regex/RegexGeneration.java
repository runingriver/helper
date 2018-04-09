package org.helper.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.lanwen.verbalregex.VerbalExpression;

/**
 * 正则生成器
 */
public class RegexGeneration {
    private static final Logger logger = LoggerFactory.getLogger(RegexGeneration.class);

    public static void demo() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("a").anything().endOfLine().then("b")
                .build();
        logger.info(testRegex.toString());

        String str = "a123dddb";
        logger.info("{}", testRegex.test(str));
    }

    public static void demo1() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("http").maybe("s").then("://")
                .maybe("www.").anythingBut(" ").endOfLine().build();

        String url = "https://www.google.com";

        // 测试url是否match,精确匹配
        boolean b1 = testRegex.testExact(url);
        // 测试url中是否包含
        boolean b2 = testRegex.test(url);
        logger.info(testRegex.toString());
    }

    public static void demo2() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("abc").or("def").build();

        String testString = "defzzz";

        testRegex.test(testString); // true
        testRegex.testExact(testString); // false
        testRegex.getText(testString); // returns: def
    }

    public static void demo3() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex().find("a").capture().find("b").anything().endCapture()
                .then("cd").build();

        regex.getText(text); // returns "abcd"
        regex.getText(text, 1); // returns "b"

        VerbalExpression build = VerbalExpression.regex().wordChar().nonWordChar()
                .space().nonSpace()
                .digit().nonDigit().build();
    }

    public static void demo4() {
        String text = "352017102286135ttB3721300113429864422041";
        String date = "20171108";
        String mobile = "86189k7TT2690";
        VerbalExpression regex = VerbalExpression.regex().startOfLine().digit()
                .count(10).find(mobile).then("00").digit().count(15).endOfLine().build();
        logger.info("regex:{}", regex.toString());
        logger.info("is match:{}", regex.test(text));

        VerbalExpression regex2 = VerbalExpression.regex().startOfLine()
                .digit().count(2).then(date)
                .anything().count(15)
                .digit().count(15).endOfLine().build();
        logger.info("regex:{}", regex2.toString());
        logger.info("is match:{}", regex2.test(text));

        VerbalExpression regex3 = VerbalExpression.regex().startOfLine()
                .digit().count(2).then(date)
                .anything().count(15)
                .digit().count(15).endOfLine().build();
        logger.info("regex:{}", regex3.toString());
        logger.info("is match:{}", regex3.test(text));
    }

    public static void main(String[] args) {
        demo4();
    }
}
