package org.helper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Hello world!
 *
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        String[] phones = new String[5];
        logger.info("{}", phones.length);

        List<String> list = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        List<String> list2 = Lists.newArrayList("11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
        list.addAll(list2);
        logger.info("{}", list);

        int decryptCount = 3;
        int decryptSize = list.size();
        List<String> decryptList = Lists.newArrayList();

        int size = 0;
        List<String> tempList = Lists.newArrayListWithCapacity(decryptCount);
        for (String mobile : list) {
            tempList.add(mobile);
            size++;
            if (size % decryptCount == 0) {
                decryptList.addAll(tempList);
                tempList.clear();
            }
            if (size == decryptSize) {
                decryptList.addAll(tempList);
            }
        }
        logger.info("{}", decryptList.toString());

    }
}
