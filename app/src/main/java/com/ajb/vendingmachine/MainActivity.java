package com.ajb.vendingmachine;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajb.vendingmachine.adapter.GalleryAdapter;
import com.ajb.vendingmachine.util.GlideImageLoader;
import com.ajb.vendingmachine.util.qrcode.EncodingHandler;
import com.google.zxing.WriterException;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context context;
    Banner banner;
    private Bitmap qRCodeBitmap;
    private ImageView qRcodeIv;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initBanner();
        setQRCode();
        initGallery();
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
                Toast.makeText(MainActivity.this, position+"", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRecyclerViewDatas() {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.mipmap.b1,
                R.mipmap.b2,
                R.mipmap.b3,
                R.mipmap.b3,R.mipmap.b3,R.mipmap.b3,R.mipmap.b3,R.mipmap.b3));
    }
}
