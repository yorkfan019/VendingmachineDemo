package com.ajb.vendingmachine;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajb.vendingmachine.adapter.GalleryAdapter;
import com.ajb.vendingmachine.callback.ConnectCallBackHandler;
import com.ajb.vendingmachine.callback.MqttCallbackHandler;
import com.ajb.vendingmachine.callback.SubcribeCallBackHandler;
import com.ajb.vendingmachine.event.MessageEvent;
import com.ajb.vendingmachine.http.DataLoader;
import com.ajb.vendingmachine.http.Fault;
import com.ajb.vendingmachine.model.notice;
import com.ajb.vendingmachine.util.GlideImageLoader;
import com.ajb.vendingmachine.util.qrcode.EncodingHandler;
import com.google.zxing.WriterException;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context context;
    Banner banner;
    private Bitmap qRCodeBitmap;
    private ImageView qRcodeIv;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;
    private List<String> urlDatas = new ArrayList<String>(Arrays.asList("weixin://.dfadfadfadsf.,dfadf/",
            "alipay://dfdafadf.dfadfadfadf",
            "http://www.tabobao.com/",
            "https://www.baidu.com/",
            "http://www.qq.com/",
            "http://www.tabobao.com/",
            "https://www.baidu.com/",
            "http://www.qq.com/",
            "http://www.tabobao.com/"));


    HandlerThread handlerThread;
    Handler mHandler;

    Handler subscribeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           switch(msg.what) {
               case 1:
                   subscribe("anjubao/vem/topic/pay/notify");
                   break;
               case 0:
                   break;
               default:
                   break;
           }
        }
    };

    private String clientID = "android_vem_pay";
    private String serverIP = "192.168.42.19";
    private String port = "1883";
//    private String serverIP = "192.168.200.88";
    private static MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initBanner();
        initGallery();
        handlerThread = new HandlerThread("CreateQRcode");
        handlerThread.start();
        mHandler = new Handler();
        startConnectActiveMq(clientID,serverIP,port);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        handlerThread.quit();
    }

    private void initBanner() {
        banner = (Banner) findViewById(R.id.banner);
        List<Integer> list=new ArrayList<>();
        list.add(R.mipmap.b1);
        list.add(R.mipmap.b2);
        list.add(R.mipmap.b3);

        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(list);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    private void setQRCode(final String url) {
        qRcodeIv = (ImageView) findViewById(R.id.ivQRCode);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int widthAndHeight = (int) (2 * getResources().getDimension(R.dimen.qrcode_size));
                    qRCodeBitmap = EncodingHandler.createQRCode(url,widthAndHeight);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qRcodeIv.setImageBitmap(qRCodeBitmap);
                    }
                });
            }
        });

    }

    private void initGallery() {
        initRecyclerViewDatas();
        //得到控件
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new GalleryAdapter(context, mDatas);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.id_index_gallery_item_text);
                String info = textView.getText().toString();
                Toast.makeText(MainActivity.this, position+":"+info, Toast.LENGTH_SHORT).show();
                setQRCode(urlDatas.get(position));
                httpRequest();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRecyclerViewDatas() {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.mipmap.b1,
                R.mipmap.b2,
                R.mipmap.b3,
                R.mipmap.b1,
                R.mipmap.b2,
                R.mipmap.b3,
                R.mipmap.b1,
                R.mipmap.b2,
                R.mipmap.b3));
    }


    /**
     * 连接activeMQ服务器
     */
    private void startConnectActiveMq(String clientID, String serverIP, String port) {
        //服务器地址
        String uri = "tcp://";
        uri = uri+serverIP+":"+port;
        Log.d(TAG,uri+"  "+clientID);
        /**
         * 连接的选项
         */
        MqttConnectOptions conOpt = new MqttConnectOptions();
        /**设计连接超时时间*/
        conOpt.setConnectionTimeout(3000);
        /**设计心跳间隔时间300秒*/
        conOpt.setKeepAliveInterval(300);
        /**
         * 创建连接对象
         */
        client = new MqttAndroidClient(context,uri, clientID);
        /**
         * 连接后设计一个回调
         */
        client.setCallback(new MqttCallbackHandler(context, clientID));
        /**
         * 开始连接服务器，参数：ConnectionOptions,  IMqttActionListener
         */
        try {
            client.connect(conOpt, null, new ConnectCallBackHandler(context,subscribeHandler));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取MqttAndroidClient实例
     * @return
     */
    public static MqttAndroidClient getMqttAndroidClientInstace(){
        if(client!=null)
            return  client;
        return null;
    }


    /**
     * 订阅topic
     */
    public void subscribe(String topic) {
        MqttAndroidClient client = getMqttAndroidClientInstace();
        if(client != null) {
            try {
                client.subscribe(topic,0,null,new SubcribeCallBackHandler(context));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("subscribe","MqttAndroidClient==null");
        }
    }

    /**
     * 运行在主线程
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) throws JSONException {
        String string = event.getString();
        if("".equals(string)){
            String topic = event.getTopic();
            MqttMessage mqttMessage = event.getMqttMessage();
            String s = new String(mqttMessage.getPayload());
            topic=topic+" : "+s;
//            JSONObject jsonObject =  new JSONObject(s);
//            String outTradeNo = jsonObject.getString("outTradeNo");
//            String payResult = jsonObject.getString("payResult");
//            String datetime = jsonObject.getString("datetime");
//            topic=topic+" : "+" outTradeNo: "+outTradeNo+" payResult:"+payResult+" datetime:"+datetime;
            Toast.makeText(MainActivity.this, topic, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
        }
    }


    private DataLoader mDataLoader = new DataLoader();
    /**
     * 网络请求相关
     */
    private void httpRequest() {
        int noticeId = 3;
        mDataLoader.getPayInfo(noticeId).subscribe(new Action1<notice>() {
            @Override
            public void call(notice notice) {
                Log.e(TAG,notice.toString());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG","error message:"+throwable.getMessage());
                if(throwable instanceof Fault){
                    Fault fault = (Fault) throwable;
                    if(fault.getErrorCode() == 404){
                        //错误处理
                    }else if(fault.getErrorCode() == 500){
                        //错误处理
                    }else if(fault.getErrorCode() == 501){
                        //错误处理
                    }
                }
            }
        });
    }

}
