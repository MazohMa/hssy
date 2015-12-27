package com.xpg.hssy.main.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssychargingpole.R;

/**
 * WebViewAlipayActivity
 * 
 * @author Mazoh 支付宝
 * 
 */
public class WebViewAlipayActivity extends BaseActivity {

	private WebView webView_alipay;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		url = getIntent().getStringExtra("url");

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.webview_alipay);
		webView_alipay = (WebView) findViewById(R.id.webView_alipay);
		webView_alipay.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		webView_alipay.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放
		webView_alipay.loadUrl(url);
		webView_alipay.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				log("------" + url);
				if (url.startsWith("http://www.huashangsanyou.com/")
						|| url.startsWith("http://www.example.com/")) {
					// 支付完成
					setResult(RESULT_OK);
					finish();
					return true;
				}
				view.loadUrl(url);// 使用当前WebView处理跳转
				return true;// true表示此事件在此处被处理，不需要再广播
			}

			@Override
			// 转向错误时的处理
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				ToastUtil.show(WebViewAlipayActivity.this, "Oh no! "
						+ description);
			}
		});
	}

	@Override
	// 默认点回退键，会退出Activity，需监听按键操作，使回退在WebView内发生
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_alipay.canGoBack()) {
			webView_alipay.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
