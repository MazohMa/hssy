package com.xpg.hssy.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.easy.manager.EasyActivityManager;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssychargingpole.R;

/**
 * 
 * @author Mazoh 用户注册--注册成功
 * 
 */
public class RegisterActivitySuccessfully extends BaseActivity implements
		OnClickListener {

	private Button btn_charge;
	private ImageButton btn_left;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.register_success, null);
		setContentView(view);
		hideSoftInput(view);
		setTitle(R.string.user_register);
		btn_charge = (Button) view.findViewById(R.id.btn_charge);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_left.setVisibility(View.INVISIBLE);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_charge.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_charge:
			EasyActivityManager.getInstance().finishAll();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

}
