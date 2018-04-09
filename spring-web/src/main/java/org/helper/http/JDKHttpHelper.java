package org.helper.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDK提供的HttpURLConnection使用示例
 */
public class JDKHttpHelper {
    private static final Logger logger = LoggerFactory.getLogger(JDKHttpHelper.class);

    public static void main(String[] args) {
        String getStr = sendGet("https://www.baidu.com/");
        logger.info("{}", getStr);

        String postStr = sendPost("https://www.baidu.com/","user=hzz&pwd=123");
        logger.info("{}", postStr);
    }

    private static String sendGet(String url) {
        String responseBody = null;
        try {
            URL getUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) getUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String readLine;
                StringBuilder response = new StringBuilder();
                while (null != (readLine = bufferedReader.readLine())) {
                    response.append(readLine);
                }
                bufferedReader.close();
                responseBody = response.toString();
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            logger.error("http get request MalformedURLException error.", e);
        } catch (IOException e) {
            logger.error("http get request IOException error.", e);
        }
        return responseBody;
    }

    private static String sendPost(String url, String params) {
        String responseBody = null;
        try {
            URL postUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) postUrl.openConnection();

            // 设置属性方法
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(params.length()));
            //设置是否向httpUrlConnection输出内容,因为post请求参数在http正文中.
            urlConnection.setDoOutput(true);
            //设置是否获取的数据可以从缓存拿
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.connect();

            // 如果post包含参数,则写入参数.
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(params);
            outputStream.flush();
            outputStream.close();

            //获取并解析数据
            int respCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == respCode) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while (null != (inputLine = bufferedReader.readLine())) {
                    response.append(inputLine);
                }

                bufferedReader.close();
                responseBody = response.toString();
                urlConnection.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }
}
