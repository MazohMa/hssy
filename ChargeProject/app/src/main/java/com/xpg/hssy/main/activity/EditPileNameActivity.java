package com.xpg.hssy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class EditPileNameActivity extends BaseActivity implements
		OnClickListener {

	private ImageButton btn_left;
	private ImageButton btn_right;
	private TextView tv_title;
	private EditText et_pilename;
	private String pilename;
	private Pile pile;
	private SharedPreferences sp;
    private LoadingDialog loadingDialog = null ;
	private boolean isLogin() {
		if (sp == null) {
			sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean("isLogin", false);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		pilename = intent.getStringExtra("pileName");
		pile = (Pile) intent.getSerializableExtra("pile");
	}
	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.edit_pile_name_layout, null);
		setContentView(view);
		hideSoftInput(view);
		et_pilename = (EditText) findViewById(R.id.et_pilename);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton)findViewById(R.id.btn_right);
		tv_title = (TextView) findViewById(R.id.tv_center);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setImageResource(R.drawable.ok_icon2);
		tv_title.setText("电桩名称");
		et_pilename.setText(pilename);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
			break;
			case R.id.btn_right:
			modifyName();
			break;

		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_right_in,
				R.anim.slide_left_out);
	}

	private void modifyName() {
		if (TextUtils.isEmpty(et_pilename.getText().toString().trim())) {
			ToastUtil.show(this, "桩名字不能为空");
			return;
		} else if (et_pilename.getText().toString().length() < 3 || et_pilename.getText().toString().length() > 16) {
			ToastUtil.show(this, "请输入电桩名称，3-16字符");
			return;
		}
		loadingDialog = new LoadingDialog(self,R.string.please_wait) ;
		loadingDialog.showDialog();
		pile.setPileName(et_pilename.getText().toString().trim());
		WebAPIManager.getInstance().modifyPile(pile,
				new WebResponseHandler(this) {

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(self, e);
					}

					@Override
					public void onFailure(WebResponse response) {
						super.onFailure(response);
						TipsUtil.showTips(self, response);
					}

					@Override
					public void onSuccess(WebResponse response) {
						super.onSuccess(response);
						DbHelper.getInstance(self).insertPile(pile);
						ToastUtil.show(self,R.string.fix_success_tip);
						finish();
						overridePendingTransition(R.anim.slide_left_in,
								R.anim.slide_right_out);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						loadingDialog.dismiss();
					}
				});
	}
}
