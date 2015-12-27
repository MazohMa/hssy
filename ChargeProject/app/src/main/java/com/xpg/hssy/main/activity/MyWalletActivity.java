package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.dialog.DialogUtil;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * Created by Administrator on 2015/12/7.
 */
public class MyWalletActivity extends BaseActivity {

	private static final int REQUEST_FOR_REFRESH = 1;
	public static final int RESULT_RECHARGE_FAILED = 2;
	private TextView tv_charge_money;
	private LoadingDialog loadingDialog;
	private float remainMoney;
	private String userId;
	private SPFile spFile;

	@Override
	protected void initData() {
		super.initData();
		spFile = new SPFile(self, "config");
		userId = spFile.getString("user_id", null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (spFile != null) {
			userId = spFile.getString("user_id", null);
		}

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_my_wallet);
		((TextView) findViewById(R.id.tv_center)).setText(R.string.my_wallet);
		tv_charge_money = (TextView) findViewById(R.id.tv_charge_money);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_use).setOnClickListener(this);
		findViewById(R.id.btn_recharge).setOnClickListener(this);
		findViewById(R.id.ll_payment_history).setOnClickListener(this);
		findViewById(R.id.tv_use).setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getRemainMoney();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_use:
				Intent intent = new Intent(self, MyOrderFragmentActivity.class);
				intent.putExtra(KEY.INTENT.KEY_SHOW_ORDERING, true);
				startActivity(intent);
//				finish();
				break;
			case R.id.btn_recharge:
				intent = new Intent(self, RechargeActivity.class);
				startActivityForResult(intent, REQUEST_FOR_REFRESH);
				break;
			case R.id.ll_payment_history:
				intent = new Intent(self, PaymentRecordsActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_use:
				intent = new Intent(self, ChargeMoneyInstructionsActivity.class);
				startActivity(intent);
				break;
		}
	}

	//获取充电币余额
	private void getRemainMoney() {
		LogUtils.e("MyWallet...", "userId:" + userId);
		if (userId == null) return;
		WebAPIManager.getInstance().getRemainMoney(userId, new WebResponseHandler<String>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
				loadingDialog = new LoadingDialog(self, R.string.get_remaining_money);
				loadingDialog.show();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadingDialog != null) loadingDialog.dismiss();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<String> response) {
				super.onFailure(response);
				ToastUtil.show(self, R.string.get_remain_monuy_failed);
			}

			@Override
			public void onSuccess(WebResponse<String> response) {
				super.onSuccess(response);
				String result = response.getResult();
				if (EmptyUtil.notEmpty(result)) {
					remainMoney = Float.parseFloat(result);
				}
				tv_charge_money.setText(CalculateUtil.formatDefaultNumber(remainMoney) + "");
			}


		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_FOR_REFRESH:
				if (resultCode == RESULT_OK) {
					DialogUtil.showDialog(self, R.layout.dialog_recharge_tips, R.id.tv_recharge_result, R.string.recharge_succeed, R.id.btn_ok, R.string.known,
							null, null, null);
					getRemainMoney();
				}else if(resultCode == RESULT_RECHARGE_FAILED){
					DialogUtil.showDialog(self, R.layout.dialog_recharge_tips, R.id.tv_recharge_result, R.string.recharge_succeed, R.id.btn_ok, R.string.reselecte_recharge_way, new View.OnClickListener() {


						@Override
						public void onClick(View view) {
							Intent intent = new Intent(self, RechargeActivity.class);
							startActivityForResult(intent, REQUEST_FOR_REFRESH);
							DialogUtil.dismissDialog();
						}
					}, R.id.btn_cancel, R.string.cancel_recharge);
				}
				break;
		}
	}
}
