package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssychargingpole.R;

/**
 * @author Guitian 动态资讯item跳转过来的网页
 */
public class WebViewNewsActivity extends BaseActivity {

	private ImageButton btn_left;
	private ImageButton btn_right;
	private TextView tv_center;
	private WebView wv_news;
	private AnimationDrawable loadingAdAnimation;
	private ImageView iv_loading;
	private RelativeLayout rl_loading_fail;
	private RelativeLayout rl_loading;
	private Button btn_try_to_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_news);
		initViews();
	}

	private void initViews() {
		rl_loading_fail = (RelativeLayout) findViewById(R.id.rl_loading_fail);
		btn_try_to_refresh = (Button) rl_loading_fail.findViewById(R.id.btn_try_to_refresh);
		btn_try_to_refresh.setOnClickListener(this);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		iv_loading = (ImageView) rl_loading.findViewById(R.id.iv_loading);
		loadingAdAnimation = (AnimationDrawable) iv_loading.getBackground();
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_left.setOnClickListener(this);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		tv_center = (TextView) findViewById(R.id.tv_center);
		tv_center.setText(R.string.dynamic_title);
		tv_center.setCompoundDrawables(null, null, null, null);
		wv_news = (WebView) findViewById(R.id.wv_news);
		wv_news.getSettings().setSupportZoom(false);
		wv_news.getSettings().setJavaScriptEnabled(true);
		wv_news.getSettings().setDomStorageEnabled(true);
		wv_news.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				rl_loading.setVisibility(View.VISIBLE);
				loadingAdAnimation.start();
				rl_loading_fail.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				loadingAdAnimation.stop();
				rl_loading.setVisibility(View.GONE);
				rl_loading_fail.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadingAdAnimation.stop();
				rl_loading.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				Log.i("tag", "onReceivedSslError");
//                super.onReceivedSslError(view, handler, error);
				//handler.cancel(); 默认的处理方式，WebView变成空白页
				handler.proceed();//接受证书
				//handleMessage(Message msg); 其他处理
			}
		});
		loadData();
	}

	private void onClose() {
		wv_news.loadUrl("about:blank");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_try_to_refresh:
				loadData();
				break;
		}
	}

	private void loadData() {
		Intent data = getIntent();
		if (data != null) {
			String link = data.getStringExtra(KEY.INTENT.WEB_LINK);
			Log.i("tag", "链接：" + link);
			if (link != null) {
				wv_news.loadUrl(link);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		onClose() ;
		wv_news.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		wv_news.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		wv_news.destroy();
		System.gc();
	}
}
