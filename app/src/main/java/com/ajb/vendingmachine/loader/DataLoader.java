package com.ajb.vendingmachine.loader;


import com.ajb.vendingmachine.http.BaseResponse;
import com.ajb.vendingmachine.http.ObjectLoader;
import com.ajb.vendingmachine.http.PayLoad;
import com.ajb.vendingmachine.http.RetrofitServiceManager;
import com.ajb.vendingmachine.model.Good;
import com.ajb.vendingmachine.model.PayInfo;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public class DataLoader extends ObjectLoader {
    private PayInfoService mPayInfoService;

    public DataLoader() {
        mPayInfoService = RetrofitServiceManager.getInstance().create(PayInfoService.class);
    }

    public Observable<PayInfo> getPayInfo(Good good) {
        return observe(mPayInfoService.getPayInfo(good)).map(new PayLoad<PayInfo>());
    }

    public interface PayInfoService{

        @POST("vem/trade/precreate")
        Observable<BaseResponse<PayInfo>> getPayInfo(
                @Body Good good);
    }

}
