package com.ajb.vendingmachine.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public class noticeList implements Serializable {

    private int noticeId;
    private String receiver;
    private String title;
    private String publisher;
    private String content;
    private int publishType;
    private String[] images;

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPublishType() {
        return publishType;
    }

    public void setPublishType(int publishType) {
        this.publishType = publishType;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "noticeList{" +
                "noticeId=" + noticeId +
                ", receiver='" + receiver + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", content='" + content + '\'' +
                ", publishType=" + publishType +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
