package org.helper.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对发送http请求的一个封装.
 * 类的设计优点:
 * 1.类设有销毁方法
 * 2.对Http返回数据的封装:HttpDataBean
 * 3.异常的统一处理:dealWithException
 * 4.公共逻辑的处理
 * 5.int型数据采用16进制
 */
public class LightHttp {
    private static final Logger logger = LoggerFactory.getLogger(LightHttp.class);

    public final static int Success = 0x10;
    public final static int UrlFailed = 0x11;
    public final static int TimeOut = 0x12;
    public final static int ProtocolFailed = 0x13;
    public final static int EncodingFailed = 0x14;
    public final static int IOFailed = 0x15;
    public final static int NetErr = 0x16;

    private ThreadPoolExecutor threadPool;
    /**
     * 全局回调接口 GloblelghHttpListeners 注意： 个人建议，如果请求页面多的，那就不要使用全局接口。尽量采用singleInterface 否则，你需要在用户层页面的每次onResume重新设置
     */
    private LghHttpGlobleListener GloblelghHttpListeners;

    public static LightHttp getInstance() {
        return LghHttpStatic.singleLghHttp;
    }

    private static class LghHttpStatic {
        private static LightHttp singleLghHttp = new LightHttp();
    }

    /** 销毁，内存释放善后操作 */
    public void destroy() {
        if (threadPool != null) {
            if (!threadPool.isShutdown()) {
                threadPool.shutdown();
                threadPool = null;
            }
        }
        if (GloblelghHttpListeners != null) {
            GloblelghHttpListeners = null;
        }
        LghHttpStatic.singleLghHttp = null;
    }

    /**
     * lgh.httpdemo.LghHttp 基础数据类 作为 handler 传递的数据种子，只在成功时传递
     */
    private class HttpDataBean implements Serializable {

        private String response;
        private LghHttpSingleListener listeners;

        public void setResponse(String response) {
            this.response = response;
        }

        public void setListeners(LghHttpSingleListener listeners) {
            this.listeners = listeners;
        }

        public String getResponse() {
            return this.response;
        }

        public LghHttpSingleListener getListeners() {
            return this.listeners;
        }
    }

    /** 初始化函数 */
    public synchronized void init() {
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    }

    /** handler 发消息部分整合 */
    public void sendMessage(int what, int code, Object object) {

    }

    private void sendMessage(int what, Object object) {
        sendMessage(what, -1, object);
    }

    /**
     * requestCode 请求标识符，方便区分
     */

    /** Get 请求整合 */
    public void doGet(final int requestCode, final String url, final LghHttpSingleListener lghHttpListeners) {
        if (!checkConnection()) {
            lghHttpListeners.onFailed(NetErr);
            return;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                get(requestCode, url, lghHttpListeners);
            }
        };
        if (threadPool != null) {
            threadPool.execute(runnable);
        } else {
            logger.info("do get threadPool is null");
        }
    }

    private void get(int requestCode, String url, LghHttpSingleListener lghHttpListener) {
        try {
            HttpURLConnection httpURLConnection = getHttpUrlConnection(url, "GET");
            httpURLConnection.setUseCaches(false);
            sendMessage(Success, requestCode, commonGetResult(httpURLConnection, lghHttpListener));
        } catch (MalformedURLException e) {
            dealWithException(e, lghHttpListener);
        } catch (IOException e) {
            dealWithException(e, lghHttpListener);
        }
    }

    /** Post 请求整合 */
    public void doPost(final int requestCode, final String url, final String[] keys, final String[] values,
            final LghHttpSingleListener listener) {
        if (!checkConnection()) {
            listener.onFailed(NetErr);
            return;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                post(requestCode, url, keys, values, listener);
            }
        };
        if (threadPool != null) {
            threadPool.execute(runnable);
        } else {
            logger.info("do post threadPool is null");
        }
    }

    /** 采用第一种post协议，application/x-www-form-urlencoded */
    private void post(int requestCode, String url, String[] keys, String[] values, LghHttpSingleListener listener) {
        if (url == null) {
            return;
        }
        try {
            HttpURLConnection httpURLConnection = getHttpUrlConnection(url, "POST");
            httpURLConnection.setDoOutput(true); /** post 必不可少 */
            httpURLConnection.setUseCaches(false);

            if (keys != null && values != null) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                commonCombinePostText(keys, values, outputStream);
                outputStream.flush();
                outputStream.close();
            }
            sendMessage(Success, requestCode, commonGetResult(httpURLConnection, listener));
        } catch (MalformedURLException e) {
            dealWithException(e, listener);
        } catch (SocketTimeoutException e) {
            dealWithException(e, listener);
        } catch (ProtocolException e) {
            dealWithException(e, listener);
        } catch (UnsupportedEncodingException e) {
            dealWithException(e, listener);
        } catch (IOException e) {
            dealWithException(e, listener);
        }
    }

    /** 公共部分，异常集合处理 */
    private void dealWithException(Exception e, LghHttpSingleListener lghHttpListeners) {
        HttpDataBean bean = new HttpDataBean();
        bean.setListeners(lghHttpListeners);
        if (e instanceof MalformedURLException) {
            sendMessage(UrlFailed, bean);
        } else if (e instanceof SocketTimeoutException) {
            sendMessage(TimeOut, bean);
        } else if (e instanceof ProtocolException) {
            sendMessage(ProtocolFailed, bean);
        } else if (e instanceof UnsupportedEncodingException) {
            sendMessage(EncodingFailed, bean);
        } else if (e instanceof IOException) {
            sendMessage(IOFailed, bean);
        }
    }

    /** 获取一个HttpUrlConnection，合并一些公共部分 */
    private static HttpURLConnection getHttpUrlConnection(String url, String requestWay) throws IOException {
        URL mRrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) mRrl.openConnection();
        httpURLConnection.setRequestMethod(requestWay);
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.setConnectTimeout(5 * 1000);
        return httpURLConnection;
    }

    /** 获取结果公共部分 */
    private HttpDataBean commonGetResult(HttpURLConnection httpURLConnection, LghHttpSingleListener listener)
            throws IOException {
        if (httpURLConnection == null) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"),
                8 * 1024);
        StringBuffer resultBuffer = new StringBuffer("");
        String line;
        while ((line = br.readLine()) != null) {
            resultBuffer.append(line);
        }
        HttpDataBean bean = new HttpDataBean();
        bean.setResponse(resultBuffer.toString());
        bean.setListeners(listener);
        br.close();
        return bean;
    }

    /** 组合 post 文本数据公共部分 */
    private OutputStream commonCombinePostText(String[] keys, String[] values, OutputStream outputStream)
            throws IOException {
        StringBuffer requestStr = new StringBuffer();
        int keysLength = keys.length;
        for (int i = 0; i < keysLength; i++) {
            requestStr.append(keys[i] + "=" + values[i] + "&");
        }
        outputStream.write(requestStr.toString().getBytes());
        return outputStream;
    }

    /**
     * 网络判断 自己补全 context common.context
     */
    public static boolean checkConnection() {
        return false;

    }

    /** 接口分离 */
    private interface LghHttpBaseListenr {
        void onFailed(int type);
        // void onUrlFailed();
        // void onTimeOut();
        // void onProtocolFailed();
        // void onEncodingFailed();
        // void onIoFailed();
    }

    /** 全局有 requestCode 区分 */
    public interface LghHttpGlobleListener extends LghHttpBaseListenr {
        void onSuccess(int requestCode, String response);
    }

    /** 单一的没 requestCode 区分 */
    public interface LghHttpSingleListener extends LghHttpBaseListenr {
        void onSuccess(String response);
    }

}
