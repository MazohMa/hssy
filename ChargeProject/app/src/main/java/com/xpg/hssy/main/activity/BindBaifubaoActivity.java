package com.xpg.hssy.main.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.easy.util.EmptyUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.BaifubaoParam;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * Created by Administrator on 2015/11/20.
 */
public class BindBaifubaoActivity extends BaseActivity {

	private WebView webView;
	private ImageView iv_left;
	private static final String url = "https://www.baifubao.com/wap/0/contract_sign/0";
	private String id;
	private LoadingDialog loadingDialog;
//	private int type = 0;

	@Override
	protected void initData() {
		super.initData();
		id = getIntent().getStringExtra("id");
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_bindbaifubao);
		webView = (WebView) findViewById(R.id.bind_webview);
		iv_left = (ImageView) findViewById(R.id.iv_left);
		webView.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString) {
				super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
				if (loadingDialog != null) loadingDialog.dismiss();
			}


			public void onPageStarted(WebView paramAnonymousWebView, String paramAnonymousString, Bitmap paramAnonymousBitmap) {
				super.onPageStarted(paramAnonymousWebView, paramAnonymousString, paramAnonymousBitmap);
				LogUtils.e("", "paramAnonymousString:" + paramAnonymousString);
				if(paramAnonymousString.startsWith(WebAPI.BASE_URL)||paramAnonymousString.startsWith("http://yyc.renrenchongdian.com")){
					SPFile sp = new SPFile(self, "config");
					String userId = sp.getString("user_id", null);
					if (EmptyUtil.notEmpty(userId)) {
						User user = DbHelper.getInstance(self).getUserDao().load(userId);
						user.setContractFlag(true);
						DbHelper.getInstance(self).getUserDao().update(user);
					}
					setResult(RESULT_OK);
					finish();
				}
			}

			public void onReceivedError(WebView paramAnonymousWebView, int paramAnonymousInt, String paramAnonymousString1, String paramAnonymousString2) {
				super.onReceivedError(paramAnonymousWebView, paramAnonymousInt, paramAnonymousString1, paramAnonymousString2);

			}

			public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString) {
				paramAnonymousWebView.loadUrl(paramAnonymousString);
				return true;
			}
		});
		WebSettings localWebSettings = webView.getSettings();
		localWebSettings.setJavaScriptEnabled(true);
		localWebSettings.setBuiltInZoomControls(true);
		localWebSettings.setSupportZoom(true);
		localWebSettings.setUseWideViewPort(true);
		localWebSettings.setLoadWithOverviewMode(true);
//		if (type == 0) {
			requestPay();
//		}
//		else if(type ==1) {
//			String str  = "contract_no=201511250003343110&sign_method=1&sp_no=1000293835&key=f2w3JVpbf8fuhRGCityEBw2qTefRgwXw";
//			String str2 = "contract_no=201511250003343110&sign_method=1&sp_no=1000293835";
//			String sign = MD5Util.toMD5(str);
//			String url = "https://www.baifubao.com/contract/0/cancel/0?" + str2+"&sign="+ sign;
//			LogUtils.e("","url:"+url);
//			webView.loadUrl(url);
//		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		iv_left.setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_left:
				setResult(1001);
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
		}
	}

	@Override
	public void onBackPressed() {
		setResult(1001);
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	private void requestPay() {
		WebAPIManager.getInstance().requestPayMoneyForBaifubao(id, new WebResponseHandler<BaifubaoParam>() {
			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
					loadingDialog = null;
				}
				loadingDialog = new LoadingDialog(self, R.string.loading);
				loadingDialog.showDialog();
			}

			@Override
			public void onFailure(WebResponse<BaifubaoParam> response) {
				super.onFailure(response);
				if (loadingDialog != null) loadingDialog.dismiss();
				ToastUtil.show(self, response.getMessage());
				setResult(1002);
				finish();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (loadingDialog != null) loadingDialog.dismiss();
				finish();
				setResult(1002);
				ToastUtil.show(self, "获取支付信息出错！");
			}

			@Override
			public void onSuccess(WebResponse<BaifubaoParam> response) {
				super.onSuccess(response);
//				String str = "https://www.baifubao.com/wap/0/contract_sign/0?" + response.getResult ();
				BaifubaoParam baifubaoParam = response.getResultObj();
				if (baifubaoParam != null) {
					LogUtils.e("", "secret:" + baifubaoParam.getSecret());
					String sign = baifubaoParam.getSecret();
					LogUtils.e("", "sign:" + sign);
					webView.loadUrl("https://www.baifubao.com/wap/0/contract_sign/0?" + sign);
				}
			}


		});
	}


	/*private String getSign(OrderInfo paramOrderInfo) {
		TreeMap localTreeMap = new TreeMap(new Comparator() {
			public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2) {
				return paramAnonymousObject1.toString().compareTo(paramAnonymousObject2.toString());
			}
		});
		localTreeMap.put("service_code", "1");
		localTreeMap.put("sp_no", "1000293835");
		localTreeMap.put("order_create_time", String.valueOf(paramOrderInfo.create_time));
		localTreeMap.put("order_no", paramOrderInfo.order_no);
		localTreeMap.put("goods_name", "charge");
		localTreeMap.put("unit_amount", "0");
		localTreeMap.put("unit_count", "0");
		localTreeMap.put("transport_amount", "0");
		localTreeMap.put("total_amount", "0");
		localTreeMap.put("currency", "1");
		localTreeMap.put("return_url", "http://appapi.evehicle.cn/card/callback");
		localTreeMap.put("pay_type", "2");
		localTreeMap.put("input_charset", "1");
		localTreeMap.put("version", "2");
		localTreeMap.put("sign_method", "1");
		localTreeMap.put("sp_user_name", "yd_001");
		localTreeMap.put("contract_type", "2");
		localTreeMap.put("pure_sign", "1");


		StringBuilder localStringBuilder1 = new StringBuilder();
		StringBuilder localStringBuilder2 = new StringBuilder();
		int i = localTreeMap.size();
		int j = 0;
		Iterator localIterator = localTreeMap.entrySet().iterator();

		while (true) {
			try {
				if (!localIterator.hasNext()) {
					LogUtils.e("", "str:localStringBuilder1:" + localStringBuilder1);
					LogUtils.e("", "str:localStringBuilder2:" + localStringBuilder2);
					String str3 = MD5Util.toMD5(localStringBuilder1.toString() + "&key=xPsM5P3RMdrgYnpZYnK7z43xJjUN7FLD");
					return localStringBuilder2.toString() + "&sign=" + str3;
				}
				Map.Entry localEntry = (Map.Entry) localIterator.next();
				String str1 = (String) localEntry.getKey();
				String str2 = (String) localEntry.getValue();
				localStringBuilder1.append(str1).append("=").append(str2);
				if (TextUtils.equals(str1, "goods_name")) {
					localStringBuilder2.append(str1).append("=").append(URLEncoder.encode(str2, "GBK"));
					if (j < i - 1) {
						localStringBuilder1.append("&");
						localStringBuilder2.append("&");
					}
					j++;
				} else {
					localStringBuilder2.append(str1).append("=").append(str2);
					if (j < i - 1) {
						localStringBuilder1.append("&");
						localStringBuilder2.append("&");
					}
					j++;
					continue;
				}
			} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
				localUnsupportedEncodingException.printStackTrace();
				return null;
			}
		}
	}*/

	public static byte[] getUTF8toGBKString(String paramString) {
		int i = paramString.length();
		byte[] arrayOfByte1 = new byte[i * 3];
		int j = 0;
		int k = 0;
		if (j >= i) {
			if (k < arrayOfByte1.length) {
				byte[] arrayOfByte2 = new byte[k];
				System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, k);
				return arrayOfByte2;
			}
		} else {
			int m = paramString.charAt(j);
			int i2;
			if ((m < 128) && (m >= 0)) {
				i2 = k + 1;
				arrayOfByte1[k] = ((byte) m);
			}
			while (true) {
				j++;

				int n = k + 1;
				arrayOfByte1[k] = ((byte) (0xE0 | m >> 12));
				int i1 = n + 1;
				arrayOfByte1[n] = ((byte) (0x80 | 0x3F & m >> 6));
				i2 = i1 + 1;
				arrayOfByte1[i1] = ((byte) (0x80 | m & 0x3F));
				k = i2;
				break;
			}
		}
		return arrayOfByte1;
	}
}
