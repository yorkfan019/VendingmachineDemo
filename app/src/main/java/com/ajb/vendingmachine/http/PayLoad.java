package com.ajb.vendingmachine.http;

import rx.functions.Func1;

/**
 * Created by fanyufeng on 2017-7-6.
 */

public class PayLoad<T> implements Func1<BaseResponse<T>,T> {
    @Override
    public T call(BaseResponse<T> tBaseResponse) {
        if(!tBaseResponse.isSuccess()){
            throw new Fault(tBaseResponse.result,tBaseResponse.message);
        }
        return tBaseResponse.data;
    }
}
