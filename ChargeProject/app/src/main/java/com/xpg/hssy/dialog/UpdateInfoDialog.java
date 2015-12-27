package com.xpg.hssy.dialog;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.easy.util.ToastUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateResponse;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

@SuppressLint("DefaultLocale")
public class UpdateInfoDialog extends BaseDialog {

	public UpdateInfoDialog(final Context context,
			final UpdateResponse updateInfo) {
		super(context);
		setContentView(R.layout.dialog_update_info);

		((TextView) findViewById(R.id.tv_version)).setText("V"
				+ updateInfo.version);
		String sizeStr = "未知";
		try {
			int size = Integer.parseInt(updateInfo.target_size);
			if (size > 1024 * 1024) {
				sizeStr = String.format("%.2fMB", size / 1024f / 1024f);
			} else {
				sizeStr = String.format("%.2fKB", size / 1024f);
			}
		} catch (Exception e) {
		}
		((TextView) findViewById(R.id.tv_size)).setText(sizeStr);
		((TextView) findViewById(R.id.tv_content)).setText(updateInfo.updateLog
				.replaceFirst("更新内容：\\s+", ""));

		setLeftListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UmengUpdateAgent.ignoreUpdate(context, updateInfo);
				dismiss();
			}
		});
		setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				File file = UmengUpdateAgent
						.downloadedFile(context, updateInfo);
				if (file == null) {
					UmengUpdateAgent.startDownload(context, updateInfo);
					ToastUtil.show(context, "开始下载");
				} else {
					UmengUpdateAgent.startInstall(context, file);
				}
				dismiss();
			}
		});
	}

}
