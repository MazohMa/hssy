package com.xpg.hssy.base;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.easy.activity.EasyActivity;
import com.easy.util.NetWorkUtil;
import com.easy.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.xpg.hssychargingpole.MyApplication;
import com.xpg.hssychargingpole.R;

public class BaseActivity extends EasyActivity implements OnClickListener {

	private TextView tv_center;

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		findTopView();
	}

	@Override
	public void setContentView(View contentView) {
		super.setContentView(contentView);
		findTopView();
	}

	private void findTopView() {
		View v = null;
		v = findViewById(R.id.btn_left);
		if (v != null) {
			v.setOnClickListener(this);
		}
		v = findViewById(R.id.btn_right);
		if (v != null) {
			v.setOnClickListener(this);
		}
		v = findViewById(R.id.tv_center);
		if (v != null) {
			tv_center = (TextView) v;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	protected void onExit() {
		super.onExit();
		MyApplication.getInstance().exit();
	}

	public void hideSoftInput(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {

			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard();
					return false;
				}

			});
		}

		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				View innerView = ((ViewGroup) view).getChildAt(i);

				hideSoftInput(innerView);
			}
		}
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		View currentFocus = getCurrentFocus();
		if (currentFocus != null) {
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
		}
	}

	public boolean isNetworkConnected() {
		if (NetWorkUtil.isNetworkConnected(this)) {
			return true;
		}
		ToastUtil.show(this, R.string.network_no_connection);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			onLeftBtn(v);
			break;
		case R.id.btn_right:
			onRightBtn(v);
			break;
		default:
			break;
		}
	}

	protected void onLeftBtn(View v) {
		onBackPressed();
	}

	protected void onRightBtn(View v) {

	}

	public void setTitle(String title) {
		if (tv_center != null)
			tv_center.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		if (tv_center != null)
			tv_center.setText(titleId);
	}
}
