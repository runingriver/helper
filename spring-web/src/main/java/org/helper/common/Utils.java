package org.helper.common;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zongzhehu on 17-1-19.
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    //hashmap的高效遍历方式
    public static void hashMapIteratorMethod() {
        HashMap<String, String> map = Maps.newHashMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String s = next.getValue();
        }
    }

    //集合过滤

    /**
     for (int i = 0; i < buTypeList.size(); i++) {
     logger.info("{}", buTypeList.get(i));
     if (StringUtils.isBlank(buTypeList.get(i))) {
     buTypeList.remove(i);
     }
     }
     这种方式遍历会漏掉某个类,漏掉的就是在删除元素的后面那个元素
     * @param list
     * @return
     */
    public static List<String> filterList(List<String> list) {
        Collection<String> filterBuType = Collections2.filter(list, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return StringUtils.isNotBlank(input);
            }
        });
        logger.info("获取到模板业务类型数量:{}", filterBuType.size());
        return Lists.newArrayList(filterBuType);
    }



}
