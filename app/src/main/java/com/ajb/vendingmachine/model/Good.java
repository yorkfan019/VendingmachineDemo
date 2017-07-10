package com.ajb.vendingmachine.model;

import java.io.Serializable;

/**
 * Created by fanyufeng on 2017-7-5.
 */

public class Good implements Serializable{

    private String goodsName;
    private int goodsNum;
    private String goodsId;
    private int goodsPrice;//单位：分
    private String passbackParam; // 回传参数

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(int goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getPassbackParam() {
        return passbackParam;
    }

    public void setPassbackParam(String passbackParam) {
        this.passbackParam = passbackParam;
    }
}
