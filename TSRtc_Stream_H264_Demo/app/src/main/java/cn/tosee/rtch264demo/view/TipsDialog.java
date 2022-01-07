package cn.tosee.rtch264demo.view;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.tosee.rtch264demo.R;

public class TipsDialog extends Dialog {

    private Button yes;
    private TextView titleTv;
    private TextView messageTv;
    private ImageView id_close;
    private String titleStr;
    private String messageStr;

    private String yesStr;

    private onYesOnclickListener yesOnclickListener;



    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }


    public TipsDialog(Context context) {
        super(context, R.style.DialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_dialog_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        initView();
        initData();
        initEvent();

    }


    private void initEvent() {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });

        id_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

    }


    private void initData() {
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        if (yesStr != null) {
            yes.setText(yesStr);
        }
    }


    private void initView() {
        yes = (Button) findViewById(R.id.btn_ok);
        titleTv = (TextView) findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
        id_close = (ImageView) findViewById(R.id.id_close);
    }


    public void setTitle(String title) {
        titleStr = title;
    }


    public void setMessage(String message) {
        messageStr = message;
    }
    public void showCloseButton(){
        id_close.setVisibility(View.VISIBLE);
    }



    public interface onYesOnclickListener {
        void onYesClick();
    }

}
