package com.xpg.hssy.main.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.LoginInfo;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.TipsDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;
import com.xpg.hssychargingpole.shareapi.ShareApiManager;

/**
 * Created by Gunter on 2015/10/15.
 */
public class BindAccountActivity extends BaseActivity {

    private TextView tv_title;
    private LinearLayout ll_bindQQ,ll_bindWeChat;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        sp = getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
        initViews();
    }

    private void initViews(){
        //初始化标题栏
        tv_title = (TextView) findViewById(R.id.tv_center);
        tv_title.setText("账号绑定");
        findViewById(R.id.btn_right).setVisibility(View.INVISIBLE);

        ll_bindWeChat = (LinearLayout) findViewById(R.id.ll_btn_bind_wechat);
        ll_bindWeChat.setOnClickListener(this);
        ll_bindQQ = (LinearLayout) findViewById(R.id.ll_btn_bind_qq);
        ll_bindQQ.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        final WebResponseHandler<Object> handler = getResponseHandler();
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_btn_bind_wechat:
                ShareApiManager.wechatLogin(self, new ShareApiManager.Listener<LoginInfo>() {
                    @Override
                    public void onComplete(LoginInfo loginInfo) {
                        String thirdUserId = loginInfo.getUserId();
                        String thirdAccToken = loginInfo.getToken();
                        int thirdAccType = loginInfo.getUserType();
                        String userId = sp.getString("user_id",null);
                        if(userId != null){
                            WebAPIManager.getInstance().bindThirdAcc(thirdUserId, thirdAccToken, thirdAccType, userId, handler);
                        }
                    }
                });
                break;
            case R.id.ll_btn_bind_qq:
                ShareApiManager.qqLogin(this, new ShareApiManager.Listener<LoginInfo>() {
                    @Override
                    public void onComplete(LoginInfo loginInfo) {
                        String thirdUserId = loginInfo.getUserId();
                        String thirdAccToken = loginInfo.getToken();
                        int thirdAccType = loginInfo.getUserType();
                        String userId = sp.getString("user_id",null);
                        if(userId != null){
                            WebAPIManager.getInstance().bindThirdAcc(thirdUserId, thirdAccToken, thirdAccType, userId, handler);
                        }
                    }
                });
                break;
        }
    }

    private WebResponseHandler<Object> getResponseHandler(){
        final LoadingDialog dialog = new LoadingDialog(BindAccountActivity.this,R.string.waiting);
        WebResponseHandler<Object> handler = new WebResponseHandler<Object>() {
            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFailure(WebResponse<Object> response) {
                super.onFailure(response);
                dialog.dismiss();
                new TipsDialog(BindAccountActivity.this,response.getMessage()).show();
            }

            @Override
            public void onSuccess(WebResponse<Object> response) {
                super.onSuccess(response);
                dialog.dismiss();
                ToastUtil.show(BindAccountActivity.this, "绑定成功");
            }
        };
        return handler;
    }
}
