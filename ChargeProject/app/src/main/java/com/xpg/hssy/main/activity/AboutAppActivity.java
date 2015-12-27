package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.UriUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh 关于优易充
 */
public class AboutAppActivity extends BaseActivity implements OnClickListener {

	private LoadingDialog loadingDialog;
	private ImageView iv_qr_code;
	private DisplayImageOptions options;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			default:
				break;
		}

	}


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
		setContentView(R.layout.activity_about_app);
		iv_qr_code = (ImageView) findViewById(R.id.iv_qr_code);
		options = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).cacheOnDisk(true)
				.showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img).showImageOnLoading(R.drawable.default_img).build();
		findViewById(R.id.topay).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = UriUtil.getIntent(AboutAppActivity.this);
				boolean b = UriUtil.judge(AboutAppActivity.this, i);
				if (b == false) {
					startActivity(i);
				}
			}
		});
		WebAPIManager.getInstance().getDownloadQrCode(new WebResponseHandler<String>() {
			@Override
			public void onStart() {
				super.onStart();
				if(loadingDialog!=null&&loadingDialog.isShowing())
				{
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(self,R.string.loading);
				loadingDialog.show();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if(loadingDialog!=null&&loadingDialog.isShowing())
				{
					loadingDialog.dismiss();
				}
			}

			@Override
			public void onSuccess(WebResponse<String> response) {
				super.onSuccess(response);
				String url = response.getResult();
				if (!TextUtils.isEmpty(url)) {
					url = url.replaceAll("\"","");
					ImageLoaderManager.getInstance().displayImage(url, iv_qr_code, options, true);
				}
			}
		});
	}

	@Override
	protected void initEvent() {
		super.initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
