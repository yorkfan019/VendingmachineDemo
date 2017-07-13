package com.ajb.vendingmachine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajb.vendingmachine.adapter.GalleryAdapter;
import com.ajb.vendingmachine.event.MessageEvent;
import com.ajb.vendingmachine.http.ExceptionHandle;
import com.ajb.vendingmachine.loader.DataLoader;
import com.ajb.vendingmachine.model.Good;
import com.ajb.vendingmachine.model.PayInfo;
import com.ajb.vendingmachine.model.PayNotify;
import com.ajb.vendingmachine.service.MQTTService;
import com.ajb.vendingmachine.ui.AlertDialog;
import com.ajb.vendingmachine.ui.QRcodeAlertDialog;
import com.ajb.vendingmachine.util.GlideImageLoader;
import com.ajb.vendingmachine.util.NetworkUtils;
import com.ajb.vendingmachine.util.qrcode.EncodingHandler;
import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context context;
    //屏幕宽高
    private int windowWidth;
    private int windowHeight;
    private Bitmap alipayLogo;
    private Bitmap weChatLogo;

    //UI
    private Bitmap weChatBitmap;
    private Bitmap alipayBitmap;
    private ImageView detailIv;
    private QRcodeAlertDialog qRcodeAlertDialog;
    private AlertDialog alertDialog;


    private List<Integer> mDatas;
    private List<Integer> mBigDatas;

    HandlerThread handlerThread;
    Handler mHandler;

    private DataLoader mDataLoader;
    private Good mGood = new Good();
    private PayInfo mPayInfo = new PayInfo();
    private PayNotify mPayNotify = new PayNotify();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;
        //获取屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        windowWidth = metric.widthPixels;     // 屏幕宽度（像素）
        windowHeight = metric.heightPixels;   // 屏幕高度（像素）
        alipayLogo = BitmapFactory.decodeResource(getResources(),R.drawable.alipay_icon512);
        weChatLogo = BitmapFactory.decodeResource(getResources(),R.drawable.wechat_icon);

        mDataLoader = new DataLoader();
        //初始化banner
        initBanner();
        //初始化底部滑动栏
        initGallery();
        handlerThread = new HandlerThread("CreateQRcode");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        //连接队列服务器
        EventBus.getDefault().register(this);
        startService(new Intent(this, MQTTService.class));

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,MQTTService.class));
        super.onDestroy();
        dismissAlertDialog();
        dismissQRcodeAlertDialog();
        handlerThread.quit();
    }
    private void dismissAlertDialog() {
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    private void dismissQRcodeAlertDialog() {
        if(qRcodeAlertDialog != null && qRcodeAlertDialog.isShowing()) {
            qRcodeAlertDialog.dismiss();
            qRcodeAlertDialog = null;
        }
    }

    private void initBanner() {
        Banner banner = (Banner) findViewById(R.id.banner);
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

    private void initGallery() {
        initRecyclerViewData();
        //得到控件
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        detailIv = (ImageView) findViewById(R.id.iv_detail);
        detailIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpRequest();
            }
        });
//        detailIv.setImageResource(mBigDatas.get(0));
        Glide.with(context).load(mBigDatas.get(0)).fitCenter().into(detailIv);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        int mRecyclerViewWidth =  windowWidth;
        //设置适配器
        GalleryAdapter mAdapter = new GalleryAdapter(context, mDatas,mRecyclerViewWidth);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                detailIv.setImageResource(mBigDatas.get(position));
                Glide.with(context).load(mBigDatas.get(position)).fitCenter().into(detailIv);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRecyclerViewData() {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.mipmap.good1_s,
                R.mipmap.good2_s,
                R.mipmap.good3_s,
                R.mipmap.good4_s,
                R.mipmap.good5_s));

        mBigDatas = new ArrayList<Integer>(Arrays.asList(R.mipmap.good1,
                R.mipmap.good2,
                R.mipmap.good3,
                R.mipmap.good4,
                R.mipmap.good5));
    }

    /**
     * 运行在主线程
     * 订阅mqtt返回信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String string = event.getString();
        Log.d(TAG,"event.getString() = " + string);
        if("".equals(string)){
            String topic = event.getTopic();
            MqttMessage mqttMessage = event.getMqttMessage();
            String s = new String(mqttMessage.getPayload());
            topic=topic+" : "+s;
            Log.d(TAG,"mqttMessage.getPayload() = " + s);
            Log.d(TAG,"mqttMessage.toString() = " + mqttMessage.toString());
            try{
                JSONObject jsonObject =  new JSONObject(s);
                mPayNotify.setOutTradeNo(jsonObject.getString("outTradeNo"));
                mPayNotify.setPayResult(jsonObject.getString("payResult"));
                mPayNotify.setDatetime(jsonObject.getString("datetime"));
                topic=topic+" : "+mPayNotify.toString();
            }catch (JSONException e) {
                e.printStackTrace();
            }

            //根据返回结果弹出对应提示框
            dismissAlertDialog();
            dismissQRcodeAlertDialog();
            if(mPayNotify.getPayResult().equals("success")
                    && mPayNotify.getOutTradeNo().equals(mPayInfo.getOutTradeNo())) {

                alertDialog = new AlertDialog(context,getResources().getString(R.string.pay_success),
                        getResources().getString(R.string.success_content),
                        windowWidth,
                        windowHeight);
            } else {

                alertDialog = new AlertDialog(context,getResources().getString(R.string.pay_fail),
                        getResources().getString(R.string.fail_content),windowWidth,windowHeight);
            }
            alertDialog.show();
            Toast.makeText(MainActivity.this, topic, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 网络请求相关
     */
    private void httpRequest() {

        mGood.setGoodsName("可口可乐");
        mGood.setGoodsNum(1);
        mGood.setGoodsId("1");
        mGood.setGoodsPrice(1);
        mGood.setPassbackParam("pass");
        if(NetworkUtils.isConnected() && MQTTService.getMqttAndroidClientInstance().isConnected()) {

            //请求支付信息
            mDataLoader.getPayInfo(mGood).subscribe(new Action1<PayInfo>() {
                @Override
                public void call(PayInfo payInfo) {
                    mPayInfo = payInfo;
                    Log.e(TAG, "mPayInfo="+mPayInfo.toString());
                    setQRCode(mPayInfo.getWxpayCodeUrl(),mPayInfo.getWxpayCodeUrl());
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e(TAG,"error message:"+throwable.getMessage());
                    if(throwable instanceof Exception){
                        //访问获得对应的Exception
                        ExceptionHandle.ResponeThrowable responeThrowable = ExceptionHandle.handleException(throwable);
                        Toast.makeText(MainActivity.this, responeThrowable.message, Toast.LENGTH_SHORT).show();
                    }else {
                        //将Throwable 和 未知错误的status code返回
                        Toast.makeText(MainActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "当前网络无连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void setQRCode(final String weChatUrl,final String alipayUrl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int widthAndHeight = (int) (windowWidth*0.3);
                    weChatBitmap = EncodingHandler.createQRCode(weChatUrl,widthAndHeight,weChatLogo);
                    alipayBitmap = EncodingHandler.createQRCode(alipayUrl,widthAndHeight,alipayLogo);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissQRcodeAlertDialog();
                        qRcodeAlertDialog = new QRcodeAlertDialog(context,
                                mGood.getGoodsPrice(),
                                weChatBitmap,
                                alipayBitmap,
                                windowWidth,
                                windowHeight);
                        qRcodeAlertDialog.show();
                    }
                });
            }
        });
    }

}
