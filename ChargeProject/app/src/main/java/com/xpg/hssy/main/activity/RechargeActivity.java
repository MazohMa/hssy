package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.BeecloudPaymentParam;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialog;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.beecloud.BCPay;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.beecloud.entity.BCPayResult;
import cn.beecloud.entity.BCReqParams;

/**
 * Created by Administrator on 2015/12/7.
 */
public class RechargeActivity extends BaseActivity {

	private EditText et_money;
	private Button btn_ok;
	private ImageView iv_select_baifubao;
	private ImageView iv_select_zhifubao;
	private SPFile sp;
	private String userid;
	private LoadingDialog loadingDialog;
	private int payWay;


	@Override
	protected void initData() {
		super.initData();
		sp = new SPFile(self, "config");
		userid = sp.getString("user_id", null);
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = getLayoutInflater().inflate(R.layout.activity_recharge,null);
		setContentView(view);
		hideSoftInput(view);
		((TextView) findViewById(R.id.tv_center)).setText(R.string.recharge);
		et_money = (EditText) findViewById(R.id.et_money);
		iv_select_baifubao = (ImageView) findViewById(R.id.iv_select_baifubao);
		iv_select_zhifubao = (ImageView) findViewById(R.id.iv_select_zhifubao);
		btn_ok = (Button) findViewById(R.id.btn_ok);

		SPFile spFile = new SPFile(self, "config");
		userid = spFile.getString("user_id", null);
		if (EmptyUtil.notEmpty(userid)) {
			sp = new SPFile(self, userid);
			int type = sp.getInt(KEY.INTENT.KEY_PAYWAY, MyConstant.PAYWAY_NONE);
			switch (type) {
				case MyConstant.PAYWAY_NONE:
					selectBaifubao();
					break;
				case MyConstant.PAYWAY_BAIFUBAO:
					selectBaifubao();
					break;
				case MyConstant.PAYWAY_ALIPAY:
					selectZhifubao();
					break;
				default:
					selectBaifubao();
					break;
			}
		}
	}

	private void selectBaifubao() {
		iv_select_baifubao.setImageResource(R.drawable.wallet_choice);
		iv_select_zhifubao.setImageResource(R.drawable.wallet_not_choice);
//		iv_select_baifubao.setVisibility(View.VISIBLE);
//		iv_select_zhifubao.setVisibility(View.INVISIBLE);
		payWay = MyConstant.PAYWAY_BAIFUBAO;
	}

	private void selectZhifubao() {
		iv_select_baifubao.setImageResource(R.drawable.wallet_not_choice);
		iv_select_zhifubao.setImageResource(R.drawable.wallet_choice);
//		iv_select_baifubao.setVisibility(View.INVISIBLE);
//		iv_select_zhifubao.setVisibility(View.VISIBLE);
		payWay = MyConstant.PAYWAY_ALIPAY;
	}

	//请求充值参数
	private void requestParams(double money) {
		BigDecimal bigDecimal = new BigDecimal(money);
		float currencyMoney = bigDecimal.floatValue();
		LogUtils.e("", "currencyMoney:" + currencyMoney + "  money:" + money);
		if (EmptyUtil.isEmpty(userid)) return;
		WebAPIManager.getInstance().recharge(userid, currencyMoney, new WebResponseHandler<BeecloudPaymentParam>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
				loadingDialog = new LoadingDialog(self, R.string.loading);
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
			public void onFailure(WebResponse<BeecloudPaymentParam> response) {
				super.onFailure(response);
				ToastUtil.show(self, response.getMessage());
			}

			@Override
			public void onSuccess(WebResponse<BeecloudPaymentParam> response) {
				super.onSuccess(response);
				BeecloudPaymentParam paymentParams = response.getResultObj();
				if (paymentParams == null) {
					ToastUtil.show(self, "获取支付信息失败");
				} else {
					pay(paymentParams, payWay);
				}
			}
		});
	}

	//支付结果返回入口
	private BCCallback bcCallback = new BCCallback() {
		@Override
		public void done(final BCResult bcResult) {
			final BCPayResult bcPayResult = (BCPayResult) bcResult;
			if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
			//需要注意的是，此处如果涉及到UI的更新，请在UI主进程或者Handler操作
			RechargeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					String result = bcPayResult.getResult();

                    /*
                      注意！
                      所有支付渠道建议以服务端的状态金额为准，此处返回的RESULT_SUCCESS仅仅代表手机端支付成功
                    */
					if (result.equals(BCPayResult.RESULT_SUCCESS)) {
						ToastUtil.show(self, "充值成功");
						setResult(RESULT_OK);
						finish();
					} else if (result.equals(BCPayResult.RESULT_CANCEL)) {
						ToastUtil.show(self, "取消充值");
//						setResult(MyWalletActivity.RESULT_RECHARGE_FAILED);
//						finish();
					} else if (result.equals(BCPayResult.RESULT_FAIL)) {
						String detailInfo = bcPayResult.getDetailInfo();
						if (EmptyUtil.notEmpty(detailInfo) && detailInfo.contains("#")) {
							detailInfo = detailInfo.split("#")[0];
						}
						ToastUtil.show(self, detailInfo);
						setResult(MyWalletActivity.RESULT_RECHARGE_FAILED);
						finish();
					} else if (result.equals(BCPayResult.RESULT_UNKNOWN)) {
						//可能出现在支付宝8000返回状态
						ToastUtil.show(self, "订单状态未知");
						setResult(MyWalletActivity.RESULT_RECHARGE_FAILED);
						finish();
					} else {
						ToastUtil.show(self, "invalid return");
						setResult(MyWalletActivity.RESULT_RECHARGE_FAILED);
						finish();
					}
					if (bcPayResult.getId() != null) {
						//你可以把这个id存到你的订单中，下次直接通过这个id查询订单
//						getBillInfoByID(bcPayResult.getId());
					}
				}
			});
		}
	};

	private void pay(BeecloudPaymentParam paymentParam, int payType) {
		switch (payType) {
			case MyConstant.PAYWAY_ALIPAY:
				Map<String, String> mapOptional = new HashMap<String, String>();
				mapOptional = new HashMap<String, String>();
				mapOptional.put("客户端", "安卓");
				mapOptional.put("consumptioncode", "consumptionCode");
				mapOptional.put("money", "2");
				BCPay.getInstance(self).reqAliPaymentAsync(paymentParam.getBillTitle(),               //订单标题
						paymentParam.getBillTotalFee(), paymentParam.getBillNum(), mapOptional, bcCallback);

				break;
			case MyConstant.PAYWAY_BAIFUBAO:
				mapOptional = new HashMap<String, String>();
				mapOptional.put("goods desc", "商品详细描述");
				Map<String, String> analysis;
				//通过创建PayParam的方式发起支付
				//你也可以通过reqBaiduPaymentAsync的方式支付
				BCPay.PayParams payParam = new BCPay.PayParams();
				        /*
				        *  支付渠道，此处以百度钱包为例，实际支付允许
                        *  BCReqParams.BCChannelTypes.WX_APP，
                        *  BCReqParams.BCChannelTypes.ALI_APP，
                        *  BCReqParams.BCChannelTypes.UN_APP，
                        *  BCReqParams.BCChannelTypes.BD_APP，
                        *  BCReqParams.BCChannelTypes.PAYPAL_SANDBOX，
                        *  BCReqParams.BCChannelTypes.PAYPAL_LIVE
                        */
				payParam.channelType = BCReqParams.BCChannelTypes.BD_APP;

				//商品描述, 32个字节内, 汉字以2个字节计
				payParam.billTitle = paymentParam.getBillTitle();

				//支付金额，以分为单位，必须是正整数
				payParam.billTotalFee = paymentParam.getBillTotalFee();

				//商户自定义订单号
				payParam.billNum = paymentParam.getBillNum();

				//订单超时时间，以秒为单位，建议最小300，可以为null
				payParam.billTimeout = 300;

				//扩展参数，可以传入任意数量的key/value对来补充对业务逻辑的需求，可以为null
				payParam.optional = mapOptional;

				//扩展参数，用于后期分析，目前只支持key为category的分类分析，可以为null
				analysis = new HashMap<String, String>();
				analysis.put("category", "BD");
				payParam.analysis = analysis;

				BCPay.getInstance(self).reqPaymentAsync(payParam, bcCallback);
				LogUtils.e("", "payParam:" + payParam.toString());
				break;
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.rl_baifubao).setOnClickListener(this);
		findViewById(R.id.rl_zhifubao).setOnClickListener(this);
		findViewById(R.id.tv_use).setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		et_money.addTextChangedListener(new InputMoneyTextWatcher());
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.rl_baifubao:
				selectBaifubao();

				break;
			case R.id.rl_zhifubao:
				selectZhifubao();

				break;
			case R.id.btn_ok: {
				if (TextUtils.isEmpty(et_money.getText())) {
					final WaterBlueDialog waterBlueDialog = new WaterBlueDialog(this);
					waterBlueDialog.setContent("请输入有效的充值金额");
					waterBlueDialog.setRightListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							waterBlueDialog.dismiss();
						}
					});
					waterBlueDialog.setLeftListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							waterBlueDialog.dismiss();
						}
					});
					waterBlueDialog.show();
				} else {
					double money = Double.valueOf(et_money.getText().toString());
					if (money <= 0) {
						final WaterBlueDialog waterBlueDialog = new WaterBlueDialog(this);
						waterBlueDialog.setContent("请输入有效的充值金额");
						waterBlueDialog.setRightListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								waterBlueDialog.dismiss();
							}
						});
						waterBlueDialog.setLeftListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								waterBlueDialog.dismiss();
							}
						});
						waterBlueDialog.show();
						return;
					}
//				ToastUtil.show(self, money + "");
					requestParams(money);
//				BeecloudPaymentParam param = new BeecloudPaymentParam();
//				param.setBillNum("2134654654654");
//				param.setBillTitle("充值");
//				param.setBillTotalFee(1);
//				pay(param, MyConstant.PAYWAY_BAIFUBAO);
				}
				break;
			}
			case R.id.tv_use:
				Intent intent = new Intent(self, ChargeMoneyInstructionsActivity.class);
				startActivity(intent);
				break;
		}
	}

	private class InputMoneyTextWatcher implements TextWatcher {
		private String inputMoney = "";
		private boolean resetInput = false;

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (!resetInput) inputMoney = charSequence.toString();
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//			if (!TextUtils.isEmpty(et_money.getText())) {
//				btn_ok.setEnabled(true);
//			} else {
//				btn_ok.setEnabled(false);
//			}
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if (!editable.toString().equals("") && !editable.toString().matches("^0$|^[1-9]+\\d*$|^0\\.\\d{0,2}$|^[1-9]+\\d*\\.\\d{0,2}$")) {
				resetInput = true;
				editable.clear();
				editable.append(inputMoney);
				resetInput = false;
			}
		}
	}
}
