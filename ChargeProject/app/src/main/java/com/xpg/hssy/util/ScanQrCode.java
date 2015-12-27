package com.xpg.hssy.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.zxing.Result;
import com.xpg.hssy.zxing.ZxingManager;
import com.xpg.hssy.zxing.ZxingManager.OnResultListener;
import com.xpg.hssy.zxing.view.ViewfinderView;
import com.xpg.hssychargingpole.R;

public class ScanQrCode extends com.xpg.hssy.base.BaseActivity {

	private ZxingManager mZxingManager;
	private SurfaceView preview_view;
	private ImageButton btn_left;

	@Override
	protected void initData() {
		super.initData();

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.scan_qr_code);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		preview_view = (SurfaceView) findViewById(R.id.preview_view);
		ViewfinderView viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);
		mZxingManager = new ZxingManager(this, viewfinderView, preview_view);
		mZxingManager.onCreate();
		viewfinderView.setType(ViewfinderView.TYPE_1D_CODE);
		viewfinderView.setTips("请二维码/条形码放入框内，即可自动扫描");
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		mZxingManager.setOnResultListener(new OnResultListener() {
			@Override
			public void OnResult(Result result, Bitmap barcode) {
				mZxingManager.onPause();
				String code = result.getText().trim();
				Log.v("二维码", code);
				System.out.println("code");
				Intent i = getIntent();
				i.putExtra("resultStringCode", code);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mZxingManager.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mZxingManager.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mZxingManager.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		finish();

	}
}
