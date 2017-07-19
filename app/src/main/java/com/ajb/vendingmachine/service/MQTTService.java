package com.ajb.vendingmachine.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ajb.vendingmachine.event.MessageEvent;
import com.ajb.vendingmachine.http.ApiConfig;
import com.ajb.vendingmachine.receiver.NetWorkStateReceiver;
import com.ajb.vendingmachine.util.NetworkUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by fanyufeng on 2017-7-12.
 */

public class MQTTService extends Service{

    public static final String TAG = MQTTService.class.getSimpleName();

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private boolean isServiceOnDestroy = false;
    NetWorkStateReceiver netWorkStateReceiver;
    NetWorkStateReceiver.NetworkStateChange networkStateChange;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String ClientID = "android_"+ getUniqueId();
        init(ClientID,ApiConfig.ACTIVE_MQ_IP,ApiConfig.ACTIVE_MQ_PORT);
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver,filter);
        networkStateChange = new NetWorkStateReceiver.NetworkStateChange() {
            @Override
            public void checkMqttConnect() {
                doClientConnection();
            }
        };
        netWorkStateReceiver.setNetworkStateListener(networkStateChange);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service onDestroy");
        isServiceOnDestroy = true;
        unregisterReceiver(netWorkStateReceiver);
        if(client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    /**
     * 获取MqttAndroidClient实例
     * @return
     */
    public static MqttAndroidClient getMqttAndroidClientInstance(){
        if(client!=null)
            return  client;
        return null;
    }

    private void init(String clientID, String serverIP, String port) {
        /**
         * 服务器地址
         */
        String uri = "tcp://";
        uri = uri+serverIP+":"+port;
        Log.d(TAG,uri+"  "+clientID);
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        /**设计连接超时时间*/
        conOpt.setConnectionTimeout(60);
        /**设计心跳间隔时间60秒*/
        conOpt.setKeepAliveInterval(60);

        /**
         * 创建连接对象
         */
        client = new MqttAndroidClient(this,uri, clientID);
        /**
         * 连接后设计一个回调
         */
        client.setCallback(mqttCallbackHandler);

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientID + "\"}";
        String topic = ApiConfig.TOPIC;
        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                connectCallBackHandler.onFailure(null, e);
            }
        }
        if (doConnect && !isServiceOnDestroy) {
            doClientConnection();
        }

    }

    /** 连接MQTT服务器 */
    private void doClientConnection() {
        Log.i(TAG, "doClientConnection进行连接");

        if(NetworkUtils.isConnected()) {
            if ( !isServiceOnDestroy && !client.isConnected()) {
                try {
                    client.connect(conOpt, null, connectCallBackHandler);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.i(TAG, "当前网络无连接");
        }
    }

    // MQTT是否连接成功
    private IMqttActionListener connectCallBackHandler = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "connectCallBackHandler连接成功 ");
            try {
                // 订阅myTopic话题
                client.subscribe(ApiConfig.TOPIC,1,null,subscribeCallBackHandler);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            Log.i(TAG, "connectCallBackHandler连接失败");
            arg1.printStackTrace();
            // 连接失败，重连
            doClientConnection();
        }
    };

    //MQTT是否订阅成功
    private IMqttActionListener subscribeCallBackHandler = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "subscribeCallBackHandler订阅成功 ");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.i(TAG, "subscribeCallBackHandler订阅失败 ");

            if(exception != null) {
                exception.printStackTrace();
            }
            // 订阅失败，重连
            doClientConnection();
        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallbackHandler = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "messageArrived ");

            MessageEvent messageEvent = new MessageEvent(topic,message);
            String str1 = new String(message.getPayload());
            EventBus.getDefault().post(messageEvent);
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Log.i(TAG, "messageArrived:" + str1);
            Log.i(TAG, str2);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "deliveryComplete ");

        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "connectionLost ");
            // 失去连接，重连
            doClientConnection();
        }
    };

    private String getUniqueId() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits

        return m_szDevIDShort;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
