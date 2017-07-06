package com.ajb.vendingmachine.http;


import com.ajb.vendingmachine.model.notice;
import com.ajb.vendingmachine.model.noticeList;
import com.ajb.vendingmachine.model.payInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public interface PayInfoService {

    @GET("interaction/notice/detail")
    Observable<notice> getPayInfo(@Query("noticeId") int noticeId);
}
