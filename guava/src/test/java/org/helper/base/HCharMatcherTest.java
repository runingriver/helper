package org.helper.base;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCharMatcherTest {
    private static final Logger logger = LoggerFactory.getLogger(HCharMatcherTest.class);

    @Test
    public void Test1() throws Exception {
        String s12 = HCharMatcher.is('*').or(HCharMatcher.is(' ')).removeFrom("(* This is a comment. *)");
        logger.info(s12);
    }

    @Test
    public void Test2() throws Exception {
        String s12 = HCharMatcher.is(' ').replaceFrom("hello test replace.",',');
        logger.info(s12);
    }

}