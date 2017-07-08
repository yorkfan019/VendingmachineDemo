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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajb.vendingmachine.adapter.GalleryAdapter;
import com.ajb.vendingmachine.callback.ConnectCallBackHandler;
import com.ajb.vendingmachine.callback.MqttCallbackHandler;
import com.ajb.vendingmachine.callback.SubcribeCallBackHandler;
import com.ajb.vendingmachine.event.MessageEvent;
import com.ajb.vendingmachine.http.DataLoader;
import com.ajb.vendingmachine.http.Fault;
import com.ajb.vendingmachine.model.activityDetail;
import com.ajb.vendingmachine.ui.AlertDialog;
import com.ajb.vendingmachine.ui.QRcodeAlertDialog;
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
    private int windowWidth;
    private int windowHeight;
    Banner banner;
    private Bitmap weChatBitmap;
    private Bitmap alipayBitmap;
    private ImageView detailIv;

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
            "http://www.qq.com/"));


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
    private DataLoader mDataLoader;
    private int iv_position = 0;
    private QRcodeAlertDialog.OnDialogButtonClickListener dialogButtonClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        windowWidth = metric.widthPixels;     // 屏幕宽度（像素）
        windowHeight = metric.heightPixels;   // 屏幕高度（像素）

        mDataLoader = new DataLoader();
        initBanner();
        initGallery();
        handlerThread = new HandlerThread("CreateQRcode");
        handlerThread.start();
        mHandler = new Handler();
        startConnectActiveMq(clientID,serverIP,port);
        dialogButtonClickListener = new QRcodeAlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                AlertDialog dialog;
                if(isPositive) {
                    dialog = new AlertDialog(context,"付款成功","出货中，请在取货口出货",windowWidth,windowHeight);
                } else {
                    dialog = new AlertDialog(context,""
                            ,"很抱歉，商品出货失败！\n"+ "请联系客服人员，客服电话：020*******",windowWidth,windowHeight);
                }
                dialog.show();
            }
        };
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
        list.add(R.mipmap.banner1);
        list.add(R.mipmap.banner2);
        list.add(R.mipmap.banner3);

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

    private void setQRCode(final String weChatUrl,final String alipayUrl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int widthAndHeight = (int) (windowWidth*0.3);
                    weChatBitmap = EncodingHandler.createQRCode(weChatUrl,widthAndHeight);
                    alipayBitmap = EncodingHandler.createQRCode(alipayUrl,widthAndHeight);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       new QRcodeAlertDialog(context,7,weChatBitmap,alipayBitmap,
                               dialogButtonClickListener,windowWidth,windowHeight).show();
                    }
                });
            }
        });

    }

    private void initGallery() {
        initRecyclerViewDatas();
        //得到控件
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        detailIv = (ImageView) findViewById(R.id.iv_detail);
        detailIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setQRCode(urlDatas.get(iv_position),urlDatas.get(iv_position+1));
            }
        });
        detailIv.setImageResource(mDatas.get(0));
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        int mRecyclerViewWidth =  windowWidth;
        //设置适配器
        mAdapter = new GalleryAdapter(context, mDatas,mRecyclerViewWidth);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                detailIv.setImageResource(mDatas.get(position));
                iv_position = position;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRecyclerViewDatas() {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.mipmap.p1,
                R.mipmap.p2,
                R.mipmap.p3,
                R.mipmap.p4,
                R.mipmap.p5,
                R.mipmap.p1,
                R.mipmap.p2,
                R.mipmap.p3));
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



    /**
     * 网络请求相关
     */
    private void httpRequest() {

        int activityId = 2;
        mDataLoader.getActivityDetail(activityId).subscribe(new Action1<activityDetail>() {
            @Override
            public void call(activityDetail activityDetail) {
                Log.e(TAG, activityDetail.toString());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG,"error message:"+throwable.getMessage());
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
