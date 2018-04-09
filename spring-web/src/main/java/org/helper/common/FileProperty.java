package org.helper.common;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 获得数据库的属性
 */
@Component
@Lazy(value = false)
public class FileProperty {
    private static final Logger logger = LoggerFactory.getLogger(FileProperty.class);

    /**
     * hzz修改,通过spring的方式加载properties,避免不同环境切换路径
     */
    @Resource
    Properties config;

    private static Properties properties;

    @PostConstruct
    public void init() {
        properties = config;
    }

    public String getProperty(String property, String defaultString) {
        if (StringUtils.isNoneBlank(property, defaultString)) {
            logger.warn("property parameter is empty.");
            return "";
        }
        return config.getProperty(property, defaultString);
    }

    public static String getPropertyValues(String property, String defaultString) {
        if (StringUtils.isNoneBlank(property, defaultString)) {
            logger.warn("property parameter is empty.");
            return "";
        }
        return properties.getProperty(property, defaultString);
    }

    public static String getPropertyValues(String property) {
        if (StringUtils.isBlank(property)) {
            logger.warn("property parameter is empty.");
            return "";
        }
        return properties.getProperty(property);
    }

}
