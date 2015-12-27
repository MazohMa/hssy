package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.Price;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class EditChargeFreeActivity extends BaseActivity implements
		OnClickListener {

	private ImageButton btn_left;
	private ImageButton btn_right;
	private TextView tv_title;
	private EditText et_charge_free;
	private String price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		price = intent.getStringExtra("price");
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.edit_chargefree_layout, null);
		setContentView(view);
		hideSoftInput(view);
		et_charge_free = (EditText) findViewById(R.id.et_charge_free);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		tv_title = (TextView) findViewById(R.id.tv_center);
		btn_right.setImageResource(R.drawable.ok_bt);
		tv_title.setText(R.string.charge_free);
		et_charge_free.setText(price);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			String price = et_charge_free.getText().toString().trim();
			if (price.equals("")) {
				ToastUtil.show(this, "充电费服务费不能为空");
				return;
			}
			final LoadingDialog dialog = new LoadingDialog(this,R.string.waiting);
			dialog.show();
			WebAPIManager.getInstance().setPrice(price, new WebResponseHandler<Price>() {

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					ToastUtil.show(EditChargeFreeActivity.this, e.getMessage());
				}

				@Override
				public void onFailure(WebResponse<Price> response) {
					super.onFailure(response);
					ToastUtil.show(EditChargeFreeActivity.this,response.getMessage());
				}

				@Override
				public void onSuccess(WebResponse<Price> response) {
					super.onSuccess(response);

					Intent intentRight = new Intent();
					intentRight.putExtra("et_charge_free", et_charge_free.getText()
							.toString().trim());
					setResult(Activity.RESULT_OK, intentRight);
					finish();
				}

				@Override
				public void onFinish() {
					super.onFinish();
					dialog.dismiss();
				}
			});
			break;
		}
	}

}
