package org.helper.utils;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Created by zongzhehu on 17-2-10.
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static void main(String[] args) {
        List<String> list = Lists.newArrayList("  ", " 111", " 111 ", "222", "222 ");
        List<String> list1 = Utils.modifiedAndFilter(list);
        logger.info("{}", list1.toString());

        List<String> list2 = Lists.newArrayList("111", "111", "222", "222");
        ImmutableList<String> list3 = ImmutableSet.copyOf(list2).asList();
        logger.info("{}", list3.toString());
    }

    // 将list:{" ","111"," 111 ","222 "}变成:list:{"111","222"}
    public static List<String> modifiedAndFilter(List<String> list) {
        Predicate<String> filter = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                if (StringUtils.isBlank(input)) {
                    return false;
                }
                return true;
            }
        };
        Collection<String> filterList = Collections2.filter(list, filter);
        Iterable<String> transform = Iterables.transform(filterList, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.trim();
            }
        });
        ImmutableList<String> list3 = ImmutableSet.copyOf(transform).asList();
        return list3;
    }

    // 将function的输出作为predicate的输入,但是并不会你改吧list的内容
    public static List<String> predicateComposeUse(List<String> list) {
        Predicate<String> compose = Predicates.compose(new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                if (StringUtils.isBlank(input)) {
                    logger.info("Predicates false {}", input);
                    return false;
                }
                logger.info("Predicates true {}", input);
                return true;
            }
        }, new Function<String, String>() {
            @Override
            public String apply(String input) {
                String result = null;
                if (null != input) {
                    result = input.trim();
                }
                logger.info("function {}", result);
                return result;
            }
        });
        Collection<String> filter = Collections2.filter(list, compose);
        logger.info(filter.toString());
        return Lists.newArrayList(filter);
    }

}
