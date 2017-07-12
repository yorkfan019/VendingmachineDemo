package com.ajb.vendingmachine.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by fanyufeng on 2017-7-11.
 */

public class NetWorkStateReceiver extends BroadcastReceiver {

    public static final String TAG = "NetWorkStateReceiver";

    NetworkStateChange mNetworkStateChange;
    @Override
    public void onReceive(Context context, Intent intent) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Log.i(TAG,"WIFI已连接,移动数据已连接");
                mNetworkStateChange.checkMqttConnect();
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                Log.i(TAG,"WIFI已连接,移动数据已断开");
                mNetworkStateChange.checkMqttConnect();
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Log.i(TAG,"WIFI已断开,移动数据已连接");
                mNetworkStateChange.checkMqttConnect();
            } else {
                Log.i(TAG,"WIFI已断开,移动数据已断开");
            }

        } else {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            boolean state = false;
            //通过循环将网络信息逐个取出来
            for (int i=0; i < networks.length; i++){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if(networkInfo.isConnected()) {
                    state = true;
                }
            }
            Log.i(TAG,sb.toString());
            if(state) {
                mNetworkStateChange.checkMqttConnect();
            }
        }

    }

    public interface NetworkStateChange {
        void checkMqttConnect();
    }

    public void setNetworkStateListener(NetworkStateChange networkStateChange) {
        this.mNetworkStateChange = networkStateChange;
    }
}
