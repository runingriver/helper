package org.helper.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.Semaphore;


/**
 * Created by zongzhehu on 17-2-28.
 */
public class AsyncHttpClientFactoryBeanTest {
    private static final Semaphore concurrency = new Semaphore(1024);

    @Resource
    AsyncHttpClientFactoryBean asyncClient;

    @Test
    public void asyncClientTest() throws Exception {
        Assert.assertNotNull(asyncClient);

        //step1 获取信号量控制并发数（防止内存溢出）
        concurrency.acquireUninterruptibly();

        try {
            //step2 设置HttpUrlRequest
            final HttpUriRequest httpUriRequest = RequestBuilder.get()
                    .setUri("http://www.baidu.com")
                    .build();

            //step3 执行异步调用,这里的getObject,疑问!
            asyncClient.getObject().execute(httpUriRequest, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse httpResponse) {
                    //处理Http响应
                }

                @Override
                public void failed(Exception e) {
                    //根据情况进行重试
                }

                @Override
                public void cancelled() {
                    //记录失败日志
                }
            });
        } finally {
            //step4 释放信号量
            concurrency.release();
        }
    }

}