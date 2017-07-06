package com.ajb.vendingmachine.model;

import java.io.Serializable;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public class notice implements Serializable {
    private int count;
    private String result;
    private String message;
    private noticeList data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public noticeList getData() {
        return data;
    }

    public void setData(noticeList data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "notice{" +
                "count=" + count +
                ", result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
