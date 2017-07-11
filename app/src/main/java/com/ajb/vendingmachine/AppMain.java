package com.ajb.vendingmachine;

import com.ajb.vendingmachine.base.BaseApplication;
import com.ajb.vendingmachine.util.CrashUtils;
import com.ajb.vendingmachine.util.Utils;

/**
 * Created by fanyufeng on 2017-7-11.
 */

public class AppMain extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        initCrash();
    }

    private void initCrash() {
        CrashUtils.init();
    }
}
