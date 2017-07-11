package com.ajb.vendingmachine.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajb.vendingmachine.R;

import java.text.DecimalFormat;

/**
 * Created by fanyufeng on 2017-7-7.
 */

public class QRcodeAlertDialog extends Dialog implements View.OnClickListener {

    /**
     * 自定义Dialog监听器
     */
    public interface OnDialogButtonClickListener {

        /**点击按钮事件的回调方法
         * @param requestCode 传入的用于区分某种情况下的showDialog
         * @param isPositive
         */
        void onDialogButtonClick(int requestCode, boolean isPositive);
    }

    @SuppressWarnings("unused")
    private Context context;
    private int price;
    private Bitmap weChatBitmap;
    private Bitmap alipayBitmap;
    private TextView tv_price;
    private ImageView wechatIv;
    private ImageView alipayIv;
    private Button btn_close;
    private OnDialogButtonClickListener listener;
    private int windowWidth;
    private int windowHeight;
    private DialogCountDown mCountDown;
    private TextView tv_timeCount;

    /**
     * 带监听器参数的构造函数
     */
    public QRcodeAlertDialog(Context context, int price, Bitmap weChatBitmap,Bitmap alipayBitmap,OnDialogButtonClickListener listener) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.price = price;
        this.alipayBitmap = alipayBitmap;
        this.weChatBitmap = weChatBitmap;
        this.listener = listener;
    }

    public QRcodeAlertDialog(Context context, int price, Bitmap weChatBitmap,Bitmap alipayBitmap,
                            int windowWidth, int windowHeight) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.price = price;
        this.alipayBitmap = alipayBitmap;
        this.weChatBitmap = weChatBitmap;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    public QRcodeAlertDialog(Context context, int price, Bitmap weChatBitmap,Bitmap alipayBitmap,
                             OnDialogButtonClickListener listener,int windowWidth, int windowHeight) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.price = price;
        this.alipayBitmap = alipayBitmap;
        this.weChatBitmap = weChatBitmap;
        this.listener = listener;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_qrcode);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (windowWidth * 0.8); // 宽度设置为屏幕的一定比例大小
        params.height = (int) (windowHeight * 0.6); // 宽度设置为屏幕的一定比例大小
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(true);
        tv_price = (TextView) findViewById(R.id.tv_price);
        wechatIv = (ImageView) findViewById(R.id.iv_wechat);
        alipayIv = (ImageView) findViewById(R.id.iv_alipay);
        tv_timeCount = (TextView) findViewById(R.id.tv_pay_time_count);
        ViewGroup.LayoutParams we_params = wechatIv.getLayoutParams();
        we_params.height = (int) (windowWidth*0.3);
        we_params.width = (int) (windowWidth*0.3);
        wechatIv.setLayoutParams(we_params);
        ViewGroup.LayoutParams al_params = wechatIv.getLayoutParams();
        al_params.height = (int) (windowWidth*0.3);
        al_params.width = (int) (windowWidth*0.3);
        alipayIv.setLayoutParams(al_params);
        btn_close = (Button) findViewById(R.id.btn_qrcode_close);
        tv_price.setText("￥"+fenToYuan(price));
        wechatIv.setImageBitmap(weChatBitmap);
        alipayIv.setImageBitmap(alipayBitmap);
        btn_close.setOnClickListener(this);
        wechatIv.setOnClickListener(this);
        alipayIv.setOnClickListener(this);
        mCountDown = new DialogCountDown(90000,1000);
        mCountDown.start();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() ==  R.id.btn_qrcode_close) {
            dismiss();
        }
        if(view.getId() == R.id.iv_alipay) {
            listener.onDialogButtonClick(1,false);
        }
        if(view.getId() == R.id.iv_wechat) {
            listener.onDialogButtonClick(1,true);
        }
    }

    private String fenToYuan(int fen) {
        double yuan = (double)fen/100;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        return df.format(yuan);//返回的是String类型的
    }

    private class  DialogCountDown extends CountDownTimer {

        public DialogCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            String text_str = context.getResources().getString(R.string.pay_in_90s);
            tv_timeCount.setText(String.format(text_str, l/1000));
        }

        @Override
        public void onFinish() {
            dismiss();
        }
    }
}
