package org.helper.common;


/**
 * 所有ajax请求返回类型,封装json结果
 * 通常我们跟前端交互的时候,对返回结果要进行一次封装,以告诉前端是否正确返回数据.
 * 可以定制化,比如加入status(状态码),total(返回数据数量)
 * 如果使用ResponseBody返回对象,转成json,里面为null的字段也会被放入json串.可以用JSON转换后返回string
 * @param <T> 返回的类型
 */
public class JsonView<T> {
    //返回成功失败标示
    private boolean success;
    //数据
    private T data;
    //错误码
    private String error;

    public JsonView(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public JsonView(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
