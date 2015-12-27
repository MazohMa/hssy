package com.xpg.hssy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.easy.util.BadgeUtil;
import com.easy.util.SPFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xpg.hssy.bean.Message;
import com.xpg.hssy.main.activity.MyOrderDetailActivity;
import com.xpg.hssy.main.activity.TopicDetailActivity;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JpushReceiver";

	/**
	 * 接收到设备唯一标识码
	 *
	 * @param appkey
	 * @param pushId
	 */
	private void onReceivePushId(Context context, String appkey, String pushId) {
		Log.d(TAG, "[MyReceiver] 接收Registration Id : " + pushId);
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		if (sp.getBoolean("isLogin", false)) {
			String user_id = sp.getString("user_id", null);
			WebAPIManager.getInstance().bindPushId(user_id, pushId,1 , new WebResponseHandler<Object>() {

				@Override
				public void onError(Throwable e) {
					super.onError(e);
				}

				@Override
				public void onFailure(WebResponse<Object> response) {
					super.onFailure(response);
				}

				@Override
				public void onSuccess(WebResponse<Object> response) {
					super.onSuccess(response);
					Log.d(TAG, "[MyReceiver] 成功绑定");
				}

			});
		}

	}

	/**
	 * 透传消息
	 *
	 * @param context
	 * @param id
	 * @param title
	 * @param content
	 * @param extra
	 */
	private void onReceiveMessage(Context context, int id, String title, String content, String extra) {
		Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + content);
	}

	/**
	 * 接收到通知
	 *
	 * @param id
	 * @param title
	 * @param content
	 * @param extra   附加的json信息
	 */
	private void onReceiveNotification(final Context context, int id, String title, String content, String extra) {
		Log.d(TAG, "[MyReceiver] 接收到推送下来的通知: " + content);
		Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + id);
		// 显示未读消息数
		String userId = new SPFile(context, "config").getString("user_id", null);
		if (userId == null) {
			return;
		}
		WebAPIManager.getInstance().getUnreadMessageNum(userId, new WebResponseHandler<String>(context) {
			@Override
			public void onSuccess(WebResponse<String> response) {
				super.onSuccess(response);
				int unReadMessageNum = 0;
				try {
					unReadMessageNum = Integer.parseInt(response.getResult());
					BadgeUtil.setBadgeCount(context, unReadMessageNum);
				} catch (Exception e) {
				}

				Intent i = new Intent("com.xpg.hsy.updatemsgnum");
				i.putExtra("num", unReadMessageNum);
				context.sendBroadcast(i);
			}
		});
	}

	/**
	 * 点击通知
	 *
	 * @param id
	 * @param title
	 * @param content
	 * @param extra
	 */
	private void onClickNotification(Context context, int id, String title, String content, String extra) {
		Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

		// 解析json
		Map<String, String> extraMap = new Gson().fromJson(extra, new TypeToken<Map<String, String>>() {
		}.getType());
		String typeStr = extraMap.get("type");
		int type = -1;
		try {
			type = Integer.valueOf(typeStr);
		} catch (NumberFormatException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		}
		jumpActivityByMessageType(context, extraMap, type);
		// 设置消息为已读
		setMessageStatus(extraMap.get("noticeId"));
	}

	private void jumpActivityByMessageType(Context context, Map<String, String> extraMap, int type) {
		switch (type) {
			case Message.MESSAGE_TYPE_ORDER: {
				// 打开自定义的Activity
				Intent i = new Intent(context, MyOrderDetailActivity.class);
				String orderId = extraMap.get("typeId");
				if (orderId != null) {
					i.putExtra("orderId", orderId);
					// i.putExtras(bundle);
					// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
				}
				break;
			}
			case Message.MESSAGE_TYPE_CHARGE_TIME_LINE: {
				// 打开自定义的Activity
				Intent i = new Intent(context, TopicDetailActivity.class);
				String articleId = extraMap.get("typeId");
				if (articleId != null) {
					i.putExtra("articleId", articleId);
					// i.putExtras(bundle);
					// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
				}
				break;
			}
			case Message.MESSAGE_TYPE_CHARGE_START: {
				//不做处理
				break;
			}
			default: {
				break;
			}
		}
	}

	/**
	 * 接收到连接状态改变
	 *
	 * @param appkey
	 * @param connected
	 */
	private void onReceiveConnectionChange(Context context, String appkey, boolean connected) {
		Log.w(TAG, "[MyReceiver]" + " connected state change to: " + connected);
		// TODO
	}

	/**
	 * 接收到未知的广播
	 *
	 * @param intent
	 */
	private void onReceiveOther(Context context, Intent intent) {
		Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		// TODO
	}

	/**
	 * 分派接收到的所有广播
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		String appkey = bundle == null ? null : bundle.getString(JPushInterface.EXTRA_APP_KEY);
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			onReceivePushId(context, appkey, regId);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			int msgId = bundle.getInt(JPushInterface.EXTRA_MSG_ID);
			String title = bundle.getString(JPushInterface.EXTRA_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			onReceiveMessage(context, msgId, title, content, extra);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			onReceiveNotification(context, notifactionId, title, content, extra);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			onClickNotification(context, notifactionId, title, content, extra);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
			// TODO

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			onReceiveConnectionChange(context, appkey, connected);
		} else {
			onReceiveOther(context, intent);
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// send msg to MainActivity
	// private void processCustomMessage(Context context, Bundle bundle) {
	// if (MainActivity.isForeground) {
	// String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
	// String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	// Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
	// msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
	// if (!ExampleUtil.isEmpty(extras)) {
	// try {
	// JSONObject extraJson = new JSONObject(extras);
	// if (null != extraJson && extraJson.length() > 0) {
	// msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
	// }
	// } catch (JSONException e) {
	//
	// }
	//
	// }
	// context.sendBroadcast(msgIntent);
	// }
	// }

	private void setMessageStatus(String messageId) {

		WebAPIManager.getInstance().setMessageStatus(messageId, Message.HAVE_READ, new WebResponseHandler<Object>() {

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
			}
		});
	}
}
