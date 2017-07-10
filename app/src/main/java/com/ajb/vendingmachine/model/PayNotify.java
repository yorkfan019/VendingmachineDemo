package com.ajb.vendingmachine.model;

import java.io.Serializable;

/**
 * Created by fanyufeng on 2017-7-5.
 */

public class PayNotify implements Serializable {

    private String outTradeNo;
    private String payResult;
    private String datetime;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "PayNotify{" +
                "outTradeNo='" + outTradeNo + '\'' +
                ", payResult='" + payResult + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
