package com.ajb.vendingmachine.http;

/**
 * 网络请求结果 基类
 */

public class BaseResponse<T> {
    public int count;
    public int result;
    public String message;
    public T data;

    public boolean isSuccess() {
        return result == 0;
    }
}
