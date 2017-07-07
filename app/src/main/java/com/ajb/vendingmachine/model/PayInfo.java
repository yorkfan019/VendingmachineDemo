package com.ajb.vendingmachine.model;

import java.io.Serializable;

/**
 * Created by fanyufeng on 2017-7-5.
 */

public class PayInfo implements Serializable {

    private String wxpayCodeUrl;
    private String alipayCodeUrl;
    private String outTradeNo;
    private String passbackParam;
    private String datetime;

    public String getWxpayCodeUrl() {
        return wxpayCodeUrl;
    }

    public void setWxpayCodeUrl(String wxpayCodeUrl) {
        this.wxpayCodeUrl = wxpayCodeUrl;
    }

    public String getAlipayCodeUrl() {
        return alipayCodeUrl;
    }

    public void setAlipayCodeUrl(String alipayCodeUrl) {
        this.alipayCodeUrl = alipayCodeUrl;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getPassbackParam() {
        return passbackParam;
    }

    public void setPassbackParam(String passbackParam) {
        this.passbackParam = passbackParam;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
