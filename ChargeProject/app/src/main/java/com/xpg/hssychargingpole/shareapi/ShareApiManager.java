package com.xpg.hssychargingpole.shareapi;

import android.content.Context;
import android.util.Log;

import com.easy.util.ToastUtil;
import com.xpg.hssy.db.pojo.LoginInfo;
import com.xpg.hssychargingpole.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by black-Gizwits on 2015/09/16.
 */
public class ShareApiManager {
	private static final int USER_TYPE_WECHAT = 3;
	private static final int USER_TYPE_QQ = 4;

	public static void oneKeyShareDownloadImage(Context context, String imageUrl) {
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(context.getString(R.string.app_name));
//		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(imageUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(context.getString(R.string.share_text));
		oks.setUrl(imageUrl);
		oks.setImageUrl(imageUrl);
		oks.show(context);
	}


	/**
	 * QQ和Qzone第三方登录
	 *
	 * @param context
	 */
	public static void qqLogin(Context context) {
		qqLogin(context, null);
	}

	public static void qqLogin(Context context, final Listener<LoginInfo> listener) {
		ShareSDK.initSDK(context);
		final Platform qzone = ShareSDK.getPlatform(context, QZone.NAME);
		qzone.removeAccount();
		qzone.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				Set<Map.Entry<String, Object>> entries = hashMap.entrySet();
//				for (Map.Entry entry : entries) {
//					Log.e("entry", entry.getKey().toString() + " : " + entry.getValue().toString());
//				}
				for (String str : hashMap.keySet()) {
					Log.e("entry", str + " : " + hashMap.get(str));
				}
				Log.e("userId", "userId: " + platform.getDb().getUserId());
				Log.e("userId", "Token: " + platform.getDb().getToken());
				Log.e("userId", "TokenSecret: " + platform.getDb().getTokenSecret());
				Object avater = hashMap.get("figureurl_qq_2");

				LoginInfo loginInfo = new LoginInfo();
				loginInfo.setGender(hashMap.get("gender").toString().equals("男") ? 1 : 2);
				loginInfo.setNickName(hashMap.get("nickname").toString());
				loginInfo.setToken(platform.getDb().getToken());
				loginInfo.setUserId(platform.getDb().getUserId());
				loginInfo.setUserAvaterUrl(avater == null ? "" : avater.toString());
				loginInfo.setUserType(USER_TYPE_QQ);

				if (listener != null) listener.onComplete(loginInfo);
			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {

			}

			@Override
			public void onCancel(Platform platform, int i) {

			}
		});
		qzone.showUser(null);
	}

	/**
	 * 微信第三方登录
	 *
	 * @param context
	 */

	public static void wechatLogin(final Context context) {
		wechatLogin(context, null);
	}

	public static void wechatLogin(final Context context, final Listener<LoginInfo> listener) {
		ShareSDK.initSDK(context);
		final Platform weChat = ShareSDK.getPlatform(context, Wechat.NAME);
		weChat.removeAccount();
		if (weChat.isAuthValid()) {
			String userId = weChat.getDb().getUserId();
			ToastUtil.show(context, userId);
		} else {
			weChat.setPlatformActionListener(new PlatformActionListener() {
				@Override
				public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
					for (String key : hashMap.keySet()) {
						Log.e("entry", key + " : " + hashMap.get(key));
					}
					Log.e("userId", "userId: " + platform.getDb().getUserId());
					Object avater = hashMap.get("headimgurl");

					LoginInfo loginInfo = new LoginInfo();
					loginInfo.setGender((Integer) hashMap.get("sex"));
					loginInfo.setNickName(hashMap.get("nickname").toString());
					loginInfo.setUserAvaterUrl(avater == null ? "" : avater.toString());
					loginInfo.setToken(platform.getDb().getToken());
					loginInfo.setUserId(platform.getDb().getUserId());
					loginInfo.setUserType(USER_TYPE_WECHAT);

					if (listener != null) {
						listener.onComplete(loginInfo);
					}
				}

				@Override
				public void onError(Platform platform, int i, Throwable throwable) {
				}

				@Override
				public void onCancel(Platform platform, int i) {
				}
			});
			weChat.removeAccount(true);
			weChat.SSOSetting(false);
			weChat.showUser(null);
		}
	}

	public interface Listener<T> {
		//授权成功
		void onComplete(T t);
	}
}
