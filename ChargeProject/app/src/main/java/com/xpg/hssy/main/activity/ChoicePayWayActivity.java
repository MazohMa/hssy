package com.xpg.hssy.main.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.easy.util.EmptyUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.unionpay.UPPayAssistEx;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.BeecloudPaymentParam;
import com.xpg.hssy.db.pojo.PayResult;
import com.xpg.hssy.db.pojo.PaymentParam;
import com.xpg.hssy.db.pojo.PaymentParams;
import com.xpg.hssy.db.pojo.ReturnAlipay;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.DialogUtil;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.beecloud.BCPay;
import cn.beecloud.BCQuery;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.beecloud.entity.BCBillOrder;
import cn.beecloud.entity.BCPayResult;
import cn.beecloud.entity.BCQueryBillResult;
import cn.beecloud.entity.BCReqParams;

/**
 * @author Mazoh 选择支付方式
 */
public class ChoicePayWayActivity extends BaseActivity implements OnClickListener {
	private LinearLayout zhifubaoId;
	private LinearLayout baidupay_layout;
	private LinearLayout ll_select_remain_coin;
	private TextView tv_trade_order;
	private TextView tv_total_fee;
	private TextView tv_remain_coin;
	private TextView tv_need_to_pay;
	private ImageView iv_select_remain_coin;
	private ImageView img_ali;
	private ImageView img_baidu;
	private ImageButton btn_left;
	private Button next_step;
	private TextView tv_center;
	private int isMobile = 0;
	public static final String merchantUrl = "http://www.huashangsanyou.com/";// 重复支付跳转页面
	private String notifyUrl = WebAPI.BASE_URL + "/pay/notify";
	private String remark = "charging";
	public static final String PayCompleteUrl = "http://www.example.com/";// 支付成功跳转页面
	private String id;
	private LoadingDialog loadingDialog = null;
	private final String PAY_SUCCEED = "9000";
	private final String PAY_PROCESSING = "8000";
	private final String PAY_FAILED = "4000";
	private final String PAY_USER_CANCEL = "6001";
	private final String PAY_NETWORK_ERROR = "6002";
	private final String KEY_PARTNER = "partner";
	private final String KEY_SELLER_ID = "seller_id";
	private final String KEY_OUT_TRADE_NO = "out_trade_no";
	private final String KEY_SUBJECT = "subject";
	private final String KEY_BODY = "body";
	private final String KEY_TOTAL_FEE = "total_fee";
	private final String KEY_NOTIFY_URL = "notify_url";
	private final String KEY_SERVICE = "service";
	private final String KEY_PAYMENT_TYPE = "payment_type";
	private final String KEY_INPUT_CHARSET = "_input_charset";
	private final String KEY_IT_B_PAY = "it_b_pay";
	private final String KEY_RETURN_URL = "return_url";
	private final String KEY_SIGN = "sign";
	private final String KEY_SIGN_TYPE = "sign_type";

	private final int PAYWAY_ALIPAY = 2;
	private final int PAYWAY_BAIDUPAY = 1;
	private final int PAYWAY_NOT_SELECT = 0;

	private boolean isPaying;

	private int payWay;

	private SPFile sp;
	private User user;
	private String tradeOrder;
	private float remainMoney;
	private float needPay;
	private float totalFee;
	private boolean enoughFlag;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			PayResult payResult = new PayResult((String) msg.obj);
//            ResultChecker checker = new ResultChecker((String) msg.obj, publicKey);

			// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
			String resultStatus = payResult.getResultStatus();
			loadingDialog.dismiss();
			// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//            if (checker.checkSign() == ResultChecker.RESULT_CHECK_SIGN_SUCCEED) {
			if (TextUtils.equals(resultStatus, PAY_SUCCEED)) {
				ToastUtil.show(self, "支付成功");

//				String url = response.getResultObj().getUrl();
				Intent intent = new Intent(ChoicePayWayActivity.this, WebViewAlipayActivity.class);
				intent.putExtra("url", merchantUrl);
				startActivityForResult(intent, MyOrderFragmentActivity.REQUEST_REFRESH);
			} else {
				// 判断resultStatus 为非“9000”则代表可能支付失败
				// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
				if (TextUtils.equals(resultStatus, PAY_PROCESSING)) {

				} else {
					// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
					ToastUtil.show(self, R.string.pay_failed);
				}
			}
		}
//        }
	};

	private void caculteFee() {
		float diff = totalFee - remainMoney;
		if (diff > 0) {
			needPay = diff;
			enoughFlag = false;
		} else {
			needPay = 0;
			enoughFlag = true;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_select_remain_coin:
				if ((boolean) ll_select_remain_coin.getTag()) {
					tv_need_to_pay.setText(CalculateUtil.formatRechargeMoney(totalFee));
					ll_select_remain_coin.setTag(false);
					iv_select_remain_coin.setImageResource(R.drawable.wallet_not_choice);
				} else {
					selectReaminMoney();
				}
				break;
			case R.id.zhifubao_layout:
				if (enoughFlag && (boolean) ll_select_remain_coin.getTag()) {

				} else {
					selectAlipay();
				}
				break;
			case R.id.baidupay_layout:
				if (enoughFlag && (boolean) ll_select_remain_coin.getTag()) {

				} else {
					selectBaifubao();
				}
				break;
			case R.id.btn_left:
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				break;
			case R.id.next_step:
				isPaying = true;
				switch (payWay) {
					case MyConstant.PAYWAY_BAIFUBAO:
						requestPayForBeecloud(payWay);
						sp.put(KEY.INTENT.KEY_PAYWAY, MyConstant.PAYWAY_BAIFUBAO);
						break;
					case MyConstant.PAYWAY_ALIPAY:
						sp.put(KEY.INTENT.KEY_PAYWAY, MyConstant.PAYWAY_ALIPAY);
						requestPayForBeecloud(payWay);
						break;
					case MyConstant.PAYWAY_BAIFUBAO_WITHHOLDING:
						Intent intent = new Intent(self, BindBaifubaoActivity.class);
						intent.putExtra("id", id);
						startActivityForResult(intent, MyOrderFragmentActivity.REQUEST_REFRESH);
						sp.put(KEY.INTENT.KEY_PAYWAY, MyConstant.PAYWAY_BAIFUBAO);
						break;
					default:
						requestPayForBeecloud(payWay);
						break;
				}

				break;
			default:
				break;
		}

	}

	private void requestPay() {
		loadingDialog = new LoadingDialog(self, R.string.loading);
		loadingDialog.showDialog();
		WebAPIManager.getInstance().requestPayMoney(id, new WebResponseHandler<PaymentParam>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<PaymentParam> response) {
				super.onFailure(response);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<PaymentParam> response) {
				super.onSuccess(response);
				PaymentParam paymentParam = response.getResultObj();
				if (paymentParam == null || paymentParam.getSellerEmail() == null || "".equals(paymentParam.getSellerEmail())) {
					loadingDialog.dismiss();
					ToastUtil.show(self, "获取支付信息失败");
				} else {
					pay(paymentParam);
				}
			}

		});
	}

	private void requestPay2() {
		loadingDialog = new LoadingDialog(self, R.string.loading);
		loadingDialog.showDialog();
		WebAPIManager.getInstance().requestPayMoney2(id, new WebResponseHandler<PaymentParams>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<PaymentParams> response) {
				super.onFailure(response);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<PaymentParams> response) {
				super.onSuccess(response);
				PaymentParams paymentParams = response.getResultObj();
				if (paymentParams == null || EmptyUtil.isEmpty(paymentParams.getSeller_id())) {
					loadingDialog.dismiss();
					ToastUtil.show(self, "获取支付信息失败");
				} else {
//                            publicKey = paymentParams.getPublicKey();
					pay(paymentParams);
				}
			}

		});
	}

	private void requestPayForBeecloud(final int payType) {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
		loadingDialog = new LoadingDialog(self, R.string.loading);
		loadingDialog.showDialog();
		WebAPIManager.getInstance().requestPayMoneyForBeecloud(id, remainMoney, new WebResponseHandler<BeecloudPaymentParam>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
				if (loadingDialog != null) loadingDialog.dismiss();
			}

			@Override
			public void onFailure(WebResponse<BeecloudPaymentParam> response) {
				super.onFailure(response);
				TipsUtil.showTips(self, response);
				if (loadingDialog != null) loadingDialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}

//			@Override
//			public void onFinish() {
//				super.onFinish();
//				if (loadingDialog != null) loadingDialog.dismiss();
//			}

			@Override
			public void onSuccess(WebResponse<BeecloudPaymentParam> response) {
				super.onSuccess(response);
				BeecloudPaymentParam paymentParams = response.getResultObj();
				LogUtils.e(TAG, "beecloud:" + paymentParams.toString());
				if (paymentParams == null) {
					if (loadingDialog != null) loadingDialog.dismiss();
					ToastUtil.show(self, "获取支付信息失败");
				} else {
					if (paymentParams.isEnoughFlag()) {
						//充电币足够抵扣，则提示用户支付成功
						if (loadingDialog != null) loadingDialog.dismiss();
						ToastUtil.show(self, "支付成功");
						Intent intent = new Intent(ChoicePayWayActivity.this, WebViewAlipayActivity.class);
						intent.putExtra("url", merchantUrl);
						startActivityForResult(intent, MyOrderFragmentActivity.REQUEST_REFRESH);
					} else {
						if ((boolean) ll_select_remain_coin.getTag() && remainMoney > 0) {
							if (loadingDialog != null) loadingDialog.dismiss();
							View view = LayoutInflater.from(self).inflate(R.layout.dialog_pay_tips, null);
							DialogUtil.showDialog(self, view, false);
						}
						pay(paymentParams, payType);
					}
				}
			}

		});
	}

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

	private void pay(PaymentParam paymentParam) {
		WebAPIManager.getInstance().requesrAlipays(paymentParam.getSellerEmail(), paymentParam.getSubject(), paymentParam.getOutTradeNo(), paymentParam
				.getTotalFee(), PayCompleteUrl, merchantUrl, notifyUrl, isMobile, remark, paymentParam.getChannel(), paymentParam.getSign(), new
				WebResponseHandler<ReturnAlipay>(self) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<ReturnAlipay> response) {
				super.onFailure(response);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<ReturnAlipay> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
				String url = response.getResultObj().getUrl();
				Intent intent = new Intent(ChoicePayWayActivity.this, WebViewAlipayActivity.class);
				intent.putExtra("url", url);
				startActivityForResult(intent, MyOrderFragmentActivity.REQUEST_REFRESH);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//使用微信支付需增加
//		BCPay.initWechatPay(this, weixinAppId);

		getRemainMoney();
	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		id = intent.getStringExtra(KEY.INTENT.KEY_ID);
		totalFee = intent.getFloatExtra(KEY.INTENT.KEY_TOTAL_FEE, 0);
		tradeOrder = intent.getStringExtra(KEY.INTENT.KEY_TRADE_ORDER);
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.choicepayway_layout);
		zhifubaoId = (LinearLayout) findViewById(R.id.zhifubao_layout);
		baidupay_layout = (LinearLayout) findViewById(R.id.baidupay_layout);
		ll_select_remain_coin = (LinearLayout) findViewById(R.id.ll_select_remain_coin);
		tv_trade_order = (TextView) findViewById(R.id.tv_trade_order);
		tv_total_fee = (TextView) findViewById(R.id.tv_total_fee);
		tv_remain_coin = (TextView) findViewById(R.id.tv_remain_coin);
		tv_need_to_pay = (TextView) findViewById(R.id.tv_need_to_pay);
		iv_select_remain_coin = (ImageView) findViewById(R.id.iv_select_remain_coin);

		img_ali = (ImageView) findViewById(R.id.img_ali);
		img_baidu = (ImageView) findViewById(R.id.img_baidu);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		next_step = (Button) findViewById(R.id.next_step);
		tv_center = (TextView) findViewById(R.id.tv_center);
		tv_center.setText("付款方式");

		tv_trade_order.setText(tradeOrder);
		tv_total_fee.setText(CalculateUtil.formatRechargeMoney(totalFee));
		tv_need_to_pay.setText(CalculateUtil.formatRechargeMoney(totalFee));

		SPFile spFile = new SPFile(self, "config");
		String userId = spFile.getString("user_id", null);
		LogUtils.e("", "userId:" + userId);
		if (EmptyUtil.notEmpty(userId)) {
			sp = new SPFile(self, userId);
			user = DbHelper.getInstance(this).getUserDao().load(userId);
			int type = sp.getInt(KEY.INTENT.KEY_PAYWAY, MyConstant.PAYWAY_NONE);
			switch (type) {
				case MyConstant.PAYWAY_NONE:
					selectBaifubao();
					break;
				case MyConstant.PAYWAY_BAIFUBAO:
					selectBaifubao();
					break;
				case MyConstant.PAYWAY_ALIPAY:
					selectAlipay();
					break;
				default:
					selectBaifubao();
					break;
			}
		}
		ll_select_remain_coin.setTag(true);
	}

	private void isSelectOtherPay() {
		if (enoughFlag && (boolean) ll_select_remain_coin.getTag()) {
			payWay = MyConstant.PAYWAY_REMAIN_COIN;
			img_baidu.setImageResource(R.drawable.wallet_not_choice);
			img_ali.setImageResource(R.drawable.wallet_not_choice);
		}
	}

	private void selectReaminMoney() {
		if (remainMoney > 0) {
			iv_select_remain_coin.setImageResource(R.drawable.wallet_choice);
			ll_select_remain_coin.setTag(true);
			tv_need_to_pay.setText(CalculateUtil.formatRechargeMoney(needPay));
			isSelectOtherPay();
		} else {
			iv_select_remain_coin.setImageResource(R.drawable.wallet_not_choice);
			ll_select_remain_coin.setTag(false);
		}
	}

	private void selectAlipay() {
		payWay = MyConstant.PAYWAY_ALIPAY;
		img_baidu.setImageResource(R.drawable.wallet_not_choice);
		img_ali.setImageResource(R.drawable.wallet_choice);
	}

	private void selectBaifubao() {
		if (user != null && user.getContractFlag()) {
			payWay = MyConstant.PAYWAY_BAIFUBAO;
		} else {
			payWay = MyConstant.PAYWAY_BAIFUBAO_WITHHOLDING;
		}
		img_baidu.setImageResource(R.drawable.wallet_choice);
		img_ali.setImageResource(R.drawable.wallet_not_choice);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		zhifubaoId.setOnClickListener(this);
		baidupay_layout.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		next_step.setOnClickListener(this);
		ll_select_remain_coin.setOnClickListener(this);

	}


	private void showDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
		loadingDialog = new LoadingDialog(self, R.string.loading);
		loadingDialog.showDialog();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isPaying) {
			if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
			//TODO 通知后台支付取消
			cancelPay();
		}
	}

	private void cancelPay() {
		WebAPIManager.getInstance().cancelPayLock(id, new WebResponseHandler<String>() {
			@Override
			public void onFinish() {
				super.onFinish();
			}

			@Override
			public void onSuccess(WebResponse<String> response) {
				super.onSuccess(response);
//				ToastUtil.show(self, "订单解除成功！");
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<String> response) {
				super.onFailure(response);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.e(TAG, "onActivityResult requestCode:" + requestCode + "  resultCode:" + resultCode);
		if (requestCode == MyOrderFragmentActivity.REQUEST_REFRESH) {
			if (resultCode == RESULT_OK) {
				setResult(resultCode, data);
				finish();
			} else if (resultCode == 1001) {
				//手动刷新是否签约成功
				WebAPIManager.getInstance().requestHasBaifubaoContract(user.getUserid(), new WebResponseHandler<String>() {
					@Override
					public void onStart() {
						super.onStart();
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						loadingDialog = new LoadingDialog(self, R.string.loading);
						loadingDialog.show();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						setResult(RESULT_OK, data);
						finish();
					}

					@Override
					public void onFailure(WebResponse<String> response) {
						super.onFailure(response);
						setResult(RESULT_OK, data);
						finish();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (loadingDialog != null) loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(WebResponse<String> response) {
						super.onSuccess(response);
						if (response.getResult().trim().equals("true")) {
							ToastUtil.show(self, R.string.pay_succeed);
							user.setContractFlag(true);
							DbHelper.getInstance(self).getUserDao().update(user);
							setResult(RESULT_OK, data);
							finish();
						} else {
							ToastUtil.show(self, "取消支付");
						}
					}
				});
			} else if (resultCode == 1002) {
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	private void pay(final PaymentParams paymentParams) {
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(ChoicePayWayActivity.this);
				// 调用支付接口，获取支付结果
				String payInfo = KEY_PARTNER + "=\"" + paymentParams.getPartner() + "\"&" + KEY_SELLER_ID + "=\"" + paymentParams.getSeller_id() + "\"&" +
						KEY_OUT_TRADE_NO + "=\"" + paymentParams.getOut_trade_no() + "\"&" + KEY_SUBJECT + "=\"" + paymentParams.getSubject() + "\"&" +
						KEY_BODY + "=\"" + paymentParams.getBody() + "\"&" + KEY_TOTAL_FEE + "=\"" + paymentParams.getTotal_fee() + "\"&" + KEY_NOTIFY_URL +
						"=\"" + paymentParams.getNotifyUrl() + "\"&" + KEY_SERVICE + "=\"" + paymentParams.getService() + "\"&" + KEY_PAYMENT_TYPE + "=\"" +
						paymentParams.getPayment_type() + "\"&" + KEY_INPUT_CHARSET + "=\"" + paymentParams.get_input_charset() + "\"&" + KEY_IT_B_PAY +
						"=\"" + paymentParams.getIt_b_pay() + "\"&" + KEY_RETURN_URL + "=\"" + paymentParams.getReturn_url() + "\"&" + KEY_SIGN + "=\"" +
						paymentParams.getSign() + "\"&" + KEY_SIGN_TYPE + "=\"" + paymentParams.getSign_type() + "\"";
//				String pay = "partner=\"2088911205944812\"&seller_id=\"2088911205944812\"&out_trade_no=\"1014435157875707581\"&subject=\"charging\"&body
// =\"该测试商品的详细描述\"&total_fee=\"0.01\"&notify_url=\"http://222.128.77.155:8080/chargepile/pay/notify\"&service=\"mobile.securitypay
// .pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay
// .com\"&sign=\"r8HeJipRJayM4XHjvbeugUzjuNBHRoG8cBNriGe7cKsVLQd%2FqiKekvgZEKIsv0RVicqvxN6Evd0HMH00o2VckKVBGXvutrmE859KGjhr%2FjHx2Keao
// %2BdQVyxWMxWqIXKxFMyA2Dsrcb4JDb00x1VMgM36umyAdE3j9mtRtmCC%2Brg%3D\"&sign_type=\"RSA\"";
				LogUtils.v("pay", "payInfo:" + payInfo);
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}


	//支付结果返回入口
	BCCallback bcCallback = new BCCallback() {
		@Override
		public void done(final BCResult bcResult) {
			final BCPayResult bcPayResult = (BCPayResult) bcResult;
			if (loadingDialog != null) loadingDialog.dismiss();
			DialogUtil.dismissDialog();
			//需要注意的是，此处如果涉及到UI的更新，请在UI主进程或者Handler操作
			ChoicePayWayActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					String result = bcPayResult.getResult();

                    /*
                      注意！
                      所有支付渠道建议以服务端的状态金额为准，此处返回的RESULT_SUCCESS仅仅代表手机端支付成功
                    */
					if (result.equals(BCPayResult.RESULT_SUCCESS)) {
						ToastUtil.show(self, "支付成功");
						Intent intent = new Intent(ChoicePayWayActivity.this, WebViewAlipayActivity.class);
						intent.putExtra("url", merchantUrl);
						startActivityForResult(intent, MyOrderFragmentActivity.REQUEST_REFRESH);
					} else if (result.equals(BCPayResult.RESULT_CANCEL)) {
						ToastUtil.show(self, "取消支付");
						cancelPay();
					} else if (result.equals(BCPayResult.RESULT_FAIL)) {
						String detailInfo = bcPayResult.getDetailInfo();
						if (EmptyUtil.notEmpty(detailInfo) && detailInfo.contains("#")) {
							detailInfo = detailInfo.split("#")[0];
						}
						ToastUtil.show(self, detailInfo);
//						ToastUtil.show(self, "支付失败, 原因: " + bcPayResult.getErrMsg() + ", " + bcPayResult.getDetailInfo());
						cancelPay();
						if (bcPayResult.getErrMsg().equals(BCPayResult.FAIL_PLUGIN_NOT_INSTALLED) || bcPayResult.getErrMsg().equals(BCPayResult
								.FAIL_PLUGIN_NEED_UPGRADE)) {
							//银联需要重新安装控件
							Message msg = mmHandler.obtainMessage();
							msg.what = 1;
							mmHandler.sendMessage(msg);
						}
					} else if (result.equals(BCPayResult.RESULT_UNKNOWN)) {
						//可能出现在支付宝8000返回状态
						cancelPay();
						ToastUtil.show(self, "订单状态未知");
					} else {
						cancelPay();
						ToastUtil.show(self, "invalid return");
					}
					if (bcPayResult.getId() != null) {
						//你可以把这个id存到你的订单中，下次直接通过这个id查询订单
//						getBillInfoByID(bcPayResult.getId());
					}
				}
			});
		}
	};


	private void getBillInfoByID(String id) {

		BCQuery.getInstance().queryBillByIDAsync(id, new BCCallback() {
			@Override
			public void done(BCResult result) {
				BCQueryBillResult billResult = (BCQueryBillResult) result;

				Log.e(TAG, "------ response info ------");
				Log.e(TAG, "------getResultCode------" + billResult.getResultCode());
				Log.e(TAG, "------getResultMsg------" + billResult.getResultMsg());
				Log.e(TAG, "------getErrDetail------" + billResult.getErrDetail());

				Log.e(TAG, "------- bill info ------");
				BCBillOrder billOrder = billResult.getBill();
				Log.e(TAG, "订单号:" + billOrder.getBillNum());
				Log.e(TAG, "订单金额, 单位为分:" + billOrder.getTotalFee());
				Log.e(TAG, "渠道类型:" + BCReqParams.BCChannelTypes.getTranslatedChannelName(billOrder.getChannel()));
				Log.e(TAG, "子渠道类型:" + BCReqParams.BCChannelTypes.getTranslatedChannelName(billOrder.getSubChannel()));

				Log.e(TAG, "订单是否成功:" + billOrder.getPayResult());

				if (billOrder.getPayResult()) Log.e(TAG, "渠道返回的交易号，未支付成功时，是不含该参数的:" + billOrder.getTradeNum());
				else Log.e(TAG, "订单是否被撤销，该参数仅在线下产品（例如二维码和扫码支付）有效:" + billOrder.getRevertResult());

				Log.e(TAG, "订单创建时间:" + new Date(billOrder.getCreatedTime()));
				Log.e(TAG, "扩展参数:" + billOrder.getOptional());
			}
		});
	}

	// Defines a Handler object that's attached to the UI thread.
	// 通过Handler.Callback()可消除内存泄漏警告
	private Handler mmHandler = new Handler(new Handler.Callback() {
		/**
		 * Callback interface you can use when instantiating a Handler to avoid
		 * having to implement your own subclass of Handler.
		 *
		 * handleMessage() defines the operations to perform when
		 * the Handler receives a new Message to process.
		 */
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				//如果用户手机没有安装银联支付控件,则会提示用户安装
				AlertDialog.Builder builder = new AlertDialog.Builder(ChoicePayWayActivity.this);
				builder.setTitle("提示");
				builder.setMessage("完成支付需要安装或者升级银联支付控件，是否安装？");

				builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UPPayAssistEx.installUPPayPlugin(ChoicePayWayActivity.this);
						dialog.dismiss();
					}
				});

				builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			return true;
		}
	});

	//获取充电币余额
	private void getRemainMoney() {
		if (user == null) return;
		WebAPIManager.getInstance().getRemainMoney(user.getUserid(), new WebResponseHandler<String>() {
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
				caculteFee();
				tv_remain_coin.setText(CalculateUtil.formatDefaultNumber(remainMoney) + "个");
//					tv_need_to_pay.setText(CalculateUtil.formatRechargeMoney(needPay));
				selectReaminMoney();

			}


		});
	}

	private static final String TAG = "ChoicePayWayActivity";

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//清理当前的activity引用
		BCPay.detachBaiduPay();
//		BCPay.detach();
	}


}
