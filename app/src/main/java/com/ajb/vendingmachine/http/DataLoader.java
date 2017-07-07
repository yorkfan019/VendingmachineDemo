package com.ajb.vendingmachine.http;


import com.ajb.vendingmachine.model.activityDetail;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public class DataLoader extends ObjectLoader {
    private PayInfoService mPayInfoService;

    public DataLoader() {
        mPayInfoService = RetrofitServiceManager.getInstance().create(PayInfoService.class);
    }

    public Observable<activityDetail> getActivityDetail(int activityId) {
        return observe(mPayInfoService.getActivityDetail(activityId)).map(new PayLoad<activityDetail>());
    }

    public interface PayInfoService{

        @GET("interaction/activity/detail")
        Observable<BaseResponse<activityDetail>> getActivityDetail(@Query("activityId") int activityId);
    }

}
