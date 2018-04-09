package org.helper.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

/**
 * Created by zongzhehu on 17-1-23.
 */
public class HttpClientHelperTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientHelperTest.class);
    @Test
    public void asyncHttpGetTest() throws Exception {
        List<String> urlList = Lists.newArrayList("http://www.apache.org/", "https://www.baidu.com/",
                "https://www.qunar.com/");
        List<String> result = HttpClientHelper.asyncHttpGet(urlList);
        logger.info("size:{},content:{}", result.size(), result);
    }



    @Test
    public void asyncHttpPost() throws Exception {
        List<String> urlList = Lists.newArrayList("http://www.apache.org/", "https://www.baidu.com/",
                "https://www.qunar.com/");
        List<Map<String,String>> keyValuePair = Lists.newArrayList();
        Map<String, String> paramMaps1 = Maps.newHashMap();
        paramMaps1.put("name", "123");
        keyValuePair.add(paramMaps1);

        Map<String, String> paramMaps2 = Maps.newHashMap();
        paramMaps2.put("name", "123");
        keyValuePair.add(paramMaps2);

        Map<String, String> paramMaps3 = Maps.newHashMap();
        paramMaps3.put("name", "123");
        keyValuePair.add(paramMaps3);

        List<String> result = HttpClientHelper.asyncHttpPost(urlList, keyValuePair);
        logger.info("size:{},content:{}", result.size(), result);
    }

}