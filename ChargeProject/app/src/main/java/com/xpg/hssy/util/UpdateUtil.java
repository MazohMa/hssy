package com.xpg.hssy.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.easy.manager.EasyActivityManager;
import com.easy.util.ToastUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.UpdateInfoDialog;
import com.xpg.hssy.dialog.WaterBlueTitleDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.MyApplication;
import com.xpg.hssychargingpole.R;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年4月20日
 */

public class UpdateUtil {
	private UpdateUtil() {
	}

	/**
	 * 每次进入app，先访问服务器判断是否需要强制更新，如果不需要则调用自动检查更新
	 */
	public static void firstUpdate(final Context context) {
		MyApplication.getInstance().setFirstUpdated(true);
		// 检查更新
		// TODO 暂时没有单独接口检查是否必须更新，先随便调用一个接口
		WebAPIManager.getInstance().getPileById("-1", new WebResponseHandler<Pile>(context) {
					@Override
					public void onFailure(WebResponse<Pile> response) {
						super.onFailure(response);
						// 需要强制更新
						if (WebResponse.CODE_LOW_VERSION.equals(response.getCode())) {
							UpdateUtil.showForceUpdateDialog(context);
						}
					}

					@Override
					public void onFinish() {
						super.onFinish();
						// 如果不是需要强制更新，则自动检查更新
						if (!MyApplication.getInstance().isShowingForceUpdateDialog()) {
							UpdateUtil.autoUpdate(context);
						}
					}
				});
	}

	/**
	 * 自动检查更新
	 */
	public static void autoUpdate(final Context context) {
		// LogUtil.e0("autoUpdate");
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (context instanceof Activity && ((Activity) context).isFinishing()) {
					return;
				}
				switch (updateStatus) {
					case UpdateStatus.Yes: // has update
						// LogUtil.e0("autoUpdate: Yes");
						showUpdateDialog(context, updateInfo);
						break;
					case UpdateStatus.No: // has no update
						// LogUtil.e0("autoUpdate: No");
						break;
					case UpdateStatus.NoneWifi: // none wifi
						// LogUtil.e0("autoUpdate: None Wifi");
						break;
					case UpdateStatus.Timeout: // time out
						// LogUtil.e0("autoUpdate: Timeout");
						break;
				}
			}
		});
		UmengUpdateAgent.update(context);
	}

	/**
	 * 手动检查更新
	 */
	public static void manualUpdate(final Context context) {
		// LogUtil.e0("manualUpdate");
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (context instanceof Activity && ((Activity) context).isFinishing()) {
					return;
				}
				switch (updateStatus) {
					case UpdateStatus.Yes: // has update
						// LogUtil.e0("manualUpdate: Yes");

						showUpdateDialog(context, updateInfo);
						break;
					case UpdateStatus.No: // has no update
						// LogUtil.e0("manualUpdate: No");
						ToastUtil.show(context, context.getString(R.string.system_setting_version_last));
						break;
					case UpdateStatus.NoneWifi: // none wifi
						// LogUtil.e0("manualUpdate: None Wifi");
						// ToastUtil.show(instance, "只在Wifi下更新");
						break;
					case UpdateStatus.Timeout: // time out
						// LogUtil.e0("manualUpdate: Timeout");
						// ToastUtil.show(instance, "检查更新超时");
						break;
				}
			}
		});
		UmengUpdateAgent.forceUpdate(context);
	}

	/**
	 * 手动检查更新
	 */
	public static void manualUpdate(final Context context, final UmengUpdateListener listener) {
		// LogUtil.e0("manualUpdate");
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (context instanceof Activity && ((Activity) context).isFinishing()) {
					return;
				}
				switch (updateStatus) {
					case UpdateStatus.Yes: // has update
						// LogUtil.e0("manualUpdate: Yes");

						showUpdateDialog(context, updateInfo);
						break;
					case UpdateStatus.No: // has no update
						// LogUtil.e0("manualUpdate: No");
						ToastUtil.show(context, context.getString(R.string.system_setting_version_last));
						break;
					case UpdateStatus.NoneWifi: // none wifi
						// LogUtil.e0("manualUpdate: None Wifi");
						// ToastUtil.show(instance, "只在Wifi下更新");
						break;
					case UpdateStatus.Timeout: // time out
						// LogUtil.e0("manualUpdate: Timeout");
						// ToastUtil.show(instance, "检查更新超时");
						break;
				}
				//执行完默认监听器后再执行自定义的监听器
				if (listener != null) {
					listener.onUpdateReturned(updateStatus, updateInfo);
				}
			}
		});

		UmengUpdateAgent.forceUpdate(context);
	}

	/**
	 * 显示更新提示窗口
	 */
	public static void showUpdateDialog(final Context context, final UpdateResponse updateInfo) {
		new UpdateInfoDialog(context, updateInfo).show();
	}

	/**
	 * App版本过低的时候不能使用App，提示强制更新
	 */
	public static void showForceUpdateDialog(final Context context) {
		// LogUtil.e0("showForceUpdateDialog");
		if (MyApplication.getInstance().isShowingForceUpdateDialog()) {
			return;
		}
		MyApplication.getInstance().setShowingForceUpdateDialog(true);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (context instanceof Activity && ((Activity) context).isFinishing()) {
					return;
				}
				switch (updateStatus) {
					case UpdateStatus.Yes: // has update
						// LogUtil.e0("forceUpdate: Yes");
						// File file = UmengUpdateAgent.downloadedFile(instance,
						// updateInfo);
						// if (file == null) {
						// UmengUpdateAgent.startDownload(instance, updateInfo);
						// } else {
						// UmengUpdateAgent.startInstall(instance, file);
						// }
						showUpdateDialog(context, updateInfo);
						break;
					case UpdateStatus.No: // has no update
						// LogUtil.e0("forceUpdate: No");
						ToastUtil.show(context, "已经是最新版本");
						break;
					case UpdateStatus.NoneWifi: // none wifi
						// LogUtil.e0("forceUpdate: None Wifi");
						// ToastUtil.show(instance, "只在Wifi下更新");
						break;
					case UpdateStatus.Timeout: // time out
						// LogUtil.e0("forceUpdate: Timeout");
						// ToastUtil.show(instance, "检查更新超时");
						break;
				}
			}
		});
		final WaterBlueTitleDialog wd = new WaterBlueTitleDialog(context);
		wd.setCancelable(false);
		wd.setTitle("版本过低");
		wd.setContent("软件版本过低！\n请获取最新版本！");
		wd.setLeftBtnText("退出应用");
		wd.setLeftListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				wd.dismiss();
				EasyActivityManager.getInstance().finishAll();
				MyApplication.getInstance().exit();
			}
		});
		wd.setRightBtnText("获取新版");
		wd.setRightListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UmengUpdateAgent.forceUpdate(context);
				// JSONObject jo = new JSONObject();
				// try {
				// jo.put("update", "Yes");
				// jo.put("update_log",
				// "1、增加了XXX功能\n2、修复了XXX个Bug\n3、改善用户体验\n4、更多");
				// jo.put("version", "2.0.0");
				// jo.put("target_size", "1.5Mb");
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				// showUpdateDialog(context, new UpdateResponse(jo));
			}
		});
		wd.show();
	}

}
