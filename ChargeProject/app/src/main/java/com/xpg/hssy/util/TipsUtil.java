package com.xpg.hssy.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.easy.util.NetWorkUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.bean.TokenAndRefreshToken;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoginAgainDialog;
import com.xpg.hssy.dialog.LoginFailDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import org.apache.http.HttpException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TipsUtil {
	/**
	 * @param context
	 * @param e
	 * @param message
	 * @param resId
	 */
	private static User currentUser = null;

	private static void showTips(Context context, Throwable e, String message, int resId) {
		if (context == null) {
			return;
		}
		System.out.println(e.toString());
		if (!NetWorkUtil.isNetworkConnected(context)) {
			showToast(context, "网络连接不可用", message, resId);
		} else if (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException || e instanceof ConnectTimeoutException || e.getCause
				() instanceof ConnectTimeoutException) {
			showToast(context, "网络超时，请稍后再试", message, resId);
		} else if (e instanceof ConnectException || e.getCause() instanceof ConnectException || e instanceof FileNotFoundException || e.getCause() instanceof
				FileNotFoundException || e instanceof UnknownHostException || e.getCause() instanceof UnknownHostException || e instanceof HttpException || e
				.getCause() instanceof HttpException) {
			showToast(context, "服务器没有响应，请稍后再试", message, resId);
		} else {
			showToast(context, "未知错误，请稍后再试", message, resId);
		}
	}

	/**
	 * @param context
	 * @param e
	 * @param message
	 */
	public static void showTips(Context context, Throwable e, String message) {
		showTips(context, e, message, -1);
	}

	/**
	 * @param context
	 * @param e
	 * @param resId
	 */
	public static void showTips(Context context, Throwable e, int resId) {
		showTips(context, e, null, resId);
	}

	/**
	 * @param context
	 * @param e
	 */
	public static void showTips(Context context, Throwable e) {
		showTips(context, e, null, -1);
	}

	/**
	 * @param context
	 * @param response
	 * @param message
	 * @param resId
	 */
	private static void showTips(Context context, WebResponse response, String message, int resId) {
		if (context != null && context instanceof Activity && ((Activity) context).isFinishing()) {
			return;
		}
		switch (response.getCode()) {
//		case WebResponse.CODE_FAIL:
//			showToast(context, "服务器请求失败，请稍后再试", message, resId);
//			break;
			case WebResponse.CODE_REGISTER_FAIL:
				showToast(context, response.getMessage() + ",请重试", message, resId);
				break;
			case WebResponse.CODE_LOGIN_FAIL:
				showToast(context, response.getMessage() + ",请重试", message, resId);
				break;
			case WebResponse.CODE_ACCOUNT_EXISTED:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_ACCOUNT_NOT_EXISTED: {
				new LoginFailDialog(context).show();
				break;
			}
			case WebResponse.CODE_AUTHORIZE_FAIL:
				showToast(context, response.getMessage() + ",请重试", message, resId);
				break;
			case WebResponse.CODE_VALIDATION_CODE_ERROR:
				showToast(context, response.getMessage() + ",请重试", message, resId);
				break;
			case WebResponse.CODE_ILLEGAL_DATA:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_ERROR_DATA:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_NO_AUTH:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_HAD_AUTH:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_EMPTY_ACCOUNT:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_ILLEGAL_ACCOUNT_PASSWORD:
				showToast(context, "用户名或密码错误", message, resId);
				break;
			case WebResponse.CODE_REPEAT_RESERVATION_TIME:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_ORDER_TIME_OUT_CANNOT_AGREE:
				showToast(context, "订单已过期，您不能同意", message, resId);
				break;
			case WebResponse.CODE_ORDER_TIME_OUT_CANNOT_REJECT:
				showToast(context, "订单已过期，您不能拒绝", message, resId);
				break;
			case WebResponse.CODE_NULL_PAY_ACOUNT:
				showToast(context, "桩主未设支付宝账号", message, resId);
				break;
			case WebResponse.CODE_SYSTEM_ERROR:
				showToast(context, "服务器请求失败,请稍后再试", message, resId);
				break;
			case WebResponse.CODE_SYSTEM_BUSY:
				showToast(context, "服务器请求失败,请稍后再试", message, resId);
				break;
			case WebResponse.CODE_LOW_VERSION:
				UpdateUtil.showForceUpdateDialog(context);
				break;
			case WebResponse.CODE_FAMILY_ACCOUNT:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_INVALID_ORDER:
				showToast(context, response.getMessage(), message, resId);
				break;
			case WebResponse.CODE_TOKEN_EXPIRED:
				refreshToken(context);
				break;
//			case WebResponse.CODE_INVALID_TOKEN:
//				break;
			case WebResponse.CODE_PHONE_NUMBER_EXITS:
				ToastUtil.show(context,"该手机号已注册，请点击返回，直接登录");
				break;
			case "1001":
				showToast(context, "操作失败，请稍后再试", message, resId);
				break;
			case "0":
				showToast(context, "操作成功", message, resId);
				break;
			default:
				if (!showLoinAgain(context, response)) {
					// showToast(context, "错误代码：" + response.getCode() + "\n错误信息："
					// + response.getMessage(), message, resId);
					showToast(context, response.getMessage(), message, resId);
				}
				break;
		}
	}

	/**
	 * @param context
	 * @param response
	 * @param message
	 */
	public static void showTips(Context context, WebResponse response, String message) {
		showTips(context, response, message, -1);
	}

	/**
	 * @param context
	 * @param response
	 * @param resId
	 */
	public static void showTips(Context context, WebResponse response, int resId) {
		showTips(context, response, null, resId);
	}

	/**
	 * @param context
	 * @param response
	 */
	public static void showTips(Context context, WebResponse response) {
		showTips(context, response, null, -1);
	}

	/**
	 * @param context
	 * @param defaultMessage
	 * @param customMessage
	 * @param resId
	 */
	private static void showToast(Context context, String defaultMessage, String customMessage, int resId) {
		if (customMessage != null) {
			ToastUtil.show(context, customMessage);
		} else if (resId != -1) {
			ToastUtil.show(context, resId);
		} else {
			ToastUtil.show(context, defaultMessage);
		}
	}

	/**
	 * @param context
	 */
	private static void loginAgain(Context context, String msg) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return;
		}
		LoginAgainDialog loginAgainDialog = LoginAgainDialog.getInstance(context);
		loginAgainDialog.setTitle(R.string.message_charge_title_tip);
		loginAgainDialog.setContent(msg);
		loginAgainDialog.setRightBtnText("重新登录");
		loginAgainDialog.setLeftBtnText("忽略");
		loginAgainDialog.initBroadcast();
		// 不知道为什么，判断了是否finish还是会报错
		// android.view.WindowManager$BadTokenException:Unable to add window --
		// token android.os.BinderProxy@42ab9560 is not valid; is your activity
		// running?
		try {
			loginAgainDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param context
	 * @param response
	 * @return
	 */
	public static boolean showLoinAgain(Context context, WebResponse response) {
		switch (response.getCode()) {
			case WebResponse.CODE_INVALID_TOKEN:
				loginAgain(context,context.getString(R.string.other_deivce_login));
				return true;
			case WebResponse.CODE_ERROR_CLIENTID:
				loginAgain(context, "请重新登录");
				return true;
			case WebResponse.CODE_INVALID_USERID:
				loginAgain(context, "请重新登录");
				return true;
			case WebResponse.CODE_INVALID_RETOKEN:
				loginAgain(context, "请重新登录");
				return true;
			case WebResponse.CODE_INVALID_CLIENTID:
				loginAgain(context, "请重新登录");
				return true;
			default:
				return false;
		}
	}

	private static void refreshToken(final Context context) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
		String user_id = sp.getString("user_id", "");
		if (!user_id.equals("")) {
			currentUser = DbHelper.getInstance(context).getUserDao().load(user_id);
			WebAPIManager.getInstance().refreshToken(currentUser.getUserid(), currentUser.getRefreshToken(), new WebResponseHandler<TokenAndRefreshToken>() {
				@Override
				public void onError(Throwable e) {
					super.onError(e);
					TipsUtil.showTips(context, e);
				}

				@Override
				public void onFailure(WebResponse<TokenAndRefreshToken> response) {
					super.onFailure(response);
					TipsUtil.showTips(context, response);
					Intent login_intent = new Intent(context, LoginActivity.class);
					context.startActivity(login_intent);
				}

				@Override
				public void onSuccess(WebResponse<TokenAndRefreshToken> response) {
					super.onSuccess(response);
					TokenAndRefreshToken tokenAndRefreshToken = response.getResultObj();
					WebAPIManager.getInstance().setAccessToken(tokenAndRefreshToken.getToken()); // WebAPIManager中保存token，后台需要用到
					currentUser.setToken(tokenAndRefreshToken.getToken());
					currentUser.setRefreshToken(tokenAndRefreshToken.getRefreshToken());
					DbHelper.getInstance(context).getUserDao().update(currentUser); // 数据库中更新user，方便以后查询
				}
			});
		}
	}
}
