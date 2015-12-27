package com.xpg.hssy.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.xpg.hssy.account.RegisterActivity2;
import com.xpg.hssychargingpole.R;

/**
 * Created by Gunter on 2015/11/3.
 */
public class LoginDialog extends BaseDialog{
    private static LoginDialog self;
    private Context context;
    private String phone;
    public static final String KEY_PHONE = "phone";

    public static LoginDialog getInstance(Context context) {
        return getInstance(context,null);
    }

    public static LoginDialog getInstance(Context context, String phone){
        if (self == null) {
            self = new LoginDialog(context,phone);
        } else {
            if (self.getContext() != context) {
                try {
                    self.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                self = new LoginDialog(context,phone);
            }
        }
        return self;
    }

    private LoginDialog(Context context,String phone) {
        super(context);
        this.context = context;
        this.phone = phone;
        init();
    }

    private void init(){
        setContentView(R.layout.water_blue_dialog_title);
        setCancelable(true);
        setContent("该手机号尚未注册，是否注册优易充？");
        setLeftBtnText("取消");
        setRightBtnText("立即注册");
        setLeftListener(this);
        setRightListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_ok:
                Intent intent = new Intent(context, RegisterActivity2.class);
                intent.putExtra(KEY_PHONE,phone);
                context.startActivity(intent);
                break;
        }
    }

}
