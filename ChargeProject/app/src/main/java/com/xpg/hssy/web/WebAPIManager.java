package com.xpg.hssy.web;

import android.text.TextUtils;
import android.util.Log;

import com.easy.util.EmptyUtil;
import com.easy.util.LogUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xpg.hssy.bean.Article;
import com.xpg.hssy.bean.CongruentPoint;
import com.xpg.hssy.bean.CtrlRes;
import com.xpg.hssy.bean.DateBookTime;
import com.xpg.hssy.bean.DynamicInfo;
import com.xpg.hssy.bean.Message;
import com.xpg.hssy.bean.PileAuthentication;
import com.xpg.hssy.bean.Price;
import com.xpg.hssy.bean.RealTimeStatus;
import com.xpg.hssy.bean.ThemeTopic;
import com.xpg.hssy.bean.TokenAndRefreshToken;
import com.xpg.hssy.bean.searchconditon.CenterPointSearchCondition;
import com.xpg.hssy.bean.searchconditon.RectangleSearchCondition;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.Alipay;
import com.xpg.hssy.db.pojo.BaifubaoParam;
import com.xpg.hssy.db.pojo.BeecloudPaymentParam;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.ChargeRecordCache;
import com.xpg.hssy.db.pojo.Command;
import com.xpg.hssy.db.pojo.ExpenseRecord;
import com.xpg.hssy.db.pojo.Favorite;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.PaymentParam;
import com.xpg.hssy.db.pojo.PaymentParams;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileList;
import com.xpg.hssy.db.pojo.PileScore;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.db.pojo.PileStatus;
import com.xpg.hssy.db.pojo.RechargeRecord;
import com.xpg.hssy.db.pojo.ReturnAlipay;
import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.web.parser.AlipayParser;
import com.xpg.hssy.web.parser.ArticleArrayParser;
import com.xpg.hssy.web.parser.ArticleParser;
import com.xpg.hssy.web.parser.BaifubaoParamParser;
import com.xpg.hssy.web.parser.BeecloudPaymentParamParser;
import com.xpg.hssy.web.parser.ChargeRecordArrayParser;
import com.xpg.hssy.web.parser.ChargeRecordParser;
import com.xpg.hssy.web.parser.CommandArrayParser;
import com.xpg.hssy.web.parser.CongruentPointArrayParser;
import com.xpg.hssy.web.parser.CtrlResParser;
import com.xpg.hssy.web.parser.DynamicInfoParser;
import com.xpg.hssy.web.parser.ExpenseRecordsParser;
import com.xpg.hssy.web.parser.IsFavorParser;
import com.xpg.hssy.web.parser.KeyArrayParser;
import com.xpg.hssy.web.parser.MessageArraParser;
import com.xpg.hssy.web.parser.DKeyValueMapParser;
import com.xpg.hssy.web.parser.OrderArrayParser;
import com.xpg.hssy.web.parser.OrderParser;
import com.xpg.hssy.web.parser.PaymentParamParser;
import com.xpg.hssy.web.parser.PaymentParamsParser;
import com.xpg.hssy.web.parser.PileArrayParser;
import com.xpg.hssy.web.parser.PileAuthenticationParser;
import com.xpg.hssy.web.parser.PileListParser;
import com.xpg.hssy.web.parser.PileParser;
import com.xpg.hssy.web.parser.PileScoreParser;
import com.xpg.hssy.web.parser.PileStateArrayParser;
import com.xpg.hssy.web.parser.PileStationParser;
import com.xpg.hssy.web.parser.PriceParser;
import com.xpg.hssy.web.parser.RealTimeStatusParser;
import com.xpg.hssy.web.parser.RechargeRecordsParser;
import com.xpg.hssy.web.parser.RefreshTokenParser;
import com.xpg.hssy.web.parser.ReturnAlipayParser;
import com.xpg.hssy.web.parser.TimeSplitResultListParser;
import com.xpg.hssy.web.parser.UserArrayParser;
import com.xpg.hssy.web.parser.UserExistParser;
import com.xpg.hssy.web.parser.UserParser;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebAPIManager
 *
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2014年7月8日
 */

public class WebAPIManager {
	public static final String tag = "WebAPIManager";

	private volatile static WebAPIManager webAPIManager;
	private String accessToken = "";
	private String clientId = "1";
	private String appVersion = "";
	/**
	 * 格式化log
	 */
	private boolean isDetail = true;

	private WebAPIManager() {
	}

	public static WebAPIManager getInstance() {
		if (webAPIManager == null) {
			synchronized (WebAPIManager.class) {
				if (webAPIManager == null) {
					webAPIManager = new WebAPIManager();
				}
			}
		}
		return webAPIManager;
	}

	/**
	 * 全部头都加入clientId和appVersion device和os
	 */
	private Map<String, Object> getDefautlHeader() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(WebAPI.KEY_CLIENT_ID, clientId);
		headers.put(WebAPI.KEY_APP_VERSION, appVersion);
		headers.put(WebAPI.KEY_OS, "Android " + android.os.Build.VERSION.RELEASE);
		headers.put(WebAPI.KEY_DEVICE, android.os.Build.MODEL);
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		return headers;
	}

	/*******************************************
	 * user api
	 **************************************/

	// 登录
	public void login(int usertype, String phone, String password, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_LOGIN;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_TYPE, usertype);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_PASSWORD, password);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 登录
	public void login(int usertype, String phone, String password, String pushId, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_LOGIN;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_TYPE, usertype);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_PASSWORD, password);
		params.put(WebAPI.KEY_PUSH_ID, pushId);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 登录
	public void login(String account, int usertype, String phone, String password, String pushId, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_LOGIN;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<>();
		params.put(WebAPI.KEY_ACCOUNT, account);
		params.put(WebAPI.KEY_USER_TYPE, usertype);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_PASSWORD, password);
		params.put(WebAPI.KEY_PUSH_ID, pushId);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 获取验证码
	public void getRegisterVerifyCode(String phone, WebResponseHandler<Object> handler) {
		getVerifyCode(phone, 1, handler);
	}

	public void getForgetPwdVerifyCode(String phone, WebResponseHandler<Object> handler) {
		getVerifyCode(phone, 2, handler);
	}

	private void getVerifyCode(String phone, int state, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_VERIFY;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_MOBILE, phone);
		params.put(WebAPI.KEY_STATE, state);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	public void checkVerifyCode(String phoneNo, String checkcode, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_CHECKCODE;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PHONENO, phoneNo);
		params.put(WebAPI.KEY_CHECKCODE, checkcode);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	// 注册
	public void register(String verifyCode, int userType, String phone, String password, String name, int gender, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_REGISTER;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_CHECK_CODE, verifyCode);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_TYPE, userType);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_PASSWORD, password);
		params.put(WebAPI.KEY_NAME, name);
		params.put(WebAPI.KEY_GENDER, gender);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	public void registerThirdAcc(String thirdAccToken, String checkCode, String thirdUid, int userType, String phone, String password, String name, int
			gender, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_REGISTER;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_CHECK_CODE, checkCode);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_TYPE, userType);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_PASSWORD, password);
		params.put(WebAPI.KEY_NAME, name);
		params.put(WebAPI.KEY_GENDER, gender);
		params.put(WebAPI.KEY_THIRD_ACC_TOKEN, thirdAccToken);
		params.put(WebAPI.KEY_THIRD_ACC_USER_ID, thirdUid);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 重置密码
	public void resetPassword(String verifyCode, int usertype, String phone, String password, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_RESET_PASSWORD;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_CHECK_CODE, verifyCode);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_TYPE, usertype);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_PASSWORD, password);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	// 注销
	public void logout(int usertype, String phone, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_LOGOUT;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_TYPE, usertype);
		params.put(WebAPI.KEY_PHONE, phone);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 修改个人信息
	public void modifyUserInfo(String userid, String name, Integer gender, String avatar, String alipayName, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_MODIFY;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_NAME, name);
		params.put(WebAPI.KEY_GENDER, gender);
		params.put(WebAPI.KEY_AVATAR, avatar);
		params.put(WebAPI.KEY_ALIPAY_NAME, alipayName);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	/**
	 * 修改个人信息-头像
	 *
	 * @param userid
	 * @param handler
	 */
	public void modifyUserInfoForAvatar(String userid, Map<String, Object> fileKeyAndPath, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_UPLOAD;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, fileKeyAndPath, handler, new UserParser(), false);
	}

	// 修改个人信息-昵称
	public void modifyUserInfoForName(String userid, String name, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_MODIFY;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_NAME, name);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 修改个人信息-性别
	public void modifyUserInfoForSex(String userid, Integer gender, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_MODIFY;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_GENDER, gender);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 修改个人信息-支付宝
	public void modifyUserInfoForalipayName(String userid, String alipayName, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_MODIFY;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_ALIPAY_NAME, alipayName);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	public void setPrice(String price, WebResponseHandler<Price> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_SET_PRICE;
//		url = url.replaceFirst("\\{price\\}", price);
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PRICE, price);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new PriceParser());
	}

	// 修改个人信息-修改密码
	public void modifyUserInfoForPwd(String userid, String currentPwd, String newPwd, String comfirmPwd, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_MODIFYPASSWORD;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_CURRENTPWD, currentPwd);
		params.put(WebAPI.KEY_NEWPWD, newPwd);
		params.put(WebAPI.KEY_COMFIRMPWD, comfirmPwd);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 检查用户是否存在
	public void userExist(String phone, WebResponseHandler<Boolean> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_EXIST;
		url = url.replaceFirst("\\{phone\\}", phone);

		Map<String, Object> headers = getDefautlHeader();

		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new UserExistParser());
	}

	// 获得支付宝账号
	public void getAlipayName(String userid, WebResponseHandler<Alipay> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ALIPAYNAME_GET;
		url = url.replaceFirst("\\{userid\\}", userid);
		Map<String, Object> headers = getDefautlHeader();
		startExcute(WebAPI.METHOD_GET, url, headers, null, handler, new AlipayParser());
	}

	// 获得支付宝支付请求参数
	public void requestPayMoney(String id, WebResponseHandler<PaymentParam> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PAY_MONEY;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, new PaymentParamParser());
	}

	// 获得支付宝支付请求参数
	public void requestPayMoney2(String id, WebResponseHandler<PaymentParams> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PAY_MONEY;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, new PaymentParamsParser());
	}

	// 获得支付宝支付请求参数
	public void requestPayMoneyForBeecloud(String id, float currencyCount, WebResponseHandler<BeecloudPaymentParam> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PAY_MONEY_BEECLOUD;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("currencyCount", currencyCount);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, new BeecloudPaymentParamParser());
	}

	// 获得百度钱包支付签约请求参数
	public void requestPayMoneyForBaifubao(String id, WebResponseHandler<BaifubaoParam> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PAY_MONEY_BAIFUBAO;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, new BaifubaoParamParser());
	}

	// 获得用户是否签约百付宝代扣服务
	public void requestHasBaifubaoContract(String userid, WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_USER_HAS_BAIFUBAO_CONTRACT + userid;
		Map<String, Object> headers = getDefautlHeader();
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("userId", userid);
		startExcute(WebAPI.METHOD_GET, url, headers, null, handler, null);
	}

	// 解除百付宝签约代扣服务
	public void cancelBaifubaoContract(String userid, WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_CANCEL_BAIFUBAO_CONTRACT + userid;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("userId", userid);
		startExcute(WebAPI.METHOD_GET, url, headers, null, handler, null);
	}

	// 解除订单支付锁定
	public void cancelPayLock(String id, WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_CANCEL_PAY_LOCK;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, null);
	}

	// 获取充电币余额
	public void getRemainMoney(String userid, WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_GET_REMAIN_MONEY + userid;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put(WebAPI.KEY_USER_ID, userid);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, null);
	}

	// 充电币充值
	public void recharge(String userid, float currencyMoney, WebResponseHandler<BeecloudPaymentParam> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_RECHARGE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_RECHARGE_Money, currencyMoney);
		startExcute(WebAPI.METHOD_GET, url, headers, params, handler, new BeecloudPaymentParamParser());
	}

	// 获取充电币充值记录
	public void getRechargeRecords(String userid, int start, int count, WebResponseHandler<List<RechargeRecord>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_GET_RECHARGE_RECORDS;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new RechargeRecordsParser());
	}


	// 获取充电币支付记录
	public void getExpenseRecords(String userid, int start, int count, WebResponseHandler<List<ExpenseRecord>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_GET_EXPENSE_RECORDS;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new ExpenseRecordsParser());
	}


	// 支付宝支付请求 置废
	public void requesrAlipay(String userId, String sellerEmail, String subject, String outTradeNo, String totalFee, String returnUrl, String merchantUrl,
	                          String notifyUrl, int isMobile, String remark, String sign, WebResponseHandler<ReturnAlipay> handler) {
		String url = WebAPI.BASE_ALIPAY_URL;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put(WebAPI.KEY_ISMOBILE, isMobile);
		params.put(WebAPI.KEY_MERCHANTURL, merchantUrl);
		params.put(WebAPI.KEY_NOTIFYURL, notifyUrl);
		params.put(WebAPI.KEY_OUTTRADENO, outTradeNo);
		params.put(WebAPI.KEY_REMARK, remark);
		params.put(WebAPI.KEY_RETURNURL, returnUrl);
		params.put(WebAPI.KEY_SELLEREMAIL_ID, sellerEmail);
		params.put(WebAPI.KEY_SIGN, sign);
		params.put(WebAPI.KEY_SUBJECT, subject);
		params.put(WebAPI.KEY_TOTALFEE, totalFee);

		startExcute(WebAPI.METHOD_POST, url, null, params, handler, new ReturnAlipayParser());
	}

	// 支付宝支付请求
	public void requesrAlipays(String sellerEmail, String subject, String outTradeNo, double totalFee, String returnUrl, String merchantUrl, String notifyUrl,
	                           int isMobile, String remark, String channel, String sign, WebResponseHandler<ReturnAlipay> handler) {
		String url = WebAPI.BASE_ALIPAY_URL;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_ISMOBILE, isMobile);
		params.put(WebAPI.KEY_MERCHANTURL, merchantUrl);
		params.put(WebAPI.KEY_NOTIFYURL, notifyUrl);
		params.put(WebAPI.KEY_OUTTRADENO, outTradeNo);
		params.put(WebAPI.KEY_REMARK, remark);
		params.put(WebAPI.KEY_RETURNURL, returnUrl);
		params.put(WebAPI.KEY_SELLEREMAIL_ID, sellerEmail);
		params.put(WebAPI.KEY_SIGN, sign);
		params.put(WebAPI.KEY_SUBJECT, subject);
		params.put(WebAPI.KEY_TOTALFEE, totalFee);
		params.put(WebAPI.KEY_CHANNEL, channel);

		startExcute(WebAPI.METHOD_POST, url, null, params, handler, new ReturnAlipayParser());
	}

	/******************************************* user api **************************************/

	/*******************************************
	 * pile api
	 **************************************/

	// 找桩

	// 刷新token
	public void refreshToken(String userid, String refreshToken, WebResponseHandler<TokenAndRefreshToken> handler) {
		if (userid == null || refreshToken == null) return;
		String url = WebAPI.BASE_URL + WebAPI.API_REFRESH_TOKEN;
		url = url.replaceFirst("\\{userid\\}", userid);
		url = url.replaceFirst("\\{refreshToken\\}", refreshToken);

		Map<String, Object> headers = getDefautlHeader();

		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new RefreshTokenParser());
	}
// 找桩

	/**
	 * @param condition
	 * @param start
	 * @param count
	 * @param handler
	 */
	public void findPiles(String userid, CenterPointSearchCondition condition, int start, int count, WebResponseHandler<List<Pile>> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET_PUBLIC;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		LogUtil.e("WebApi", "pileList:userid：" + userid);
		// 必填参数
		if (condition.getCityId() != null && condition.getCenterLatitude() > 0 && condition.getCenterLongitude() > 0 && condition.getDistance() > 0) {
			// 中心点+距离的方式查找
			params.put(WebAPI.KEY_CITY_ID, condition.getCityId());
			if (EmptyUtil.notEmpty(userid)) {
				params.put(WebAPI.KEY_USER_ID, userid);
			}
			params.put(WebAPI.KEY_LONGITUDE, condition.getCenterLongitude());
			params.put(WebAPI.KEY_LATITUDE, condition.getCenterLatitude());
			params.put(WebAPI.KEY_DISTANCE, condition.getDistance());
		} else {
//		 throw new RuntimeException("find pile params too less");
		}
		// 是否空闲
		params.put(WebAPI.KEY_IS_LDLE, condition.getIsIdle());
		// 关键字
		if (condition.getSearch() != null && !condition.getSearch().equals("")) {
			params.put(WebAPI.KEY_SEARCH, condition.getSearch());
		}

		// 排序
		if (condition.getSortType() >= 0) {
			params.put(WebAPI.KEY_SORT, condition.getSortType());
		}
		// 电流类型
		if (condition.getPileTypes() != null && condition.getPileTypes().size() > 0) {
			StringBuilder operators = new StringBuilder();
			for (Integer operator : condition.getPileTypes()) {
				operators.append(operator + ",");
			}
			operators.deleteCharAt(operators.length() - 1);
			params.put(WebAPI.KEY_PILE_TYPE, operators);
		}
		// 拥有者类型
		// if (condition.getOperatorTypes() != null
		// && condition.getOperatorTypes().length > 0) {
		// StringBuilder operators = new StringBuilder();
		// for (int operator : condition.getOperatorTypes()) {
		// operators.append(operator + ",");
		// }
		// operators.deleteCharAt(operators.length() - 1);
		// params.put(WebAPI.KEY_OPERATOR, operators);
		// }
		// 充电时间
		if (condition.getStartTime() > 0) {
			params.put(WebAPI.KEY_START_TIME, condition.getStartTime());

			if (condition.getDuration() > 0) {
				params.put(WebAPI.KEY_DURATION, condition.getDuration());
			}
			if (condition.getEndTime() > 0) {
				params.put(WebAPI.KEY_END_TIME, condition.getEndTime());
			}
		}

		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);

		JSONObject json = new JSONObject(params);
		LogUtil.e("WebApi", "pileList:" + json.toString());

		// 新接口中包装了一层数据所以要用PileStateArrayParser解释器解释
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileStateArrayParser());
	}

	/**
	 * @param start
	 * @param count
	 * @param handler
	 */
	public void searchPiles(String search, int start, int count, WebResponseHandler<List<Pile>> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET_PUBLIC;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<>();
		// 关键字
		if (!TextUtils.isEmpty(search)) {
			params.put(WebAPI.KEY_SEARCH, search);
		}

		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);

		// 新接口中包装了一层数据所以要用PileStateArrayParser解释器解释
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileStateArrayParser());
	}

	/**
	 * 向服务器请求特定的时间段分片
	 *
	 * @param startDate
	 * @param stime
	 * @param etime
	 * @param duration
	 * @param pileId
	 * @param dateCount
	 * @param handler
	 */
	public void loadPileTimeSplit(long startDate, long stime, long etime, double duration, String pileId, int dateCount,
	                              WebResponseHandler<List<DateBookTime>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_TIME_SPLIT;
		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_START_DATE, startDate);
		if (stime > -1) {
			params.put(WebAPI.KEY_STIME, stime);
		}
		if (etime > -1) {
			params.put(WebAPI.KEY_ETIME, etime);
		}
		if (duration > -1) {
			params.put(WebAPI.KEY_DURATION, duration);
		}
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_DATE_COUNT, dateCount);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new TimeSplitResultListParser());
	}

	public void loadPileTimeSplit(long startDate, double duration, String pileId, int dateCount, WebResponseHandler<List<DateBookTime>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_TIME_SPLIT;
		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_START_DATE, startDate);
		if (duration > -1) {
			params.put(WebAPI.KEY_DURATION, duration);
		}
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_DATE_COUNT, dateCount);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new TimeSplitResultListParser());
	}

	/**
	 * 已废弃 Joke 20150331
	 */
	// 获取大家已分享的桩
	@Deprecated
	public void getPublicPiles(String search, Double longitude, Double latitude, String cityId, Long distance, Integer type, String shareStartTime, String
			shareEndTime, int start, int count, WebResponseHandler<List<Pile>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET_PUBLIC;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_SEARCH, search);
		params.put(WebAPI.KEY_LONGITUDE, longitude);
		params.put(WebAPI.KEY_LATITUDE, latitude);
		params.put(WebAPI.KEY_CITY_ID, cityId);
		params.put(WebAPI.KEY_DISTANCE, distance);
		params.put(WebAPI.KEY_TYPE, type);
		params.put(WebAPI.KEY_SHARE_START_TIME, shareStartTime);
		params.put(WebAPI.KEY_SHARE_END_TIME, shareEndTime);
		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);
		// 新接口中包装了一层数据所以要用PileStateArrayParser解释器解释
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileStateArrayParser());
	}

	// 获取某个桩
	public void getFavorById(String userId, String pileId, WebResponseHandler<Boolean> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_ISCOLLECTED;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userId);
		params.put(WebAPI.KEY_PILE_ID, pileId);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new IsFavorParser());
	}

	// 获取某个桩
	public void getPileById(String pileId, WebResponseHandler<Pile> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileParser());
	}

	// 获取公共站
	public void getPileStationById(String stationId, String userId, WebResponseHandler<PileStation> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_STATION_GET;
		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_STATION_ID, stationId);
		if (EmptyUtil.notEmpty(userId)) {
			headers.put(WebAPI.KEY_TOKEN, accessToken);
			params.put(WebAPI.KEY_USER_ID, userId);
		}

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileStationParser());
	}

	//刷新站内桩状态
	public void getPileStateById(String stationId, WebResponseHandler<PileList> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_REFRESH_PILE_STATE;
		url = url.replaceFirst("\\{stationId\\}", stationId);
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new PileListParser());
	}

	// 收藏电桩
	public void addFavoritesPile(String userid, String pileId, WebResponseHandler<Object> handler) {
		setFavoritesPile(userid, pileId, 1, handler);
	}

	public void removeFavoritesPile(String userid, String pileId, WebResponseHandler<Object> handler) {
		setFavoritesPile(userid, pileId, 2, handler);
	}

	private void setFavoritesPile(String userid, String pileId, int favor, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_SET_FAVORITES;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_FAVOR, favor);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	// 获取收藏的桩
	public void getFavoritesPiles(String userid, int start, int count, WebResponseHandler<List<Pile>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET_FAVORITES;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileArrayParser());
	}

	// 取消收藏的桩
	public void cancelFavoritesPiles(List<Favorite> favorites, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_CANCEL_FAVORITES;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("favorites", favorites);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	// 获取拥有的桩
	public void getOwnPile(String userid, WebResponseHandler<List<Pile>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET_OWN;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileArrayParser());
	}

	// 修改桩信息
	public void modifyPile(Pile pile, WebResponseHandler handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_MODIFY;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pile.getPileId());
		params.put(WebAPI.KEY_PILE_NAME, pile.getPileName());
		params.put(WebAPI.KEY_PILE_DESP, pile.getDesp());
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);

	}

	// 获取家人
	public void getFamily(String pileId, WebResponseHandler<List<User>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GET_FAMILY;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserArrayParser());

	}

	// 授权给家人
	public void addFamily(String pileId, String phone, WebResponseHandler<User> handler) {
		setFamily(pileId, phone, 1, handler);
	}

	public void removeFamily(String pileId, String phone, WebResponseHandler<User> handler) {
		setFamily(pileId, phone, 0, handler);
	}

	private void setFamily(String pileId, String phone, int auth, WebResponseHandler<User> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_AUTH;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_PHONE, phone);
		params.put(WebAPI.KEY_AUTH, auth);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new UserParser());
	}

	// 设置分享
	public void share(String pileId, List<ShareTime> shareTimes, String contactName, String contactPhone, WebResponseHandler<Pile> handler) {
		setShare(pileId, shareTimes, 1, contactName, contactPhone, handler);
	}

	public void unshare(String pileId, String contactName, String contactPhone, WebResponseHandler<Pile> handler) {
		setShare(pileId, new ArrayList<ShareTime>(), 0, contactName, contactPhone, handler);
	}

	private void setShare(String pileId, List<ShareTime> shareTimes, int share, String contactName, String contactPhone, WebResponseHandler<Pile> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_SHARE;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		if (shareTimes != null) {
			List<Map<String, Object>> shareTimesList = new ArrayList<Map<String, Object>>();
			for (ShareTime shareTime : shareTimes) {
				Map<String, Object> shareTimeMap = new HashMap<String, Object>();
				shareTimeMap.put(WebAPI.KEY_START_TIME, shareTime.getStartTime());
				shareTimeMap.put(WebAPI.KEY_END_TIME, shareTime.getEndTime());
				shareTimeMap.put(WebAPI.KEY_WEEK, shareTime.getWeek());
				shareTimeMap.put(WebAPI.KEY_SERVICE_PAY, shareTime.getServicePay());
				shareTimesList.add(shareTimeMap);
			}
			params.put(WebAPI.KEY_PILE_SHARES, shareTimesList);
		}
		params.put(WebAPI.KEY_SHARE, share);
		params.put(WebAPI.KEY_CONTACT_NAME, contactName);
		params.put(WebAPI.KEY_CONTACT_PHONE, contactPhone);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new PileParser());
	}

	// 下订单
	public void addOrder(String pileId, String userid, long startTime, long endTime, WebResponseHandler<ChargeOrder> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_ADD;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_START_TIME, startTime);
		params.put(WebAPI.KEY_END_TIME, endTime);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new OrderParser());
	}

	// 获取订单
	public void getMyOrder(String userid, String search, List<Integer> actionTypes, int start, int count, int startOffset, int endOffset,
	                       WebResponseHandler<List<ChargeOrder>> handler) {
		getOrder(null, userid, search, actionTypes, start, count, startOffset, endOffset, handler);
	}

	// 根据订单号获取订单
	public void getOrderByOrderId(String user_id, String orderId, WebResponseHandler<ChargeOrder> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_GET;
		url = url.replaceFirst("\\{orderId\\}", orderId);
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new OrderParser());
	}

	//
	public void getOrder(String user_id, String orderId, int retryTimes, String pileCode, WebResponseHandler<ChargeOrder> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_GET_FOR_CHARGING_COMPLETE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, user_id);
		params.put(WebAPI.KEY_ORDER_ID, orderId);
		params.put(WebAPI.KEY_PILECODE, pileCode);
		params.put(WebAPI.KEY_RETRYTIMES, retryTimes);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new OrderParser());
	}

	// 充电完成获取订单
	public void getOrderFromPile(String pileCode, String orderId, WebResponseHandler<ChargeOrder> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_GET_FROM_PILE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		params.put(WebAPI.KEY_PILECODE, pileCode);
		params.put(WebAPI.KEY_ORDER_ID, orderId);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new OrderParser());
	}
	// 电桩或订单评价

	// 删除订单
	public void deleteOrder(String userid, String orderIds, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_DELETE;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_ORDER_IDS, orderIds);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	/**
	 * @param pileId
	 * @param pileName
	 * @param location
	 * @param evaluateType
	 * @param userId
	 * @param orderId
	 * @param evaluate
	 * @param level
	 * @param environmentLevel
	 * @param exteriorLevel
	 * @param performanceLevel
	 * @param serviceLevel
	 * @param photos
	 * @param handler
	 */
	public void postEvaluate(String pileId, String pileName, String location, int evaluateType, String userId, String orderId, String evaluate, int level, int
			environmentLevel, int exteriorLevel, int performanceLevel, int serviceLevel, Map<String, Object> photos, WebResponseHandler<ChargeOrder> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_ADD;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_PILE_NAME, pileName);
		params.put(WebAPI.KEY_TOPIC_LOCATION, location);
		params.put(WebAPI.KEY_EVALUATE_TYPE, evaluateType);
		params.put(WebAPI.KEY_USER_ID, userId);
		params.put(WebAPI.KEY_ORDER_ID, orderId);

		params.put(WebAPI.KEY_EVALUATE, evaluate);
		params.put(WebAPI.KEY_LEVEL, level);
		params.put(WebAPI.KEY_ENVIRONMENT_LEVEL, environmentLevel);
		params.put(WebAPI.KEY_EXTERIOR_LEVEL, exteriorLevel);
		params.put(WebAPI.KEY_PERFORMANCE_LEVEL, performanceLevel);
		params.put(WebAPI.KEY_SERVICE_LEVEL, serviceLevel);

		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, photos, handler, new OrderParser(), false);
	}

	// 订单评价返回内容
	public void getEvaluate(String pileId, String userId, String orderId, int start, int count, WebResponseHandler<List<Command>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_COMMAND_CONTENT;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_USER_ID, userId);
		params.put(WebAPI.KEY_ORDER_ID, orderId);

		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new CommandArrayParser());
	}

	public void getPileOrder(String pileId, String search, List<Integer> actionTypes, int start, int count, int startOffset, int endOffset,
	                         WebResponseHandler<List<ChargeOrder>> handler) {
		getOrder(pileId, null, search, actionTypes, start, count, startOffset, endOffset, handler);
	}

	private void getOrder(String pileId, String userid, String search, List<Integer> actionTypes, int start, int count, int startOffset, int endOffset,
	                      WebResponseHandler<List<ChargeOrder>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_LIST;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_SEARCH, search);
		params.put(WebAPI.KEY_STARTOFFSET, startOffset);
		params.put(WebAPI.KEY_ENDOFFSET, endOffset);
		if (actionTypes != null && actionTypes.size() != 0) {
			StringBuilder sb = new StringBuilder();
			for (Integer action : actionTypes) {
				sb.append(action + ",");
			}
			if (sb.toString().endsWith(",")) {
				sb.deleteCharAt(sb.length() - 1);
			}
			params.put(WebAPI.KEY_ACTION, sb.toString());
		}
		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new OrderArrayParser());
	}

	public void payMoney(long chargeRecordId, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_BAIDU_PAY_MONEY;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", chargeRecordId);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, null);
	}

	// 处理订单
	public void agreeOrder(String orderId, WebResponseHandler<ChargeOrder> handler) {
		handleOrder(orderId, 1, handler);
	}

	public void rejectOrder(String orderId, WebResponseHandler<ChargeOrder> handler) {
		handleOrder(orderId, 2, handler);
	}

	public void cancelOrder(String orderId, WebResponseHandler<ChargeOrder> handler) {
		handleOrder(orderId, 3, handler);
	}

	private void handleOrder(String orderId, int action, WebResponseHandler<ChargeOrder> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ORDER_MODIFY;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_ORDER_ID, orderId);
		params.put(WebAPI.KEY_ACTION, action);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new OrderParser());
	}

	// 获取充电记录
	// type类型：1:全部用户；2:家庭用户和自己；3:租户
	public void getChargeRecord(String pileId, String userid, Long startTime, Long endTime, Integer type, int start, int count,
	                            WebResponseHandler<List<ChargeRecord>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_CHARGE_RECORD_GET;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_START_TIME, startTime);
		params.put(WebAPI.KEY_END_TIME, endTime);
		params.put(WebAPI.KEY_TYPE, type);
		params.put(WebAPI.KEY_START, start);
		params.put(WebAPI.KEY_COUNT, count);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new ChargeRecordArrayParser());
	}

	// 上传告警信息
	public void uploadPileWarring(String pileId, byte faultType, WebResponseHandler<PileStatus> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_ALARM;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_RECORD_TIME, System.currentTimeMillis() + "");
		params.put(WebAPI.KEY_FAULT_TYPE, faultType + "");
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	// 获取聚合点

	// 上传充电记录
	public void uploadChargeRecord(List<ChargeRecordCache.RecordData> crs, WebResponseHandler<List<ChargeRecord>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_CHARGE_RECORD_UPLOAD;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_RECORDS, crs);
		for (int i = 0; i < crs.size(); i++) {
			String gson = GsonUtil.createSecurityGson().toJson(crs.get(i));
			Log.e("gson", gson);
		}
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new ChargeRecordArrayParser());
	}

	/**
	 * 弃用
	 */
	@Deprecated
	public void getCongruentPoint(double startLat, double startLong, double endLat, double endLong, WebResponseHandler<List<CongruentPoint>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GEET_CONGRUENT_POTIN;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_START_LATITUDE, startLat);
		params.put(WebAPI.KEY_START_LONGITUDE, startLong);
		params.put(WebAPI.KEY_END_LATITUDE, endLat);
		params.put(WebAPI.KEY_END_LONGITUDE, endLong);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new CongruentPointArrayParser());
	}

	/*******************************************
	 * pile api
	 **************************************/

	// 获取聚合点
	public void getCongruentPoint(double maxLevel, RectangleSearchCondition rsc, WebResponseHandler<List<CongruentPoint>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_GEET_CONGRUENT_POTIN;

		Map<String, Object> headers = getDefautlHeader();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_START_LATITUDE, rsc.getStartLat());
		params.put(WebAPI.KEY_START_LONGITUDE, rsc.getStartLong());
		params.put(WebAPI.KEY_END_LATITUDE, rsc.getEndLatitude());
		params.put(WebAPI.KEY_END_LONGITUDE, rsc.getEndLongitude());
		params.put(WebAPI.KEY_IS_LDLE, rsc.getIsIdle());
		params.put("maxLevel", maxLevel);

		// 关键字
		if (rsc.getSearch() != null && !rsc.getSearch().equals("")) {
			params.put(WebAPI.KEY_SEARCH, rsc.getSearch());
		}

		// 拥有者类型
		if (rsc.getPileTypes() != null && rsc.getPileTypes().size() > 0) {
			StringBuilder operators = new StringBuilder();
			for (Integer operator : rsc.getPileTypes()) {
				operators.append(operator + ",");
			}
			operators.deleteCharAt(operators.length() - 1);
			params.put(WebAPI.KEY_PILE_TYPE, operators);
		}
		// 分享时间
		if (rsc.getStartTime() > 0) {
			params.put(WebAPI.KEY_START_TIME, rsc.getStartTime());
			if (rsc.getDuration() > 0) {
				params.put(WebAPI.KEY_DURATION, rsc.getDuration());
			}
			if (rsc.getEndTime() > 0) {
				params.put(WebAPI.KEY_END_TIME, rsc.getEndTime());
			}
		}
		// 缩放参数
		if (rsc.getZoom() > 0) {
			params.put(WebAPI.KEY_ZOOM, rsc.getZoom());
		}
		LogUtils.e("WebAPIManager", "findPile:" + params.toString());
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new CongruentPointArrayParser());
	}

	/*******************************************
	 * jpush api
	 **************************************/
	public void getMessages(String user_id, int count, int pageSize, int status, WebResponseHandler<List<Message>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MESSAGES_GET;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_MESSAGE_STATUS, status);
		params.put(WebAPI.KEY_MESSAGE_START, count);
		params.put(WebAPI.KEY_MESSAGE_COUNT, pageSize);
		params.put(WebAPI.KEY_MESSAGE_USER_ID, user_id);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new MessageArraParser());

	}

	public void clearMessages(String user_id, int flag, WebResponseHandler<Integer> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MESSAGES_EMPTY;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_NOTICE_FLAG, flag);
		params.put(WebAPI.KEY_MESSAGE_USER_ID, user_id);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);

	}

	public void bindPushId(String user_id, String pushId, int pushEnable, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_BIND_PUSHID;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PUSH_ID, pushId);
		params.put(WebAPI.KEY_MESSAGE_USER_ID, user_id);
		params.put(WebAPI.KEY_PUSHENABLE, pushEnable);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	//获取资讯列表
	public void getInfoList(int pageNo, WebResponseHandler<List<DynamicInfo>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_NEWS_LIST;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
		params.put(WebAPI.KEY_PAGENO, pageNo);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new DynamicInfoParser());
	}

	//绑定第三方
	public void bindThirdAcc(String thirdUid, String thirdAccToken, int thirdAccType, String userid, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_BIND_THIRD_ACC;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_THIRD_ACC_TOKEN, thirdAccToken);
		params.put(WebAPI.KEY_THIRD_ACC_USER_ID, thirdUid);
		params.put(WebAPI.KEY_THIRD_ACC_TYPE, thirdAccType);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	public void setMessageStatus(String messageId, int status, WebResponseHandler<Object> handler) {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message(messageId, status));

		setMessageStatus(messages, handler);
	}

	public void setMessageStatus(List<Message> messages, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MESSAGES_STATUS_SET;

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_NOTICE_STATUS, messages);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	public void getUnreadMessageNum(String user_id, WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MESSAGES_UNREAD_NUM_GET;
		url = url.replaceFirst("\\{userid\\}", user_id);

		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);

		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, null);
	}

	/**
	 * 自动设置校对桩时间
	 *
	 * @param handler byMazoh
	 */
	public void setCurrentTimeForPileAutomatic(WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_SYSCURTIME;
		Map<String, Object> headers = getDefautlHeader();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, null);
	}

	/**
	 * 批量上传图片 byMazoh
	 *
	 * @param userid
	 * @param pileId
	 * @param fileKeyAndPath
	 * @param handler        byMazoh
	 */
	public void updateImagesInServer(String userid, String pileId, Map<String, Object> fileKeyAndPath, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_UPLAOD_ADD;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_USER_ID, userid);
		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, fileKeyAndPath, handler, null, false);
	}

	/**
	 * 建议与反馈 byMazoh
	 *
	 * @param userid
	 * @param type    反馈类型，1：建议，2：投诉
	 * @param content
	 * @param handler byMazoh
	 */
	public void feedBack(String userid, int type, String content, Map<String, Object> fileKeyAndPath, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_FEEDBACK;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_TYPE, type);
		params.put(WebAPI.KEY_CONTENT, content);
		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, fileKeyAndPath, handler, null, false);
	}

	//byMazoh
	public void delImgs(String userid, String pileId, ArrayList<String> imgs, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_UPLAOD_DELETE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_IMGS, imgs);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);

	}

	/**
	 * 优易圈发布话题 byMazoh
	 *
	 * @param themeTopic
	 * @param fileKeyAndPath
	 * @param handler        byMazoh
	 */
	public void displayTopic(ThemeTopic themeTopic, Map<String, Object> fileKeyAndPath, WebResponseHandler<Article> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_ADD;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		if (themeTopic != null) {
			params.put(WebAPI.KEY_TOPIC_CONTENT, themeTopic.getContent());
			params.put(WebAPI.KEY_TOPIC_USERID, themeTopic.getUserid());
			params.put(WebAPI.KEY_TOPIC_LOCATION, themeTopic.getLocation());
			params.put(WebAPI.KEY_TOPIC_TYPE, themeTopic.getType());
		}
		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, fileKeyAndPath, handler, null, false);
	}

	//获取话题详情
	public void getTopicInfo(String articleId, WebResponseHandler<Article> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_INFO;
		url = url.replaceFirst("\\{articleId\\}", articleId);
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
//		params.put(WebAPI.KEY_TOPIC_ARTICLEID, articleId);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new ArticleParser());
	}

	//话题点赞
	public void doLike(String articleId, String userid, int like, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_LIKE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
		params.put(WebAPI.KEY_TOPIC_ARTICLEID, articleId);
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_TOPIC_LIKE, like);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	//删除话题详情
	public void deleteTopicInfo(String articleId, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_DELETE;
		url = url.replaceFirst("\\{articleId\\}", articleId);
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
//		params.put(WebAPI.KEY_TOPIC_ARTICLEID, articleId);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	public void getMomentList(int pageNum, WebResponseHandler<List<Article>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_LIST;
		url = url.replaceFirst("\\{pageNum\\}", pageNum + "");
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new ArticleArrayParser());
	}

	public void getPileScore(String pileId, WebResponseHandler<PileScore> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_PILE_SCORE;
		url = url.replaceFirst("\\{pileId\\}", pileId);
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new PileScoreParser());
	}

	public void getPileMoment(String pileId, int pageNum, WebResponseHandler<List<Article>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_PILE_MOMENTS;
		url = url.replaceFirst("\\{pileId\\}", pileId);
		url = url.replaceFirst("\\{pageNum\\}", pageNum + "");
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new ArticleArrayParser());
	}


	public void getMyMoment(String userid, int pageNum, WebResponseHandler<List<Article>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_MY_MOMENTS;
		url = url.replaceFirst("\\{userid\\}", userid);
		url = url.replaceFirst("\\{pageNum\\}", pageNum + "");
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<>();

		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new ArticleArrayParser());
	}

	/**
	 * 话题回复评论接口 byMazoh
	 *
	 * @param articleId
	 * @param replyArtId
	 * @param replyUserid
	 * @param content
	 * @param handler
	 */
	public void replyMoment(String articleId, String replyArtId, String userid, String replyUserid, String content, WebResponseHandler<Object> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_MOMENT_REPLAY;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<>();
		params.put(WebAPI.KEY_TOPIC_ARTICLEID, articleId);
		if (replyArtId != null) {
			params.put(WebAPI.KEY_TOPIC_REPLYARTID, replyArtId);
		}
		if (replyUserid != null) {
			params.put(WebAPI.KEY_TOPIC_REPYUSERID, replyUserid);
		}
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_CONTENT, content);
		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	/**
	 * 重筹标点添加桩 byMazoh
	 *
	 * @param userid
	 * @param handler
	 */
	public void addPile(String userid, String userName, String address, Double longitude, Double latitude, int pileStatus, int pileType, Float power, Float
			ratedVoltage, Float ratedCurrent, String productName, String desp, Map<String, Object> fileKeyAndPath, WebResponseHandler<Object> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_ADDPILE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_USER_NAME, userName);
		params.put(WebAPI.KEY_ADDRESS, address);

		params.put(WebAPI.KEY_LONGITUDE, longitude);
		params.put(WebAPI.KEY_LATITUDE, latitude);
		params.put(WebAPI.KEY_PILESTATUS, pileStatus);
		params.put(WebAPI.KEY_PILETYPE, pileType);
		if (power != 0.0) {
			params.put(WebAPI.KEY_POWER, power);
		}
		if (ratedVoltage != 0.0) {
			params.put(WebAPI.KEY_RATED_VOLTAGE, ratedVoltage);
		}
		if (ratedCurrent != 0.0) {
			params.put(WebAPI.KEY_RATED_CURRENT, ratedCurrent);
		}
		params.put(WebAPI.KEY_PRODUCTNAME, productName);
		params.put(WebAPI.KEY_PILE_DESP, desp);
		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, fileKeyAndPath, handler, null, false);
	}


	/******************************************* jpush api **************************************/

	/**
	 * 重筹标点添加站点 byMazoh
	 *
	 * @param userid
	 * @param handler
	 */

	public void addSite(String userid, String userName, String address, Double longitude, Double latitude, int stationStatus, int stationType, int dcNum, int
			acNum, String productName, String desp, Map<String, Object> fileKeyAndPath, WebResponseHandler<Object> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_ADDSITE;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_USER_NAME, userName);
		params.put(WebAPI.KEY_ADDRESS, address);
		params.put(WebAPI.KEY_LONGITUDE, longitude);
		params.put(WebAPI.KEY_LATITUDE, latitude);
		params.put(WebAPI.KEY_STATIONSTATUS, stationStatus);
		params.put(WebAPI.KEY_STATIONTYPE, stationType);
		params.put(WebAPI.KEY_DIRECT_NUM, dcNum);
		params.put(WebAPI.KEY_ALTER_NUM, acNum);
		params.put(WebAPI.KEY_PRODUCTNAME, productName);
		params.put(WebAPI.KEY_PILE_DESP, desp);
		startExcuteForUploads(WebAPI.METHOD_POST, url, headers, params, fileKeyAndPath, handler, null, false);
	}

	/******************************************* base method **************************************/

//扫码充电相关接口 begin

	/**
	 * GPRS+gprs_ble充电桩充电鉴权接口 byMazoh
	 *
	 * @param userid  用户Id
	 * @param qrcode  运行编码
	 * @param handler
	 */
	public void scanInfo(String userid, String qrcode, WebResponseHandler<PileAuthentication> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_AUTHENTICATE;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_QRCODE, qrcode);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new PileAuthenticationParser());
	}

	/**
	 * 纯gprs桩开始或结束充电接口 byMazoh
	 *
	 * @param userid    用户Id
	 * @param pileId    出厂编码
	 * @param onOffCode 充电指令
	 * @param handler
	 */
	public void control(String userid, String pileId, String pileCode, int onOffCode, WebResponseHandler<Object> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_ONOFFCHARGE;
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_PILE_ID, pileId);
		params.put("pileCode", pileCode);
		params.put(WebAPI.KEY_ONOFFCODE, onOffCode);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, null);
	}

	/**
	 * 纯gprs桩充电结束拿Pile byMazoh
	 *
	 * @param pileCode 运行编码
	 * @param handler
	 */
	public void getPile(String pileCode, WebResponseHandler<Pile> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_GETPILE;
		url = url.replaceFirst("\\{pileCode\\}", pileCode);
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new PileParser());
	}


	/**
	 * 轮询拿充电状态 by Mazoh
	 *
	 * @param pileCode   运行编码
	 * @param userid     用户Id
	 * @param retryTimes 轮询次数
	 * @param handler
	 */
	public void getCtrlRes(String pileCode, String userid, int retryTimes, WebResponseHandler<CtrlRes> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_CTRL_RES;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_PILECODE, pileCode);
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_RETRYTIMES, retryTimes);

		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new CtrlResParser());
	}

	/**
	 * 纯gprs桩充电过程中获取电桩实时状态信息接口 byMazoh
	 *
	 * @param pileCode 运行编码
	 * @param handler
	 */
	public void getchargingStatus(String pileCode, WebResponseHandler<RealTimeStatus> handler) {

		String url = WebAPI.API_M2M + WebAPI.API_CHARGESTATUS;
		url = url.replaceFirst("\\{pileCode\\}", pileCode);
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new RealTimeStatusParser());
	}

	/**
	 * 纯gprs桩充电过程中检查GPRS充电桩设备状态 byMazoh
	 *
	 * @param userid  用户Id
	 * @param handler
	 */
	//TODO 待解决
	public void checkIfCharging(String userid, WebResponseHandler<Object> handler) {

		String url = WebAPI.BASE_URL + WebAPI.API_CHECKCHARGE;
//		url = url.replaceFirst("\\{userid\\}", userid);
		Map<String, Object> headers = getDefautlHeader();
		headers.put(WebAPI.KEY_TOKEN, accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, null);
	}

	/**
	 * 纯gprs桩充电完成后获取充电记录 byMazoh
	 *
	 * @param userid  用户Id
	 * @param orderid 订单用户Id
	 * @param handler
	 */
	public void getChargeRecordForGprs(String userid, String orderid, WebResponseHandler<ChargeRecord> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_CHARGERECORD;
		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_USER_ID, userid);
		params.put(WebAPI.KEY_ORDER_ID, orderid);
		startExcuteJson(WebAPI.METHOD_GET, url, headers, params, handler, new ChargeRecordParser());
	}

	public void getDownloadQrCode(WebResponseHandler<String> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_DOWNLOAD_QR_CODE;
		Map<String, Object> headers = getDefautlHeader();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, null);
	}

	public void getOperatorMap(WebResponseHandler<Map<Integer, String>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_OPERATOR_TYPES;
		Map<String, Object> headers = getDefautlHeader();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new DKeyValueMapParser());
	}

	public void getPileTypeMap(WebResponseHandler<Map<Integer, String>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_TYPE;
		Map<String, Object> headers = getDefautlHeader();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new DKeyValueMapParser());
	}

	public void getPileStationTypeMap(WebResponseHandler<Map<Integer, String>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_PILE_STATION_TYPE;
		Map<String, Object> headers = getDefautlHeader();
		startExcuteJson(WebAPI.METHOD_GET, url, headers, null, handler, new DKeyValueMapParser());
	}
//扫码充电相关接口 end


	/******************************************* jpush api **************************************/

	/*******************************************
	 * other
	 **************************************/

	public void getKey(String phone, int usertype, WebResponseHandler<List<Key>> handler) {
		String url = WebAPI.BASE_URL + WebAPI.API_KEY_GET;

		Map<String, Object> headers = getDefautlHeader();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WebAPI.KEY_UID, phone);
		params.put(WebAPI.KEY_ID_TYPE, usertype);

		startExcuteJson(WebAPI.METHOD_POST, url, headers, params, handler, new KeyArrayParser());
	}

	/**
	 * excute http request in new thread
	 */
	private <T> void startExcute(final String method, final String url, final Map<String, Object> headers, final Map<String, Object> params, final
	WebResponseHandler<T> handler, final WebResponseParser<T> webResponseParser) {

		startExcute(method, url, headers, params, null, null, handler, webResponseParser);

	}

	private <T> void startExcute(final String method, final String url, final Map<String, Object> headers, final Map<String, Object> params, final String
			fileKey, final String filePath, final WebResponseHandler<T> handler, final WebResponseParser<T> webResponseParser) {
		startExcute(method, url, headers, params, fileKey, filePath, handler, webResponseParser, false);
	}

	private <T> void startExcuteJson(final String method, final String url, final Map<String, Object> headers, final Map<String, Object> params, final
	WebResponseHandler<T> handler, final WebResponseParser<T> webResponseParser) {

		startExcuteJson(method, url, headers, params, null, null, handler, webResponseParser);

	}

	private <T> void startExcuteJson(final String method, final String url, final Map<String, Object> headers, final Map<String, Object> params, final String
			fileKey, final String filePath, final WebResponseHandler<T> handler, final WebResponseParser<T> webResponseParser) {
		startExcute(method, url, headers, params, fileKey, filePath, handler, webResponseParser, true);
	}

	private <T> void startExcute(final String method, final String url, final Map<String, Object> headers, final Map<String, Object> params, final String
			fileKey, final String filePath, final WebResponseHandler<T> handler, final WebResponseParser<T> webResponseParser, final boolean isJson) {

		handler.sendStart();
		new Thread(new Runnable() {
			@Override
			public void run() {
				WebResponse<T> webResponse = null;
				try {
					byte[] data = null;
					if (WebAPI.METHOD_POST.equals(method)) {
						if (fileKey == null || fileKey.equals("") || filePath == null || filePath.equals("")) {
							data = WebConnectionManager.getInstance().post(url, headers, params, isJson);
						} else {
							data = WebConnectionManager.getInstance().upload(url, headers, params, fileKey, filePath, null);
						}
					}
					if (WebAPI.METHOD_GET.equals(method)) {
						data = WebConnectionManager.getInstance().get(url, headers, params);
					}
					// handler & parser
					String json = new String(data, "UTF-8");
					webResponse = parserResponse(json);
					if (url.equals(WebAPI.BASE_ALIPAY_URL)) {// 支付宝url
						webResponse = parserResponseForGaoDe(json);
						if ("1".equals(webResponse.getCode())) {
							if (webResponseParser != null && webResponse.getResult() != null) {
								webResponseParser.parse(webResponse);
							}
							handler.sendSuccess(webResponse);
						} else {
							log(method, url, headers, params, webResponse, null, null);
							handler.sendFailure(webResponse);
						}
					} else {
						if (WebResponse.CODE_SUCCESS.equals(webResponse.getCode())) {
							if (webResponseParser != null && webResponse.getResult() != null && webResponse.isHaveResultObject() == true) {
								webResponseParser.parse(webResponse);
							}
							handler.sendSuccess(webResponse);
						} else {
							log(method, url, headers, params, webResponse, null, null);
							handler.sendFailure(webResponse);
						}
					}
				} catch (HttpException he) {
					log(method, url, headers, params, webResponse, he, null);
					handler.sendError(he);
				} catch (Exception e) {
					log(method, url, headers, params, webResponse, null, e);
					handler.sendError(e);
				} finally {
					handler.sendFinish();
				}
			}
		}).start();
	}

	@SuppressWarnings("unused")
	private <T> void startExcuteForUploads(final String method, final String url, final Map<String, Object> headers, final Map<String, Object> params, final
	Map<String, Object> fileKeyAndPath, final WebResponseHandler<T> handler, final WebResponseParser<T> webResponseParser, final boolean isJson) {

		handler.sendStart();
		new Thread(new Runnable() {
			@Override
			public void run() {
				WebResponse<T> webResponse = null;
				try {
					byte[] data = null;
					if (WebAPI.METHOD_POST.equals(method)) {
						if (fileKeyAndPath == null || fileKeyAndPath.equals("")) {
							data = WebConnectionManager.getInstance().post(url, headers, params, isJson);
						} else {
							data = WebConnectionManager.getInstance().uploads(url, headers, params, fileKeyAndPath, null);
						}
					}
					if (WebAPI.METHOD_GET.equals(method)) {
						data = WebConnectionManager.getInstance().get(url, headers, params);
					}
					// handler & parser
					String json = new String(data);
					webResponse = parserResponse(json);
					if (WebResponse.CODE_SUCCESS.equals(webResponse.getCode())) {
						if (webResponseParser != null) {
							webResponseParser.parse(webResponse);
						}
						handler.sendSuccess(webResponse);
					} else {
						log(method, url, headers, params, webResponse, null, null);
						handler.sendFailure(webResponse);
					}
				} catch (HttpException he) {
					log(method, url, headers, params, webResponse, he, null);
					handler.sendError(he);
				} catch (Exception e) {
					log(method, url, headers, params, webResponse, null, e);
					handler.sendError(e);
				} finally {
					handler.sendFinish();
				}
			}
		}).start();
	}

	/**
	 * parser response
	 * <p/>
	 * 拆分code、message、result
	 *
	 * @throws JSONException
	 */
	private <T> WebResponse<T> parserResponse(String json) throws JSONException {
		WebResponse<T> webResponse = new WebResponse<T>();
		Log.i("jsonString", json);
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
		// 第三方服务器解析
		if (!jsonObj.has(WebAPI.KEY_MESSAGE) || !jsonObj.has(WebAPI.KEY_CODE)) {
			webResponse.setCode(WebResponse.CODE_SUCCESS);
			webResponse.setMessage("第三方");
			webResponse.setResult(json);
			return webResponse;
		}

		// 业务服务器解析
		String code = jsonObj.get(WebAPI.KEY_CODE).getAsString();
		String message = jsonObj.get(WebAPI.KEY_MESSAGE).getAsString();
		//json元素
		JsonElement je = jsonObj.get(WebAPI.KEY_RESULT);

		webResponse.setCode(code);
		webResponse.setMessage(message);
		if (je != null) {
			webResponse.setResult(je.toString());
			webResponse.setHaveResultObject(true);
		} else {
			webResponse.setResult(json);
			webResponse.setHaveResultObject(false);
		}
		return webResponse;
	}

	private <T> WebResponse<T> parserResponseForGaoDe(String json) throws JSONException {
		WebResponse<T> webResponse = new WebResponse<T>();
		Log.i("jsonString", json);
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
		// 业务服务器解析
		String code = jsonObj.get(WebAPI.KEY_CODE).getAsString();
		String message = jsonObj.get(WebAPI.KEY_MESSAGE).getAsString();
		webResponse.setCode(code);
		webResponse.setMessage(message);
		webResponse.setResult(json);
		return webResponse;
	}

	private String log(String method, String url, Map<String, Object> headers, Map<String, Object> params, WebResponse<?> response, HttpException he,
	                   Exception e) {

		StringBuffer sb = new StringBuffer();

		// 错误类别
		if (response != null) {
			sb.append("【Service Error】\n");
		} else if (he != null) {
			sb.append("【Network Error】\n");
		} else if (e != null) {
			sb.append("【Unknown Error】\n");
		}
		if (response != null) {
			sb.append("Code: " + response.getCode() + "\n");
			sb.append("Message: " + response.getMessage() + "\n");
			sb.append("Content: " + response.getResult() + "\n");
		}
		if (he != null) {
			Throwable t = he.getCause() == null ? he : he.getCause();
			sb.append("Error By: " + t.getClass().getName() + ":" + t.getMessage() + "\n");
		}
		if (e != null) {
			Throwable t = e.getCause() == null ? e : e.getCause();
			sb.append("Error By: " + t.getClass().getName() + ":" + t.getMessage() + "\n");
		}

		// 请求方式
		sb.append("Method: " + method + "\n");

		// url
		if (WebAPI.METHOD_GET.equals(method) || WebAPI.METHOD_DELETE.equals(method)) {
			String urlParams = WebHelper.encodeParams(params);
			url += urlParams == null ? "" : "?" + urlParams;
		}
		sb.append("URL: " + url + "\n");

		Object value = null;
		// headers
		if (headers != null) {
			for (String key : headers.keySet()) {
				value = headers.get(key);
				if (value == null || value.equals("")) {
					continue;
				}
				sb.append("Header: " + key + " = ");
				sb.append(value + "\n");
			}
		}
		// params
		if (params != null) {
			for (String key : params.keySet()) {
				value = params.get(key);
				if (value == null || value.equals("")) {
					continue;
				}
				sb.append("Params: " + key + " = ");
				if (WebAPI.KEY_PASSWORD.equals(key)) {
					sb.append("******\n");
				} else {
					sb.append(value + "\n");
				}
			}
		}

		if (isDetail) {
			if (he != null) {
				sb.append("Error Detail: ");
				Throwable t = he.getCause() == null ? he : he.getCause();
				for (StackTraceElement ste : t.getStackTrace()) {
					sb.append(ste + "\n");
				}
			} else if (e != null) {
				sb.append("Error Detail: ");
				Throwable t = e.getCause() == null ? e : e.getCause();
				for (StackTraceElement ste : t.getStackTrace()) {
					sb.append(ste + "\n");
				}
			}
		}

		Log.e(tag, sb.toString());

		// 写到文件中
		LogUtil.log2File(MyConstant.PATH + "/log_web", sb.toString());
		return sb.toString();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

}
