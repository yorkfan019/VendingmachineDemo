package com.ajb.vendingmachine.http;


import com.ajb.vendingmachine.model.notice;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public class DataLoader extends ObjectLoader {
    private PayInfoService mPayInfoService;

    public DataLoader() {
        mPayInfoService = RetrofitServiceManager.getInstance().create(PayInfoService.class);
    }

    public Observable<notice> getPayInfo(int noticeId) {
        return observe(mPayInfoService.getPayInfo(noticeId))
                .map(new Func1<notice, notice>() {
                    @Override
                    public notice call(notice notice) {
                        return notice;
                    }
                });
    }

    public interface PayInfoService{
        @GET("interaction/notice/detail")
        Observable<notice> getPayInfo(@Query("noticeId") int noticeId);
    }
}
