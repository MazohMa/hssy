package com.xpg.hssy.dialog;

import u.aly.cu;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.easy.util.AppInfo;
import com.easy.util.ToastUtil;
import com.xpg.hssy.util.UpdateUtil;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

public class WaterBlueDialogVersion extends BaseDialog {
	private static final int MAX_CLICK_TIMES = 6;
	private static final long MAX_CLICK_INTERVAL = 500;
	private int clickTimes;
	private long lastClickTime;

	public WaterBlueDialogVersion(final Context context) {
		super(context);
		setContentView(R.layout.water_blue_dialog_update);
		setTitle("版本更新");
		setContent("当前版本：V" + AppInfo.getVersionName(context));
		setLeftBtnText("取消");
		setRightBtnText("检查更新");
		setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UpdateUtil.manualUpdate(getContext());
				dismiss();
			}
		});

		content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				long curTime = System.currentTimeMillis();
				if (curTime - lastClickTime > MAX_CLICK_INTERVAL) {
					clickTimes = 0;
				}
				clickTimes++;
				lastClickTime = curTime;

				if (clickTimes >= MAX_CLICK_TIMES) {
					StringBuilder sb = new StringBuilder();
					sb.append("内部版本号：" + AppInfo.getVersionCode(context) + "\n");
					sb.append("服务器地址：" + WebAPI.BASE_URL);
					Toast.makeText(getContext(), sb.toString(),
							Toast.LENGTH_LONG).show();
					clickTimes = 0;
				}
			}
		});
	}
}
