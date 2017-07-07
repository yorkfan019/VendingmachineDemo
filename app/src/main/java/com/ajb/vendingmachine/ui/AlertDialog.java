package com.ajb.vendingmachine.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
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

    public AlertDialog(Context context, String title, String content) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.content = content;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        setCanceledOnTouchOutside(true);
        tv_title = (TextView) findViewById(R.id.tv_dialog_title);
        tv_content = (TextView) findViewById(R.id.tv_dialog_content);
        btn_close = (Button) findViewById(R.id.btn_dialog_close);
        tv_title.setText(title);
        tv_content.setText(content);
        btn_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() ==  R.id.btn_dialog_close) {
            dismiss();
        }
    }
}
