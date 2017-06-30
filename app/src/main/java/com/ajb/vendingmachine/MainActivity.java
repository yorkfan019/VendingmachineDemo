package com.ajb.vendingmachine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ajb.vendingmachine.util.GlideImageLoader;
import com.ajb.vendingmachine.util.qrcode.EncodingHandler;
import com.google.zxing.WriterException;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Banner banner;
    private Bitmap qRCodeBitmap;
    private ImageView qRcodeIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBanner();
        setQRCode();
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

    private void setQRCode() {
        qRcodeIv = (ImageView) findViewById(R.id.ivQRCode);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    qRCodeBitmap = EncodingHandler.createQRCode("http://baidu.com/",(int) (2 * getResources().getDimension(R.dimen.qrcode_size)));
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
        }).start();

    }
}
