package org.helper.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 使用连接池，来复用连接
 * http://www.baeldung.com/httpclient-connection-management
 * https://my.oschina.net/u/3352105/blog/884674
 * https://my.oschina.net/JoeyXieIsCool/blog/734730
 *
 * @author hzz 17-11-22
 */
public class PoolingHttpClientHelper {
    private static final Logger logger = LoggerFactory.getLogger(PoolingHttpClientHelper.class);

    PoolingHttpClientConnectionManager poolingConnManager;

    public <T> T get(String path, Class<T> clazz) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingConnManager)
                .build();
        //实例化GET请求
        HttpGet httpget = new HttpGet(path);
        //JSON数据格式
        String json = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpget);
            InputStream in = response.getEntity().getContent();
            json = IOUtils.toString(in, Charset.defaultCharset());
            in.close();
        } catch (UnsupportedOperationException | IOException e) {
            logger.error("http get request exception.path:{}", path, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("http get request release exception.path:{}", path, e);
                }
            }
        }
        return JSON.parseObject(json, clazz);
    }
}
