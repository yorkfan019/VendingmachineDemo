package com.ajb.vendingmachine.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;


/**
 * Description :
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/10/25 0025
 */

public class ConnectCallBackHandler implements IMqttActionListener {

    private Context context;
    private Handler handler;

    public ConnectCallBackHandler(Context context, Handler handler) {
        this.context=context;
        this.handler = handler;
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        Log.d("ConnectCallBackHandler","ConnectCallBackHandler/onSuccess");
        Toast.makeText(context,"连接成功", Toast.LENGTH_SHORT).show();
        Message msg = handler.obtainMessage();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Log.d("ConnectCallBackHandler","ConnectCallBackHandler/onFailure");
        Toast.makeText(context,"连接失败", Toast.LENGTH_SHORT).show();
        Message msg = handler.obtainMessage();
        msg.what = 0;
        handler.sendMessage(msg);
    }
}
