package com.king.photo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.king.photo.adapter.FolderAdapter;
import com.king.photo.util.Bimp;
import com.king.photo.util.PublicWay;
import com.xpg.hssy.main.activity.PileManageToSettingInfoActivity;
import com.xpg.hssychargingpole.R;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;
	private String pile_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;
		pile_id = getIntent().getStringExtra("pile_id");
		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText("照片");
		folderAdapter = new FolderAdapter(this);
		folderAdapter.setPile_id(pile_id);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		@Override
		public void onClick(View v) {
			// 清空选择的图片
			Bimp.tempSelectBitmap.clear();
			Intent intent = new Intent();
			intent.putExtra("pile_id", pile_id);
			intent.setClass(mContext, PileManageToSettingInfoActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra("pile_id", pile_id);
			intent.setClass(mContext, PileManageToSettingInfoActivity.class);
			startActivity(intent);
		}

		return true;
	}

}
