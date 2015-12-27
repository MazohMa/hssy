package com.xpg.hssy.web;

import com.xpg.hssychargingpole.BuildConfig;

/**
 * WebAPI
 *
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2014年7月8日
 */
public class WebAPI {
	/**
	 * method
	 */
	public static final String METHOD_GET = "GET";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_UPLOAD = "UPLOAD";

	/**
	 * host
	 */
	public static final String BASE_URL;

	static {//从构建文件中获取BASE_URL
		BASE_URL = BuildConfig.BASE_URL;
	}

	//	public static final String PORT = "8080";// 验收测试
//	// public static final String PORT = "3152";// 开发
//	public static final String BASE_URL = "http://222.128.77.155:" + PORT
//			+ "/chargepile";// 测试
//	// public static final String BASE_URL = "http://renrenchongdian.com";// 正式
	public static final String BASE_ALIPAY_URL = "http://203.130.42.199/autopay/paymentinfo";// 支付宝请求url
	public static final String API_PAY_MONEY = "/pay/money";// 获得支付宝请求参数
	public static final String API_M2M = "http://111.207.235.55/m2m/cgi-bin";//纯gprs桩充电过程中获取电桩实时状态信息接口

	public static final String API_PAY_MONEY_BEECLOUD = "/pay/beecloud/money";// 获得beecloud请求参数

	public static final String API_PAY_MONEY_BAIFUBAO = "/pay/baidu/info";// 获得百度钱包支付签约请求参数
	public static final String API_USER_HAS_BAIFUBAO_CONTRACT = "/user/baidu_bing/";// 获取用户是否签约百度钱包代扣服务
	public static final String API_CANCEL_BAIFUBAO_CONTRACT = "/pay/baidu/cancle_banding/";// 解除百付宝签约代扣服务
	public static final String API_CANCEL_PAY_LOCK = "/pay/charge/cancle_lock";// 解除订单支付锁定

	//充电币
	public static final String API_GET_REMAIN_MONEY = "/user/charging_coins/";// 获取充电币余额
	public static final String API_GET_RECHARGE_RECORDS = "/charging_coins/recharge_record";// 获取充电币充值记录
	public static final String API_GET_EXPENSE_RECORDS = "/charging_coins/pay_record";// 获取充电币支付记录
	public static final String API_RECHARGE = "/pay/beecloud/recharge/recharge_count";// 充值


	/**
	 * api
	 */
	// user
	public static final String API_LOGIN = "/user/login";
	public static final String API_VERIFY = "/tplsendsms";
	public static final String API_REGISTER = "/user/register";
	public static final String API_RESET_PASSWORD = "/user/password";
	public static final String API_LOGOUT = "/user/logout";
	public static final String API_USER_MODIFY = "/user/info";
	public static final String API_USER_MODIFYPASSWORD = "/user/modifyPassword";
	public static final String API_USER_UPLOAD = "/user/upload";
	public static final String API_USER_CHECKCODE = "/user/checkcode";
	public static final String API_USER_EXIST = "/user/exist/{phone}";

	public static final String API_REFRESH_TOKEN = "/refresh_token/{userid}/{refreshToken}";
	// pile
	public static final String API_PILE_GET_list = "/pile/list";
	public static final String API_PILE_SET_PRICE = "/pile/set_price";
	public static final String API_PILE_GET_PUBLIC = "/pile/newlist";
	public static final String API_PILE_GET = "/pile";
	public static final String API_PILE_SET_FAVORITES = "/favorites";
	public static final String API_PILE_GET_FAVORITES = "/favorites/list";
	public static final String API_PILE_CANCEL_FAVORITES = "/favorites/batch";
	public static final String API_PILE_TYPE = "/sys/commonitem/pileType";


	//pileStation
	public static final String API_PILE_STATION_GET = "/station";
	public static final String API_REFRESH_PILE_STATE = "/station/pileState/{stationId}";
	public static final String API_PILE_STATION_TYPE = "/sys/commonitem/stationType";

	public static final String API_PILE_GET_OWN = "/pile/access";
	public static final String API_PILE_MODIFY = "/pile/modify";
	public static final String API_PILE_GET_FAMILY = "/pile/member";
	public static final String API_PILE_AUTH = "/pile/authorization";
	public static final String API_PILE_SHARE = "/pile/share";
	public static final String API_PILE_ISCOLLECTED = "/pile/hadcollect";
	public static final String API_PILE_GEET_CONGRUENT_POTIN = "/pile/lonlat";
	public static final String API_PILE_TIME_SPLIT = "/pile/sharedetail";
	public static final String API_SYSCURTIME = "/sys/curtime";
	public static final String API_ADDPILE = "/report/pile";
	public static final String API_ADDSITE = "/report/station";

	// order
	public static final String API_ORDER_ADD = "/order";
	public static final String API_ORDER_LIST = "/order/list";
	public static final String API_ORDER_MODIFY = "/order/modify";
	public static final String API_ORDER_GET = "/order/{orderId}";
	public static final String API_ORDER_DELETE = "/order/batchDelete";
	public static final String API_ORDER_GET_FROM_PILE = "/order/from-pile";
	public static final String API_ORDER_GET_FOR_CHARGING_COMPLETE = "/order/charge_end";



	// command
	public static final String API_COMMAND_ADD = "/evaluate/add";
	public static final String API_COMMAND_CONTENT = "/moment/pile_info/list";

	// alipayName
	public static final String API_ALIPAYNAME_GET = "/order/aliname/{userid}";
	// record
	public static final String API_CHARGE_RECORD_GET = "/pile/chargerecord";
	public static final String API_CHARGE_RECORD_UPLOAD = "/pile/record/setrecord";
	// key
	public static final String API_KEY_GET = "/key";
	// upload pic add
	public static final String API_UPLAOD_ADD = "/pile/img/put";
	// upload pic setRightDrawableDelete
	public static final String API_UPLAOD_DELETE = "/pile/img/del";
	// feedback
	public static final String API_FEEDBACK = "/sys/feedback";
	// 优易圈回复评论
	public static final String API_MOMENT_REPLAY = "/moment/reply";
	// 优易圈发布话题
	public static final String API_MOMENT_ADD = "/moment/add";
	//获取话题列表
	public static final String API_MOMENT_LIST = "/moment/list/{pageNum}";
	//优易圈获取话题详情
	public static final String API_MOMENT_INFO = "/moment/info/{articleId}";
	//优易圈删除话题详情
	public static final String API_MOMENT_DELETE = "/moment/del/{articleId}";
	//优易圈点赞
	public static final String API_MOMENT_LIKE = "/moment/like";
	//获取我的评价
	public static final String API_MOMENT_MY_MOMENTS = "/moment/my_moments/{userid}/{pageNum}";
	//获取桩,站的总体评价
	public static final String API_MOMENT_PILE_SCORE="/moment/pile_score/{pileId}";
	//获取桩,站的评价列表
	public static final String API_MOMENT_PILE_MOMENTS = "/moment/pile_moments/{pileId}/{pageNum}";
	// 告警
	public static final String API_ALARM = "/sys/pile/alarm";

	//第三方
	public static final String API_BIND_THIRD_ACC = "/user/bind_thrid_acc";

	//news
	public static final String API_NEWS_LIST = "/news/list";

	// message
	public static final String API_MESSAGES_GET = "/push/list";
	public static final String API_MESSAGES_STATUS_SET = "/push/status";
	public static final String API_MESSAGES_UNREAD_NUM_GET = "/push/unread/{userid}";
	public static final String API_BIND_PUSHID = "/push/bind";
	public static final String API_MESSAGES_EMPTY = "/push/empty";

	//下载链接二维码
	public static final String API_DOWNLOAD_QR_CODE="/sys/qrcode";

   //扫码充电

    public static final String API_AUTHENTICATE= "/pile/scan_info";
	public static final String API_ONOFFCHARGE= "/pile/control";

	public static final String API_CHARGESTATUS = "/pile/now_status/{pileCode}"  ;
	public static final String API_CHARGERECORD = ""  ;
	public static final String API_CHECKCHARGE= "/pile/in_charging"  ;
	public static final String API_GETPILE= "/pile/pile_info/{pileCode}"  ;
	public static final String API_CTRL_RES= "/pile/ctrl_res"  ;
	public static final String API_OPERATOR_TYPES= "/sys/commonitem/operatorType"  ;
	public static final String API_BAIDU_PAY_MONEY= "/pay/baidu/pay_money"  ;





	/**
	 * key
	 */
    //push
	public static final String KEY_PUSHENABLE = "pushEnable";//推送消息开关，默认打开。0：关闭；1：打开

	// common
	public static final String KEY_TOKEN = "token";
	public static final String KEY_CLIENT_ID = "clientId";
	public static final String KEY_APP_VERSION = "appVersion";
	public static final String KEY_CODE = "code";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_RESULT = "result";
	public static final String KEY_SORT = "sort";
	public static final String KEY_TYPE = "type";
	public static final String KEY_START = "start";
	public static final String KEY_COUNT = "count";
	// user
	public static final String KEY_USER_ID = "userid";
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_ADDRESS = "address";

	public static final String KEY_USER_TYPE = "usertype";
	public static final String KEY_MOBILE = "mobile";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_NAME = "name";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_AVATAR = "avatar";
	public static final String KEY_THIRD_USER_ID = "thirdUid";
	public static final String KEY_ALIPAY_NAME = "alipayName";
	public static final String KEY_CHECK_CODE = "checkcode";
	public static final String KEY_TENANT_ID = "tenantId";
	public static final String KEY_IMGS = "imgs";
	public static final String KEY_CURRENTPWD = "currentPwd";
	public static final String KEY_NEWPWD = "newPwd";
	public static final String KEY_COMFIRMPWD = "comfirmPwd";
	public static final String KEY_STATE = "state";
	public static final String KEY_PHONENO = "phoneNo";
	public static final String KEY_CHECKCODE = "checkcode";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_DEVICE = "device";
	public static final String KEY_OS = "os";

	// 支付宝请求
	public static final String KEY_SELLEREMAIL_ID = "sellerEmail";
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_OUTTRADENO = "outTradeNo";
	public static final String KEY_TOTALFEE = "totalFee";
	public static final String KEY_RETURNURL = "returnUrl";
	public static final String KEY_MERCHANTURL = "merchantUrl";
	public static final String KEY_NOTIFYURL = "notifyUrl";
	public static final String KEY_ISMOBILE = "isMobile";
	public static final String KEY_REMARK = "remark";
	public static final String KEY_SIGN = "sign";
	public static final String KEY_SEQUENCES = "sequences";
	public static final String KEY_CHANNEL = "channel";

	public static final String KEY_CURRENCY_COUNT = "currencyCount";
	public static final String KEY_RECHARGE_Money = "rechargeMoney";

	// pile
	public static final String KEY_PILE_ID = "pileId";
	public static final String KEY_PILE_NAME = "pileName";
	public static final String KEY_PILE_DESP = "desp";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_ALTITUDE = "altitude";
	public static final String KEY_CITY_ID = "cityId";
	public static final String KEY_CITY_NAME = "cityName";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_SHARE_START_TIME = "shareStartTime";
	public static final String KEY_SHARE_END_TIME = "shareEndTime";
	public static final String KEY_SHARE = "share";
	public static final String KEY_PILE_SHARES = "pileShares";
	public static final String KEY_FAVOR = "favor";
	public static final String KEY_RATED_CURRENT = "ratedCurrent";
	public static final String KEY_RATED_VOLTAGE = "ratedVoltage";
	public static final String KEY_POWER = "power";
	public static final String KEY_PRICE = "price";
	public static final String KEY_CONTACT_NAME = "contactName";
	public static final String KEY_CONTACT_PHONE = "contactPhone";
	public static final String KEY_AUTH = "auth";
	public static final String KEY_RECORDS = "records";
	public static final String KEY_OPERATOR = "operator";
	public static final String KEY_START_LONGITUDE = "longx1";
	public static final String KEY_START_LATITUDE = "laty1";
	public static final String KEY_END_LONGITUDE = "longx2";
	public static final String KEY_END_LATITUDE = "laty2";
	public static final String KEY_PILE_TYPE = "pileType";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_ZOOM = "zoom";
	public static final String KEY_IS_LDLE = "isIdle";
	public static final String KEY_STIME = "stime";
	public static final String KEY_ETIME = "etime";
	public static final String KEY_DATE_COUNT = "dateCount";
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_WEEK = "week";
	public static final String KEY_SERVICE_PAY = "servicePay";
	public static final String KEY_OPERATORCONDITION = "operatorCondition";
	public static final String KEY_PILESTATUS = "pileStatus";
	public static final String KEY_STATIONSTATUS = "stationStatus";
	public static final String KEY_STATIONTYPE = "stationType";
	public static final String KEY_PAGENO = "pageNo";
	public static final String KEY_PILECODE = "pileCode";
	public static final String KEY_RETRYTIMES = "retryTimes";

	//pileStation
	public static final String KEY_STATION_ID = "stationId";

	//	public static final String KEY_OWNER = "owner";
	public static final String KEY_DCNUM = "dcnum";
	public static final String KEY_ACNUM = "acnum";
	public static final String KEY_PRODUCTNAME = "productName";
	public static final String KEY_PILETYPE = "pileType";
	public static final String KEY_DIRECT_NUM = "directNum";
	public static final String KEY_ALTER_NUM = "alterNum";

	// warring
	public static final String KEY_RECORD_TIME = "recordTime";
	public static final String KEY_FAULT_TYPE = "faultType";
	// order
	public static final String KEY_ORDER_ID = "orderId";
	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";
	public static final String KEY_SEARCH = "search";
	public static final String KEY_STARTOFFSET = "startOffset";
	public static final String KEY_ENDOFFSET = "endOffset";
	public static final String KEY_ACTION = "action";
	public static final String KEY_ORDER_IDS = "orderIds";

	// COMMAND
	public static final String KEY_EVALUATE_TYPE = "type";
	public static final String KEY_EVALUATE = "content";
	public static final String KEY_LEVEL = "pileScore";
	public static final String KEY_ENVIRONMENT_LEVEL = "envirScore";
	public static final String KEY_EXTERIOR_LEVEL = "facedScore";
	public static final String KEY_PERFORMANCE_LEVEL = "funcScore";
	public static final String KEY_SERVICE_LEVEL = "servScore";

	// record
	public static final String KEY_STARTTIME_STAMP = "starttimestamp";
	public static final String KEY_ENDTIME_STAMP = "endtimestamp";
	public static final String KEY_RECORD_DATA = "data";
	public static final String KEY_RECORD_SEQUENCE = "data";

	// FEEDBACK
	public static final String KEY_CONTENT = "content";

	// key
	public static final String KEY_UID = "uid";
	public static final String KEY_ID_TYPE = "idType";

	// message
	public static final String KEY_MESSAGE_STATUS = "status";
	public static final String KEY_MESSAGE_START = "start";
	public static final String KEY_MESSAGE_COUNT = "count";
	public static final String KEY_MESSAGE_USER_ID = "userid";
	public static final String KEY_PUSH_ID = "pushId";
	public static final String KEY_MESSAGE_ID = "id";
	public static final String KEY_NOTICE_STATUS = "noticeStatus";
	public static final String KEY_NOTICE_FLAG = "flag";

	//
	public static final String KEY_THIRD_ACC_TOKEN = "thirdAccToken";
	public static final String KEY_THIRD_ACC_USER_ID = "thirdUid";
	public static final String KEY_THIRD_ACC_TYPE = "thirdAccType";

	// 扫码充电
	public static final String KEY_QRCODE = "qrcode";//运行编码
	public static final String KEY_DEVICENAME = "deviceName";//出厂编码
	public static final String KEY_ONOFFCODE = "action";//充电指令


	// 话题
	public static final String KEY_TOPIC_CONTENT= "content";//文字内容
	public static final String KEY_TOPIC_USERID = "userid";//用户ID
	public static final String KEY_TOPIC_LONGITUDE = "longitude";//经度
	public static final String KEY_TOPIC_LATITUDE = "latitude";//纬度
	public static final String KEY_TOPIC_LOCATION = "location";//位置信息
	public static final String KEY_TOPIC_TYPE = "type";//类型
	public static final String KEY_TOPIC_PILEID = "pileId";//电桩id
	public static final String KEY_TOPIC_PILENAME = "pileName";//电桩名称
	public static final String KEY_TOPIC_PILESCORE = "pileScore";//电桩评分
	public static final String KEY_TOPIC_ENVIRSCORE = "envirScore";//环境评分
	public static final String KEY_TOPIC_FACEDSCORE = "facedScore";//外观评分
	public static final String KEY_TOPIC_FUNCSCORE = "funcScore";//功能评分
	public static final String KEY_TOPIC_SERVSCORE = "servScore";//服务评分


	public static final String KEY_TOPIC_ARTICLEID = "articleId";//文章ID
	public static final String KEY_TOPIC_LIKE = "like";//点赞
	public static final String KEY_TOPIC_REPLYARTID = "replyArtId";//回复评论ID
	public static final String KEY_TOPIC_REPYUSERID = "replyUserid";//回复者id





}
