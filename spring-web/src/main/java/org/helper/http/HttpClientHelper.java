package org.helper.http;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * HttpClient默认使用连接池:PoolingHttpClientConnectionManager
 * HttpClient的使用示例,使用见测试 addHeader与setHeader区别
 * 1、addHeader，如果同名header已存在，则追加至原同名header后面。
 * 2、setHeader，如果同名header已存在，则覆盖一个同名header。
 * 优化策略:
 * 1. 设置连接池线程数(默认10),设置route(请求域名数,即同时可以连接的路由数,默认5,)
 * 2. 设置gzip,keepalive等参数.
 *
 * 什么是一个route？
 * 这里route的概念可以理解为 运行环境机器 到 目标机器的一条线路。
 * 举例来说，我们使用HttpClient的实现来分别请求 www.baidu.com 的资源和 www.bing.com 的资源那么他就会产生两个route。
 * 这里为什么要特别提到route最大连接数这个参数呢，因为这个参数的默认值为2，如果不设置这个参数值默认情况下对于同一个目标机器的最大并发连接只有2个！
 * 这意味着如果你正在执行一个针对某一台目标机器的抓取任务的时候，哪怕你设置连接池的最大连接数为200，
 * 但是实际上还是只有2个连接在工作，其他剩余的198个连接都在等待，都是为别的目标机器服务的。
 *
 * 每个ttpClient对象实例化时，如果不手动指定PoolingHttpClientConnectionManager，则会创建一个默认的PoolingHttpClientConnectionManager对象。
 * 两种方式：1. 全局的PoolingManager；2.使用默认的，方法内处理多个连接请求；
 */
public class HttpClientHelper {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);

    /**
     * 异步发送多个get请求
     *
     * @param urlList get请求的url包含请求参数
     * @return 结果
     */
    public static List<String> asyncHttpGet(List<String> urlList) {
        final List<String> result = Lists.newArrayList();
        final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000)
                .build();
        final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
                .build();

        httpclient.start();
        try {
            final List<HttpGet> httpGets = Lists.newArrayList();
            for (String url : urlList) {
                httpGets.add(new HttpGet(url));
            }

            final CountDownLatch latch = new CountDownLatch(httpGets.size());
            for (final HttpGet request : httpGets) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {
                    public void completed(HttpResponse httpResponse) {
                        int statusCode = httpResponse.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            try {
                                result.add(EntityUtils.toString(httpResponse.getEntity()));
                            } catch (IOException e) {
                                logger.error("获取http返回数据异常.", e);
                            }
                        }
                        latch.countDown();
                        logger.info("completed");
                    }

                    public void failed(Exception e) {
                        logger.info("failed");
                        latch.countDown();
                    }

                    public void cancelled() {
                        logger.info("cancelled");
                        latch.countDown();
                    }
                });
            }
            latch.await();
            logger.info("Shutting down");
        } catch (InterruptedException e) {
            logger.error("通过步获取get结果异常", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.error("httpclient关闭失败", e);
                }
            }
        }
        logger.info("Done");
        return result;
    }

    /**
     * 异步post请求,未测试!
     *
     * @param urlList      url集合
     * @param keyValuePair 参数集合
     * @return 结果集合
     */
    public static List<String> asyncHttpPost(List<String> urlList, List<Map<String, String>> keyValuePair) {
        final List<String> httpPostResult = Lists.newArrayList();
        if (urlList.size() != keyValuePair.size()) {
            return httpPostResult;
        }
        final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000)
                .build();
        final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
                .build();

        httpclient.start();
        try {
            final List<HttpPost> httpPosts = Lists.newArrayList();
            int i = 0;
            for (String url : urlList) {
                Map<String, String> params = keyValuePair.get(i++);
                HttpPost httpPost = new HttpPost(url);
                buildHttpPost(params, httpPost);
            }

            final CountDownLatch latch = new CountDownLatch(httpPosts.size());
            for (HttpPost httpPost : httpPosts) {
                httpclient.execute(httpPost, new FutureCallback<HttpResponse>() {
                    public void completed(HttpResponse result) {
                        int statusCode = result.getStatusLine().getStatusCode();
                        logger.info("status code:{}", statusCode);
                        if (statusCode == 200) {
                            try {
                                httpPostResult.add(EntityUtils.toString(result.getEntity()));
                            } catch (IOException e) {
                                logger.error("获取http返回数据异常.", e);
                            }
                        }
                        latch.countDown();
                        logger.info("completed");
                    }

                    public void failed(Exception ex) {
                        latch.countDown();
                        logger.info("failed");
                    }

                    public void cancelled() {
                        latch.countDown();
                        logger.info("cancelled");
                    }
                });
            }
            latch.await();
            logger.info("Shutting down");
        } catch (UnsupportedEncodingException e) {
            logger.error("通过步获取post结果异常", e);
        } catch (InterruptedException e) {
            logger.error("通过步获取post结果异常", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.error("httpclient关闭失败", e);
                }
            }
        }
        logger.info("Done");
        return httpPostResult;
    }

    private static void buildHttpPost(Map<String, String> params, HttpPost httpPost)
            throws UnsupportedEncodingException {
        if (null != params && !params.isEmpty()) {
            List<NameValuePair> nvps = Lists.newArrayList();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                nvps.add(new BasicNameValuePair(key, value));
            }
            UrlEncodedFormEntity encodeParams = new UrlEncodedFormEntity(nvps, "UTF-8");
            httpPost.setEntity(encodeParams);
        }
    }

    // 设置http超时为3秒
    private static final int TIMEOUT = 3000;

    /**
     * 使用httpclient提交表单,可以修改Header提交别的请求.
     *
     * @param url    请求地址
     * @param params 请求参数,键值对形式
     * @return http返回结果
     */
    public static String httpPostForm(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //这里定制设置:代理,gzip,连接池线程数,路由数,...,等等.
        //CloseableHttpClient httpclient = HttpClients.custom().setMaxConnTotal(200).setMaxConnPerRoute(20).build();

        CloseableHttpResponse response = null;
        try {
            buildHttpPost(params, httpPost);

            response = httpclient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            if (null != entity) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
            logger.error("发送http post请求异常,url:{},parmas:{}", url, params.toString(), e);
        } finally {
            closeResources(httpclient, response);
        }
        return null;
    }

    // 关闭http资源
    private static void closeResources(CloseableHttpClient httpclient, CloseableHttpResponse response) {
        if (httpclient != null) {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("关闭CloseableHttpClient异常", e);
            }
        }

        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("关闭CloseableHttpResponse异常", e);
            }
        }
    }

    public String httpGet(String url) {
        // 使用默认重试三次请求
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 请求获取,记录请求时间和异常次数
        BufferedReader br = null;
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                br = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

                // 获取结果,方式1
                // StringBuilder resultBuilder = new StringBuilder();
                // String line;
                // while ((line = br.readLine()) != null) {
                // resultBuilder.append(line);
                // }

                // 方式二
                return CharStreams.toString(br);
            }
        } catch (Exception e) {
            logger.error("url：{};", url, e);
        } finally {
            closeResources(httpclient, response);
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("关闭BufferedReader异常", e);
                }
            }
        }
        return null;
    }

    /**
     * post方式提交json代码
     *
     * @throws Exception
     */
    public static String postJson(String url, String json) throws Exception {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = null;
        // 接收响应结果
        CloseableHttpResponse response = null;
        try {
            // 创建httppost
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            StringEntity se = new StringEntity(json);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");// 发送json需要设置contentType
            httpPost.setEntity(se);
            response = httpclient.execute(httpPost);
            // 解析返结果
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resStr = EntityUtils.toString(entity, "UTF-8");
                return resStr;
            }
        } catch (Exception e) {
            logger.error("url：{};", url, e);
        } finally {
            closeResources(httpclient, response);
        }
        return null;
    }

    /**
     * 如果出现大量CLOSE_WAIT,肯定是代码的问题,下面可能的解决方案
     *
     * @param urlPath
     * @return
     */
    public static String httpGetTIMEWAIT(String urlPath) {
        StringBuffer sb = new StringBuffer();
        CloseableHttpClient client = null;
        InputStream in = null;
        InputStreamReader isr = null;
        HttpGet get = new HttpGet();
        try {
            client = HttpClients.createDefault();
            get.setURI(new URI(urlPath));
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                //如果不abort,可能会导致不关闭连接,停留在CLOSE_WAIT状态.
                //采用abort()可以主动关闭连接.
                get.abort();
                return null;
            }
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                in = entity.getContent();
            }
            return sb.toString();

        } catch (Exception e) {
            get.abort();
            e.printStackTrace();
            return null;
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
