package org.helper.http;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zongzhehu on 17-2-28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class HttpClientFactoryBeanTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientFactoryBeanTest.class);

    @Resource
    HttpClientFactoryBean httpClientFactoryBean;

    @Test
    public void httpClientAutoWired() throws Exception {
        HttpGet httpGet = new HttpGet("https://www.baidu.com/");
        HttpResponse response = httpClientFactoryBean.getObject().execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        logger.info("状态:{}", statusCode);
    }
}