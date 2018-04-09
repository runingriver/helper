package org.helper.common.javap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zongzhehu on 17-7-11.
 */
public class EnumMain {
    private static final Logger logger = LoggerFactory.getLogger(EnumMain.class);

    public static void main(String[] args) {
        EnumPracties[] values = EnumPracties.values();
        for (EnumPracties value : values) {
            String processor = value.processor("1", "11");
            logger.info("{},{},{},{}", processor, value.getDeclaringClass().getName(), value.ordinal(), value.toString());
        }

        EnumPracties tuesday = EnumPracties.valueOf("TUESDAY");
        String processor = tuesday.processor("tuesday", "星期二");
        logger.info("{}", processor);
    }
}
