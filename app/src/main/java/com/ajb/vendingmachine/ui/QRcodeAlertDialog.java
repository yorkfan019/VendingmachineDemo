package com.ajb.vendingmachine.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajb.vendingmachine.R;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_qrcode);
        setCanceledOnTouchOutside(true);
        tv_price = (TextView) findViewById(R.id.tv_price);
        wechatIv = (ImageView) findViewById(R.id.iv_wechat);
        alipayIv = (ImageView) findViewById(R.id.iv_alipay);
        btn_close = (Button) findViewById(R.id.btn_qrcode_close);
        tv_price.setText("￥"+price);
        wechatIv.setImageBitmap(weChatBitmap);
        alipayIv.setImageBitmap(alipayBitmap);
        btn_close.setOnClickListener(this);
        wechatIv.setOnClickListener(this);
        alipayIv.setOnClickListener(this);
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
}
