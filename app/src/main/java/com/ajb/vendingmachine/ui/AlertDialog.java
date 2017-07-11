package com.ajb.vendingmachine.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ajb.vendingmachine.R;

/**
 * Created by fanyufeng on 2017-7-7.
 */

public class AlertDialog extends Dialog implements View.OnClickListener {

    @SuppressWarnings("unused")
    private Context context;
    private TextView tv_title;
    private TextView tv_content;
    private Button btn_close;
    private String title;
    private String content;
    private int windowWidth;
    private int windowHeight;
    private DialogCountDown mCountDown;

    public AlertDialog(Context context, String title, String content) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.content = content;
    }

    public AlertDialog(Context context, String title, String content, int windowWidth, int windowHeight) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.content = content;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (windowWidth * 0.8); // 宽度设置为屏幕的一定比例大小
        params.height = (int) (windowHeight * 0.4); // 宽度设置为屏幕的一定比例大小
        params.gravity = Gravity.TOP;
        params.y = (windowHeight - params.height)/2;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(true);
        tv_title = (TextView) findViewById(R.id.tv_dialog_title);
        tv_content = (TextView) findViewById(R.id.tv_dialog_content);
        btn_close = (Button) findViewById(R.id.btn_dialog_close);
        tv_title.setText(title);
        tv_content.setText(content);
        btn_close.setOnClickListener(this);
        if(mCountDown != null) {
            mCountDown.cancel();
            mCountDown = null;
        }
        mCountDown = new DialogCountDown(60000,1000);
        mCountDown.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mCountDown != null) {
            mCountDown.cancel();
            mCountDown=null;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() ==  R.id.btn_dialog_close) {
            dismiss();
        }
    }

    private class  DialogCountDown extends CountDownTimer {

        public DialogCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            String btn_str = context.getResources().getString(R.string.close_count);
            btn_close.setText(String.format(btn_str, l/1000));
        }

        @Override
        public void onFinish() {
            dismiss();
        }
    }
}
