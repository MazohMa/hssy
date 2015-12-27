package com.xpg.hssy.web;

/**
 * WebResponse
 *
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2014年7月8日
 */

public class WebResponse<T> {

	// FAIL(-1,"处理失败"),
	// 服务器请求失败
	public static final String CODE_FAIL = "-1";
	// SUCCESS(0,"成功"),
	public static final String CODE_SUCCESS = "0";
	// REGISTER_FAIL(10000,"注册失败"),
	public static final String CODE_REGISTER_FAIL = "10000";
	// LOGIN_FAIL(10001,"登录失败"),
	public static final String CODE_LOGIN_FAIL = "10001";
	// ACCOUNT_EXISTED(10002,"账户已存在"),
	public static final String CODE_ACCOUNT_EXISTED = "10002";
	// ACCOUNT_NOT_EXISTED(10002,"账户不存在"),
	public static final String CODE_ACCOUNT_NOT_EXISTED = "10003";
	// AUTHORIZE_FAIL(20000,"授权失败"),
	public static final String CODE_AUTHORIZE_FAIL = "20000";
	// VALIDATION_CODE_ERROR(20001,"验证码错误或失效"),
	public static final String CODE_VALIDATION_CODE_ERROR = "20001";
	// INVALID_TOKEN(20002,"无效token"),
	// ----待补充
	public static final String CODE_INVALID_TOKEN = "20002";
	// ERROR_CLIENTID(20003,"Token不是由本终端生成"),
	public static final String CODE_ERROR_CLIENTID = "20003";
	// INVALID_USERID(20004,"无效Userid"),
	// 无效---
	public static final String CODE_INVALID_USERID = "20004";
	// INVALID_RETOKEN(20005,"无效RefreshToken"),
	public static final String CODE_INVALID_RETOKEN = "20005";
	// INVALID_CLIENTID(20006,"无效clientId"),
	public static final String CODE_INVALID_CLIENTID = "20006";
	// TOKEN_EXPIRED(20007,"Token过期"),
	public static final String CODE_TOKEN_EXPIRED = "20007";
	// VALIDATION_CODE_EXPIRED(20008,"验证码过期"),
	public static final String CODE_VALIDATION_CODE_EXPIRED = "20008";
	//PHONE_NUMBER_EXITS(20012,"电话号码已存在")
	public static final String CODE_PHONE_NUMBER_EXITS = "20012";
	// ILLEGAL_DATA(30000,"非法参数"),
	public static final String CODE_ILLEGAL_DATA = "30000";
	// ERROR_DATA(30001,"错误参数"),
	public static final String CODE_ERROR_DATA = "30001";
	// NO_AUTH(30002,"不能給自己授权"),
	public static final String CODE_NO_AUTH = "30002";
	// HAD_AUTH(30003,"不能重复授权"),
	public static final String CODE_HAD_AUTH = "30003";
	// EMPTY_ACCOUNT(30004,"账户不存在"),
	public static final String CODE_EMPTY_ACCOUNT = "30004";
	// ILLEGAL_ACCOUNT_PASSWORD(30005,"用户名/密码错误"),
	// 用户名或密码错误
	public static final String CODE_ILLEGAL_ACCOUNT_PASSWORD = "30005";
	// CODE_OID_ACCOUNT_PASSWORD(30007,"旧密码不正确"),
	// 旧密码不正确
	public static final String CODE_OID_ACCOUNT_PASSWORD = "30007";
	// CODE_MATTER_ACCOUNT_PASSWORD(30008,"两次新密码不同"),
	// 两次新密码不同
	public static final String CODE_MATTER_ACCOUNT_PASSWORD = "30008";
	// CODE_REPEAT_RESERVATION_TIME(304006,"该时间范围已经有了订单不能再次下单")
	public static final String CODE_REPEAT_RESERVATION_TIME = "304006";


	// 订单已过期，您不能同意
	public static final String CODE_ORDER_TIME_OUT_CANNOT_AGREE = "304007";
	// 订单已过期，您不能拒绝
	public static final String CODE_ORDER_TIME_OUT_CANNOT_REJECT = "304008";
	//订单已过期，您不能取消订单
	public static final String CODE_ORDER_TIME_OUT_CANCEL = "304009";
	//TODO tomorrow  to continue 2015 08 26
	//租户已经取消，您不能拒绝订单
	public static final String CODE_ORDER_CANCEL_CANNOT_REJECT = "304012";
	//租户已经取消，您不能确认订单
	public static final String CODE_ORDER_CANCEL_CANNOT_COMFIRM = "304013";
	//桩主已经拒绝，您不能操作订单
	public static final String CODE_ORDER_REFUSE_CANNOT_COMFIRM = "304011";
	//桩主已经确认，您不能操作订单
	public static final String CODE_ORDER_CIRFORM_CANNOT_COMFIRM = "304010";

	// {"code":305002,"message":"桩主未设支付宝账号"}
	public static final String CODE_NULL_PAY_ACOUNT = "305002";
	// SYSTEM_ERROR(40000,"系统错误"),
	// 服务器请求失败
	public static final String CODE_SYSTEM_ERROR = "40000";
	// SYSTEM_BUSY(40001,"系统繁忙"),
	// 服务器请求失败
	public static final String CODE_SYSTEM_BUSY = "40001";
	// FAMILY_ACCOUNT(40002,"系统版本过低"),
	public static final String CODE_LOW_VERSION = "40002";
	// FAMILY_ACCOUNT(50001,"家人账户无需预约"),
	public static final String CODE_FAMILY_ACCOUNT = "50001";
	// INVALID_ORDER(60001,"无效订单号");
	public static final String CODE_INVALID_ORDER = "60001";
	//未上线，未运营
	public static final String CODE_INVALID_UNLINE = "30103";
	public static final String CODE_INVALID_BLE = "304012";
	public static final String CODE_PILE_UNDISPLAY = "30113";
	public static final String CODE_CHARGING_FOR_OTHER = "303006";

	public static final String ENOUGH_CONTRACT_VALIDATE = "305008";
	public static final String CODE_SCAN_HAD_UNPAID = "304015";

	//未开机
	public static final String CODE_INVALID_UNOPEN = "-2";
	// code :
	private String code;
	// message : 提示语，或者错误描述
	private String message;
	// result : 请求返回数据
	private String result;
	private boolean isHaveResultObject;

	public boolean isHaveResultObject() {
		return isHaveResultObject;
	}

	public void setHaveResultObject(boolean isHaveResultObject) {
		this.isHaveResultObject = isHaveResultObject;
	}

	private T resultObj;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public T getResultObj() {
		return resultObj;
	}

	public void setResultObj(T resultObj) {
		this.resultObj = resultObj;
	}

	@Override
	public String toString() {
		return "WebResponse [code=" + code + ", message=" + message + ", result=" + result + ", resultObj=" + resultObj + "]";
	}

}
