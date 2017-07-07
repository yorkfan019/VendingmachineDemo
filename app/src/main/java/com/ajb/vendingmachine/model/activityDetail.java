package com.ajb.vendingmachine.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by fanyufeng on 2017-7-7.
 */

public class activityDetail implements Serializable{
    private int activityId;
    private String title;
    private String postBy;
    private String content;
    private int numLimit;
    private String entryDateFrom;
    private String entryDateTo;
    private String[] images;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumLimit() {
        return numLimit;
    }

    public void setNumLimit(int numLimit) {
        this.numLimit = numLimit;
    }

    public String getEntryDateFrom() {
        return entryDateFrom;
    }

    public void setEntryDateFrom(String entryDateFrom) {
        this.entryDateFrom = entryDateFrom;
    }

    public String getEntryDateTo() {
        return entryDateTo;
    }

    public void setEntryDateTo(String entryDateTo) {
        this.entryDateTo = entryDateTo;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "activityDetail{" +
                "activityId=" + activityId +
                ", title='" + title + '\'' +
                ", postBy='" + postBy + '\'' +
                ", content='" + content + '\'' +
                ", numLimit=" + numLimit +
                ", entryDateFrom='" + entryDateFrom + '\'' +
                ", entryDateTo='" + entryDateTo + '\'' +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
