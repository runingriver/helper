package org.helper.http;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * OKHttp 使用示例
 */
public class OKHttpHelper {
    private static final Logger logger = LoggerFactory.getLogger(OKHttpHelper.class);
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient CLIENT;

    static {
        CLIENT = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时
                .writeTimeout(10, TimeUnit.SECONDS) //写超时
                .readTimeout(30, TimeUnit.SECONDS) //读超时
                .build();
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String generateSHA1ByHexDigest(String randStr) {
        String hexDigestSha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(randStr.getBytes("UTF-8"));
            hexDigestSha1 = byteToHex(crypt.digest());

        } catch (Exception e) {
            logger.error("生成signature失败:{}", e);
        }
        return hexDigestSha1;
    }

    private static Map<String, String> getHeaderMap() {
        String access_key = "xxx";
        String access_secret = "xxx";
        String timestamp = String.valueOf(new Date().getTime()).substring(0, 10);
        String nonce = UUID.randomUUID().toString().replace("-", "");
        ArrayList<String> nonceList = Lists.newArrayList(timestamp, access_secret, nonce);
        Collections.sort(nonceList);
        String nonceListStr = StringUtils.join(nonceList, "");
        String signature = generateSHA1ByHexDigest(nonceListStr);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("X-AccessKey", access_key);
        headers.put("X-Signature", signature);
        headers.put("X-Timestamp", timestamp);
        headers.put("X-Nonce", nonce);
        return headers;
    }

    private static String sendRequest(Request request) {
        //创建响应对象
        try {
            Response response = CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("send request error,status code:{},rsp:{}", response.code(), response.body().string());
                return "";
            }
            return response.body().string();
        } catch (IOException e) {
            logger.error("send request error:{}", e.getCause());
            return "";
        }
    }

    private static void buildHeader(Map<String, String> headers, Request.Builder builder) {
        if (null != headers && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.header(key, value);
            }
        }
    }

    /**
     * get请求,支持http和https
     */
    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        //1. Builder对象
        Request.Builder builder = new Request.Builder();

        //处理参数
        if (null != params && params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                stringBuilder.append(key).append("=").append(value).append("&");
            }

            String param = stringBuilder.toString();
            url += param.substring(0, param.lastIndexOf("&"));
        }

        // 2. 处理请求头
        buildHeader(headers, builder);
        // 3. build and Request
        Request request = builder.url(url).build();
        return sendRequest(request);
    }

    /**
     * post请求,支持http和https
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers) {
        //Builder对象
        Request.Builder builder = new Request.Builder();
        //处理请求头
        buildHeader(headers, builder);

        if (null != params && params.size() > 0) {
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
            RequestBody formBody = formBuilder.build();
            builder.post(formBody);
        }
        Request request = builder.url(url).build();
        return sendRequest(request);
    }

    private static String getTaskInfo() {
        String url = "https://xxx.xxx.net/api/v2/task_info/";
        Map<String, Object> params = Maps.newHashMap();
        params.put("project_id", "111");
        params.put("task_create_time", "2018-10-17 11:00,2018-10-17 23:30");

        Map<String, String> headers = getHeaderMap();
        String result = get(url, params, headers);
        logger.info("resutl:{}", result);
        return result;
    }

    private static String createTask() {
        String url = "https://xxx.xxx.net/api/v2/create_task/";
        String objectId = RandomStringUtils.random(16, false, true);
        String randStr = RandomStringUtils.random(20, true, true);
        Map<String, String> objectData = ImmutableMap.of("content", randStr, "title", "Test For Demo");

        Map<String, Object> params = Maps.newHashMap();
        params.put("project_id", "111");
        params.put("force_create", "1");
        params.put("object_id", objectId);
        params.put("object_data", JSON.toJSONString(objectData));

        Map<String, String> headers = getHeaderMap();
        String result = post(url, params, headers);
        logger.info("resutl:{}", result);
        return result;
    }

    private static String auth() {
        String url = "https://xxx.xxx.net/apis/test/v1/auth/";
        Map<String, String> headers = getHeaderMap();
        String result = post(url, null, headers);
        logger.info("resutl:{}", result);
        return result;
    }

    public static void main(String[] args) {
        getTaskInfo();
        createTask();
        auth();
    }
}
