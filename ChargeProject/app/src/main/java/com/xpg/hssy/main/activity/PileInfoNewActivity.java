package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.easy.manager.EasyActivityManager;
import com.easy.util.EmptyUtil;
import com.easy.util.IntentUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.ecloud.pulltozoomview.PullToZoomBase;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.adapter.TimePickAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.DateBookTime;
import com.xpg.hssy.bean.TimeGroup;
import com.xpg.hssy.bean.TimeItem;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.Command;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.DurationDialog;
import com.xpg.hssy.dialog.GPRSPileDurationDialog;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialogComfirmNav;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.main.activity.callbackinterface.IBookingPileOperator;
import com.xpg.hssy.main.fragment.PileFindFragment;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssy.view.FixHeightListView;
import com.xpg.hssy.view.PilePhotoPager;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.MyApplication;
import com.xpg.hssychargingpole.R;
import com.xpg.hssychargingpole.shareapi.ShareApiManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PileInfoNewActivity extends BaseActivity implements IBookingPileOperator {
	private static final int REQUEST_REFRESH = 0x99;
	// topBar控件
	private ImageView iv_left, iv_edit_grade, iv_favor, iv_share;// 左边返回按钮,右边编辑按钮,心形按钮,右边分享按钮
	private TextView tv_center;// 中间title
	private RelativeLayout rl_loading;
	private AnimationDrawable loadingAdAnimationDrawable;
	private AnimationDrawable backgroundLoadingAdAnimation;
	private ImageView iv_loading;
	private ImageView iv_background_loading;
	private RelativeLayout rl_loading_fail;
	private Button btn_try_to_refresh;// 重试一下
	// 带下拉回弹效果的scrollView
	private PullToZoomScrollViewEx pzs_center_container;
	// 电桩信息栏控件
//	private LinearLayout ll_bookable;// 可预约的图标layout
//	private TextView tv_bookable;// 可预约图标的文字
	private TextView tv_location;// 位置
	//	private TextView tv_name;// 电桩名字
	private TextView tv_pile_type;//电桩类型
	private TextView tv_message;// 是否空闲
	private TextView tv_distance;// 距离
	//	private ImageView iv_own_type;// 所有者类型
//	private ImageView iv_current_type;// 电流类型
	private LinearLayout ll_price;// 费率layout
	private TextView tv_price;// 费用
	private TextView tv_price_sign;
	private TextView tv_price_unit;

	//GPRS桩充电选择
//    private RelativeLayout rl_gprs_charging_time;
//    private TextView tv_gprs_charging_time;

	//GPRS桩电量显示
	private LinearLayout ll_electricity;
	private SeekBar sb_soc;
	private TextView tv_electricity_percent;


	// 充电信息栏
	private RelativeLayout rlyt_charge_time;// 充电时间layout
	private Button btn_charge_time;//充电时长选择按钮
//	private TextView tv_time;// 充电的启始结束时间
//	private TextView tv_duration;// 充电的持续时间

	// 充电日期选择
	private LinearLayout ll_date_pick_layout;// 日期选择栏layout
	private LinearLayout ll_date_pick;// 日期选择栏

	// 充电时间段选择
	private FixHeightListView fhlv_time_pick_list;// 充电时间选择栏
	private TextView tv_no_time_split_message;// 没有可预约时间段的提醒文字
	private TextView tv_more_time;// "更多"或"收起"的文字
	private RelativeLayout rl_more_time;// 更多充电时间按钮
	private ImageView iv_more_time_arrow;// 更多充电时间箭头

	// 评价栏
	private RelativeLayout rl_pile_grade;// 评分Layout
	private TextView tv_grade;
	private EvaluateColumn eva_star_point;// 星星栏
	private TextView tv_grade_data;// 评分

	private RelativeLayout rl_user_comment;//用户评价
	private TextView tv_user_name;//用户名
	private ImageView civ_user_image;//用户头像
	private EvaluateColumn eva_user_star_point;// 星星栏
	private TextView tv_date_y_m;//评价年月
	private TextView tv_user_command_content;//评价内容
	private RelativeLayout rl_view_all_comment;// 评分Layout
	private TextView tv_grade_number;// 评价人数
	private ImageView iv_grade_arrow;// 右侧箭头

	// 所有者
	private RelativeLayout rl_pile_owner;// 拥有者layout
	private TextView tv_owner_detail;//所有者标题
	private TextView tv_owner_name;// 所有者名字
	private TextView tv_owner_phone_number;// 所有者联系方式
	private ImageView iv_phone_icon;// 电话
	private TextView tv_producer;//生产厂家
	// 电桩信息
	private RelativeLayout rl_pile_meassage;// 电桩信息
	private TextView tv_power;// 功率
	private TextView tv_voltage;// 电压
	private TextView tv_current;// 电流
	private TextView tv_charging_port_num;//充电口数
	private TextView tv_function_area;//功能区
	private LinearLayout ll_wifi, ll_convenience_store, ll_parking_space, ll_camera, ll_market, ll_restaurant, ll_hotel;
	private TextView tv_nothing;

	// 电桩详细说明
	private RelativeLayout rl_pile_detail;// 电桩信息
	private TextView tv_deail;//描述头
	private TextView tv_detail_data;// 描述
	// 电桩图片
	private PilePhotoPager ppp_photo;
	private LinearLayout ll_indicator;
	private ImageView iv_ppp_favor;

	// 底部预约按钮栏
	private RelativeLayout rl_bottom;
	private Button btn_charge_or_comment;//立即充电(预约)或点评按钮
	private Button btn_navigation;// 导航按钮,跟预约按钮大小一样
	private Pile pile;
	private String pileId;
	private SPFile sp;
	private boolean isCollect = true;
	private int isCollected = 0;
	private boolean isCollectedPile = false;

	private String userId;

	private List<User> member;

	private List<CheckBox> datePickList;
	private int checkedIndex;

	private List<DateBookTime> dateBookTimeList;
	private DateBookTime currentDateBookTime;
	protected List<TimeGroup> currentTimeGroupList;
	private Map<TimeGroup, Set<TimeItem>> currentTimeItemsMap;

	private TimePickAdapter<PileInfoNewActivity> timePickAdapter;

	private final static String TIME_FORMAT_YMD = "yyyy/MM/dd";
	private final static String TIME_FORMAT_HM = "HH:mm";
	private final static String TIME_FORMAT_KM = "kk:mm";

	private Calendar currentC;// 当前时间,包括年月日时分,其余字段为0
	private Calendar dateC;// 充电开始的日期,包括年月日,不包括时,分,其余字段为0
	private Calendar startC;// 搜索开始的时间,仅记录时,分,年月日由dateC提供
	private Calendar endC;// 搜索结束时间,仅记录时,分,年月日由dateC提供
	private Calendar bookingStartC;// 预约充电开始时间,仅记录时,分,年月日实际上是1970-01-01
	private Calendar bookingEndC;// 预约充电结束时间,仅记录时,分,年月日实际上是1970-01-01

	private double duration;
	private double gprs_duration;//GPRS桩充电时间

	private GPRSPileDurationDialog gprsPileDurationDialog;

	private DurationDialog durationDialog;

	private datePickChangeListener dateChangeListener;

	private SimpleDateFormat sdf;

	private WebResponseHandler<List<DateBookTime>> dateBookTimeHandler;
	private boolean isShowingMoreTime;
	private BDLocation mLocation;

	private double heightScale;
	private int mScreenHeight;
	private int mScreenWidth;

	private DisplayImageOptions option;
	private final double DEFAULT_DURATION = 0.5;
	private final double DEFAULT_GPRS_DURATION = 0.25;

//	private int gprsType;//是否是GPRS桩
	private int position;//记录找桩列表桩在Adapter的位置
	private int operator;

	private LoadingDialog loadingDialog;
	private int loadState;
	private static final int STATE_LOAD_COMPLETED = 2;

	private boolean isBackgroundRefreshing = false;
	private boolean isBookingPile = false;

	@Override
	protected void onLeftBtn(View v) {
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void initData() {
		super.initData();
		pileId = getIntent().getStringExtra(KEY.INTENT.PILE_ID);
//		gprsType = getIntent().getIntExtra(KEY.INTENT.GPRS_TYPE, 0);
		position = getIntent().getIntExtra(KEY.INTENT.POSITION, -1);
		operator = getIntent().getIntExtra(KEY.INTENT.OPERATOR, 0);

		isCollected = getIntent().getIntExtra("collectedTag", 2);

		isCollectedPile = getIntent().getBooleanExtra("isCollectedPile", false);
//        centerLatitude = getIntent().getDoubleExtra(KEY.INTENT.LATITUDE, -1);
//        centerLongitude = getIntent().getDoubleExtra(KEY.INTENT.LONGITUDE, -1);
		sp = new SPFile(self, "config");
		userId = sp.getString("user_id", null);
		getMember();
		if (isLogin()) {
			// getCollectedTag();
		}
	}

	private void initNoGPRSPile() {
		// 初始化日期数据
		currentC = Calendar.getInstance();
		currentC.set(Calendar.SECOND, 0);
		currentC.set(Calendar.MILLISECOND, 0);
		startC = (Calendar) currentC.clone();
		startC.set(Calendar.HOUR_OF_DAY, 0);
		startC.set(Calendar.MINUTE, 0);
		dateC = (Calendar) startC.clone();
		// 这里只是方便生产日历对象,所以才采用了克隆start的方式进行,跟他们所装载的数据没关系
		bookingStartC = (Calendar) startC.clone();
		bookingEndC = (Calendar) startC.clone();
		endC = (Calendar) startC.clone();

		long startTime = getIntent().getLongExtra(KEY.INTENT.START_TIME, -1);
		long endTime = getIntent().getLongExtra(KEY.INTENT.END_TIME, -1);
		duration = getIntent().getDoubleExtra(KEY.INTENT.DURATION, DEFAULT_DURATION);
//		duration = DEFAULT_DURATION;
		startC.setTimeInMillis(startTime);
		endC.setTimeInMillis(endTime);

		sdf = new SimpleDateFormat(TIME_FORMAT_HM);

		// 为了避免空指针异常,先初始化
		dateBookTimeList = new ArrayList<DateBookTime>();
		currentTimeGroupList = new ArrayList<TimeGroup>();
		currentTimeItemsMap = new TreeMap<TimeGroup, Set<TimeItem>>();

		// 初始化时间分块数据
		dateBookTimeHandler = new WebResponseHandler<List<DateBookTime>>(this) {
			@Override
			public void onStart() {
				super.onStart();
				btn_charge_or_comment.setEnabled(false);
			}

			@Override
			public void onFailure(WebResponse<List<DateBookTime>> response) {
				super.onFailure(response);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadState > STATE_LOAD_COMPLETED) {
					if (!isBackgroundRefreshing) {
						loadingAdAnimationDrawable.stop();
						rl_loading.setVisibility(View.GONE);
						rl_loading_fail.setVisibility(View.GONE);
						rl_bottom.setVisibility(View.VISIBLE);
						iv_share.setVisibility(View.VISIBLE);
						pzs_center_container.setVisibility(View.VISIBLE);
					} else {
						backgroundLoadingAdAnimation.stop();
						iv_background_loading.setVisibility(View.GONE);
						isBackgroundRefreshing = false;
					}
					loadState = 0;
				}
			}

			@Override
			public void onSuccess(WebResponse<List<DateBookTime>> response) {
				super.onSuccess(response);
				loadState++;
				dateBookTimeList = response.getResultObj();
				if (EmptyUtil.isEmpty(dateBookTimeList)) {
					return;
				}
				// 下面两for循环用于处理预约日列表的显示
				int i = 0;
				// 这个for循环用于找出第一个可预约的日期并选中,如果有则选中并跳出循环,如果没有则将此日期设置为不可用
				for (; i < dateBookTimeList.size(); i++) {
					DateBookTime dateBookTime = dateBookTimeList.get(i);
					if (dateBookTime.isBookable()) {// 选中第一个可预约的日期
						currentDateBookTime = dateBookTime;
						currentTimeGroupList = dateBookTime.getTimeGroupList();
						currentTimeItemsMap = dateBookTime.getGroupToItemsMap();
						if (checkedIndex != -1) {
							CheckBox cb = datePickList.get(checkedIndex);
							checkedIndex = -1;
							cb.setChecked(false);
						}
						datePickList.get(i).setChecked(true);
						fhlv_time_pick_list.setVisibility(View.VISIBLE);
						rl_more_time.setVisibility(View.VISIBLE);
//						rl_bottom.setVisibility(View.VISIBLE);
						btn_charge_or_comment.setEnabled(true);
						tv_no_time_split_message.setVisibility(View.GONE);
						break;
					} else {
						datePickList.get(i).setEnabled(false);
						datePickList.get(i).setChecked(false);
						fhlv_time_pick_list.setVisibility(View.GONE);
						rl_more_time.setVisibility(View.GONE);
//						rl_bottom.setVisibility(View.GONE);
						btn_charge_or_comment.setEnabled(false);
						tv_no_time_split_message.setVisibility(View.VISIBLE);
					}
				}
				// 这个for循环则用于处理上个for循环跳出之后的日期数据,可预约的日期则设为可用,不可预约则设为不可用
				for (; i < dateBookTimeList.size(); i++) {
					DateBookTime dateBookTime = dateBookTimeList.get(i);
					if (dateBookTime.isBookable()) {
						datePickList.get(i).setEnabled(true);
					} else {
						datePickList.get(i).setEnabled(false);
						datePickList.get(i).setChecked(false);
					}
				}
			}

		};
	}

	private void loadPileDetail() {
		WebAPIManager.getInstance().getPileById(pileId, new WebResponseHandler<Pile>() {
			@Override
			public void onStart() {
				super.onStart();
				loadState=0;
				if (!isBackgroundRefreshing) {
					loadingAdAnimationDrawable.start();
				} else {
					iv_background_loading.setVisibility(View.VISIBLE);
					backgroundLoadingAdAnimation.start();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadState > STATE_LOAD_COMPLETED) {
					if (!isBackgroundRefreshing) {
						loadingAdAnimationDrawable.stop();
						rl_loading.setVisibility(View.GONE);
						rl_loading_fail.setVisibility(View.GONE);
						rl_bottom.setVisibility(View.VISIBLE);
						iv_share.setVisibility(View.VISIBLE);
						pzs_center_container.setVisibility(View.VISIBLE);
					} else {
						backgroundLoadingAdAnimation.stop();
						iv_background_loading.setVisibility(View.GONE);
						isBackgroundRefreshing = false;
					}
					loadState = 0;
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (!isBackgroundRefreshing) {
					loadingAdAnimationDrawable.stop();
					rl_loading.setVisibility(View.GONE);
					rl_loading_fail.setVisibility(View.VISIBLE);
				} else {
					backgroundLoadingAdAnimation.stop();
					iv_background_loading.setVisibility(View.GONE);
					isBackgroundRefreshing = false;
				}
			}

			@Override
			public void onFailure(WebResponse<Pile> response) {
				super.onFailure(response);
				if (!isBackgroundRefreshing) {
					loadingAdAnimationDrawable.stop();
					rl_loading.setVisibility(View.GONE);
					rl_loading_fail.setVisibility(View.VISIBLE);
				} else {
					backgroundLoadingAdAnimation.stop();
					iv_background_loading.setVisibility(View.GONE);
					isBackgroundRefreshing = false;
				}
			}

			@Override
			public void onSuccess(WebResponse<Pile> response) {
				super.onSuccess(response);
				loadState++;
				if (response.getResultObj() == null) {
					return;
				}
//				iv_favor.setVisibility(View.VISIBLE);

				LayoutInflater inflater = LayoutInflater.from(self);
				pile = response.getResultObj();
				tv_center.setText(pile.getPileName());
				String coverImgUrl = pile.getCoverImg();
				tv_location.setText(pile.getLocation());

				tv_distance.setVisibility(View.VISIBLE);
				LatLng eLatLng = new LatLng(pile.getLatitude(), pile.getLongitude());
				CalculateUtil.infuseDistance(self, tv_distance, eLatLng);

				tv_charging_port_num.setText(pile.getChargerNum() + "个");
				tv_function_area.setText(pile.getFuncTypeAsStringID());
//				tv_message.setText(pile.getRunStatusAsStringID());

				if (pile.getPrice() != null) {
					tv_price.setText(CalculateUtil.formatDefaultNumber(pile.getPrice()));
					tv_price_sign.setVisibility(View.VISIBLE);
					tv_price_unit.setVisibility(View.VISIBLE);
				} else {
					tv_price.setText("");
					tv_price_sign.setVisibility(View.INVISIBLE);
					tv_price_unit.setVisibility(View.INVISIBLE);
				}
				int[] amenities = pile.getFacilitiesAsInt();
				if (amenities != null && amenities.length > 0) {
					for (int i : amenities) {
						if (i == MyConstant.AMENITIES_TYPE_WIFI) {
							ll_wifi.setVisibility(View.VISIBLE);
						}
						if (i == MyConstant.AMENITIES_TYPE_STORE) {
							ll_convenience_store.setVisibility(View.VISIBLE);
						}
						if (i == MyConstant.AMENITIES_TYPE_PARKING_SPACE) {
							ll_parking_space.setVisibility(View.VISIBLE);
						}
						if (i == MyConstant.AMENITIES_TYPE_CAMERA) {
							ll_camera.setVisibility(View.VISIBLE);
						}
						if (i == MyConstant.AMENITIES_TYPE_MARKET) {
							ll_market.setVisibility(View.VISIBLE);
						}
						if (i == MyConstant.AMENITIES_TYPE_RESTAURANT) {
							ll_restaurant.setVisibility(View.VISIBLE);
						}
						if (i == MyConstant.AMENITIES_TYPE_HOTEL) {
							ll_hotel.setVisibility(View.VISIBLE);
						}
					}
				} else {
					tv_nothing.setVisibility(View.VISIBLE);
				}
				tv_producer.setText(pile.getFactotryAsString());
				// 评分
				if (pile.getAvgLevel() != null) {
					String eva = String.format("%.1f", pile.getAvgLevel());
					tv_grade_data.setText(eva);
					eva_star_point.setEvaluate(pile.getAvgLevel());
					if (pile.getAcount() != null) {
						if (pile.getAcount() == 0) {
							rl_pile_grade.setEnabled(false);
						} else if (pile.getAcount() == 1) {
							rl_view_all_comment.setVisibility(View.GONE);
							rl_pile_grade.setEnabled(false);
						} else {
							rl_view_all_comment.setVisibility(View.VISIBLE);
							String str = self.getResources().getString(R.string.pile_comment_all, pile.getAcount());
							tv_grade_number.setText(Html.fromHtml(str));
							rl_pile_grade.setEnabled(true);
						}
					} else {
						rl_pile_grade.setEnabled(false);
					}
				}

				if(pile.getGprsType() == Pile.TYPE_BLUETOOTH){
					initNoGPRSPile();
					initTimeStting(startC.getTimeInMillis(), endC.getTimeInMillis(), duration);
					initDatePickList();
					loadTimeSpilt();
					setNoGPRSPile();
				}else{
					setGPRSPile();
					loadState++;
				}
				//电桩类型
				String pileType = MyApplication.getInstance().getPileTypeByKey(pile.getType());
				tv_pile_type.setText(pileType);
				if (pile.getType() != null && pile.getType() == Pile.TYPE_AC) {
					ll_electricity.setVisibility(View.GONE);
				}
				tv_owner_name.setText(pile.getOwnerNameAsString());
				tv_owner_phone_number.setText(pile.getContactPhoneAsString());
				if (pile.getRatedPower() == null || pile.getRatedVoltage() == null || pile.getRatedCurrent() == null) {
					rl_pile_meassage.setVisibility(View.GONE);
				} else {
					tv_power.setText(BigDecimal.valueOf(pile.getRatedPower()).stripTrailingZeros().toPlainString() + "kW");
					tv_voltage.setText(BigDecimal.valueOf(pile.getRatedVoltage()).stripTrailingZeros().toPlainString() + "V");
					tv_current.setText(BigDecimal.valueOf(pile.getRatedCurrent()).stripTrailingZeros().toPlainString() + "A");
				}

				if (EmptyUtil.isEmpty(pile.getDesp())) {
					tv_detail_data.setText(R.string.pile_detail_default);
				} else {
					tv_detail_data.setText(pile.getDesp());
				}
				// TODO begin bug collection
				if (isCollectedPile) {
					iv_ppp_favor.setImageResource(R.drawable.collect_heart_active);

				} else {
					if (isLogin()) {
						getCollectedTag();
					}
				}

				//图片加载
				if (!isBackgroundRefreshing) {
					ppp_photo.clearItems();
					ll_indicator.removeAllViews();
					if (!TextUtils.isEmpty(coverImgUrl)) {
						ppp_photo.loadPhoto(coverImgUrl);
						ll_indicator.addView(inflater.inflate(R.layout.iv_indicator, null));
					}
					List<String> imgUrls = pile.getIntroImgs();
					if (imgUrls != null && imgUrls.size() > 0) {
						for (String url : imgUrls) {
							ppp_photo.loadPhoto(url);
							ll_indicator.addView(inflater.inflate(R.layout.iv_indicator, null));
						}
					} else if (TextUtils.isEmpty(coverImgUrl)) {
						ppp_photo.loadPhoto("");
					}
					if (ppp_photo.getCount() > 0) {
						pzs_center_container.setZoomEnabled(true);
						pzs_center_container.setHideHeader(false);
						if (!(ppp_photo.getCount() > 1)) {
							ll_indicator.removeAllViews();
						} else {
							ll_indicator.getChildAt(0).setSelected(true);
						}
					}
				}
			}
		});
	}

	private void loadTimeSpilt() {
		Calendar startDate = (Calendar) currentC.clone();
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		Log.e("duration", "duration :" + duration);
		WebAPIManager.getInstance().loadPileTimeSplit(startDate.getTimeInMillis(), startC.getTimeInMillis(), endC.getTimeInMillis(), duration, pileId, 7,
				dateBookTimeHandler);
	}

	private void loadPileEvaluate() {
		if (isNetworkConnected()) {
			WebAPIManager.getInstance().getEvaluate(pileId, null, null, 0, 1, new WebResponseHandler<List<Command>>() {
				@Override
				public void onStart() {
					super.onStart();
				}

				@Override
				public void onFinish() {
					if (loadState > STATE_LOAD_COMPLETED) {
						if (!isBackgroundRefreshing) {
							loadingAdAnimationDrawable.stop();
							rl_loading.setVisibility(View.GONE);
							rl_loading_fail.setVisibility(View.GONE);
							rl_bottom.setVisibility(View.VISIBLE);
							iv_share.setVisibility(View.VISIBLE);
							pzs_center_container.setVisibility(View.VISIBLE);
						} else {
							backgroundLoadingAdAnimation.stop();
							iv_background_loading.setVisibility(View.GONE);
							isBackgroundRefreshing = false;
						}
						loadState = 0;
					}
				}

				@Override
				public void onSuccess(WebResponse<List<Command>> response) {
					super.onSuccess(response);
					loadState++;
					List<Command> commands = response.getResultObj();
					if (commands != null && commands.size() > 0) {
						rl_pile_grade.setEnabled(true);
						rl_user_comment.setVisibility(View.VISIBLE);
						Command command = commands.get(0);
						tv_user_name.setText(command.getUserName());
						tv_date_y_m.setText(TimeUtil.format(command.getCreateTime(), "yyyy-MM-dd"));
						tv_user_command_content.setText(command.getEvaluate());
						String url = "";
						if (command.getAvatarUrl() != null) {
							url = command.getAvatarUrl();
						}
						ImageLoaderManager.getInstance().displayImage(url, civ_user_image, option);
						eva_user_star_point.setEvaluate(command.getLevel());
					} else {
						rl_user_comment.setVisibility(View.GONE);
						rl_pile_grade.setEnabled(false);
					}
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					if (!isBackgroundRefreshing) {
						loadingAdAnimationDrawable.stop();
						rl_loading.setVisibility(View.GONE);
						rl_loading_fail.setVisibility(View.VISIBLE);
					} else {
						backgroundLoadingAdAnimation.stop();
						iv_background_loading.setVisibility(View.GONE);
						isBackgroundRefreshing = false;
					}
				}

				@Override
				public void onFailure(WebResponse<List<Command>> response) {
					super.onFailure(response);
					if (!isBackgroundRefreshing) {
						loadingAdAnimationDrawable.stop();
						rl_loading.setVisibility(View.GONE);
						rl_loading_fail.setVisibility(View.VISIBLE);
					} else {
						backgroundLoadingAdAnimation.stop();
						iv_background_loading.setVisibility(View.GONE);
						isBackgroundRefreshing = false;
					}
				}
			});
		}
	}

	private void setGPRSPile() {
//        rl_gprs_charging_time.setVisibility(View.VISIBLE);

		if (pile.getRunStatus() == Pile.STATE_PERSION_FREE) {
			tv_message.setText(R.string.idle);
			tv_message.setTextColor(getResources().getColor(R.color.sky_blue));
			ll_electricity.setVisibility(View.GONE);
		} else if (pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() == Pile.STATE_PERSION_CHARGE_FINISHED) {
			tv_message.setText(R.string.charging);
			tv_message.setTextColor(getResources().getColor(R.color.yellow));
			if (pile.getType() != null && pile.getType() == Pile.TYPE_DC) {
				ll_electricity.setVisibility(View.VISIBLE);
				tv_electricity_percent.setText(pile.getChargingProgress() + "%");
				sb_soc.setProgress(pile.getChargingProgress());
			} else {
				ll_electricity.setVisibility(View.GONE);
			}
		} else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT) {
			tv_message.setText(R.string.occupy);
			tv_message.setTextColor(getResources().getColor(R.color.yellow));
			ll_electricity.setVisibility(View.GONE);
		} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_BOOK) {
			tv_message.setText(R.string.idle);
			tv_message.setTextColor(getResources().getColor(R.color.sky_blue));
			ll_electricity.setVisibility(View.GONE);
		} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT) {
			tv_message.setText(R.string.maintenance);
			tv_message.setTextColor(getResources().getColor(R.color.gray));
			ll_electricity.setVisibility(View.GONE);
		} else if (pile.getRunStatus() == Pile.STATE_PERSION_GPRS_DISCONNECT) {
			tv_message.setText(R.string.offline);
			tv_message.setTextColor(getResources().getColor(R.color.gray));
			ll_electricity.setVisibility(View.GONE);
		} else {
			tv_message.setText(R.string.unknown);
			tv_message.setTextColor(getResources().getColor(R.color.gray));
			ll_electricity.setVisibility(View.GONE);
		}


//		if (pile.getRunStatus() == Pile.STATE_PERSION_FREE) {
//			tv_message.setText(R.string.idle);
//			tv_message.setTextColor(getResources().getColor(R.color.sky_blue));
//			ll_electricity.setVisibility(View.GONE);
//		} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT || pile.getRunStatus() == Pile
//				.STATE_PERSION_GPRS_DISCONNECT) {
//			tv_message.setText(R.string.maintenance);
//			tv_message.setTextColor(getResources().getColor(R.color.yellow));
//			ll_electricity.setVisibility(View.GONE);
//		} else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT || pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() == Pile
//				.STATE_PERSION_CHARGE_FINISHED) {
//			tv_message.setText(R.string.occupy);
//			tv_message.setTextColor(getResources().getColor(R.color.yellow));
//
//			ll_electricity.setVisibility(View.VISIBLE);
//			tv_electricity_percent.setText(pile.getChargingProgress() + "%");
//			sb_soc.setProgress(pile.getChargingProgress());
//		} else {
//			tv_message.setText(R.string.unknown);
//			tv_message.setTextColor(getResources().getColor(R.color.yellow));
//			ll_electricity.setVisibility(View.GONE);
//		}

		ll_date_pick_layout.setVisibility(View.GONE);
		rlyt_charge_time.setVisibility(View.GONE);
		fhlv_time_pick_list.setVisibility(View.GONE);
		rl_more_time.setVisibility(View.GONE);
		btn_charge_or_comment.setText(getString(R.string.immediately_charge));
	}

	private void setNoGPRSPile() {
//        rl_gprs_charging_time.setVisibility(View.GONE);

		ll_electricity.setVisibility(View.GONE);
		btn_charge_or_comment.setText(getString(R.string.book_charge));

		btn_charge_or_comment.setText(R.string.book_charge);
		// 设置是否可预约
		if (pile.getShareState() != null && pile.getShareState() == Pile.SHARE_STATUS_YES) {
			btn_charge_or_comment.setEnabled(true);
			tv_message.setText(R.string.had_shared);
			tv_message.setTextColor(getResources().getColor(R.color.sky_blue));
		} else {
			tv_message.setText(R.string.not_shared);
			tv_message.setTextColor(getResources().getColor(R.color.text_gray_light));
			btn_charge_or_comment.setEnabled(false);
			btn_charge_time.setVisibility(View.GONE);
			rlyt_charge_time.setVisibility(View.GONE);
			ll_price.setVisibility(View.INVISIBLE);
			ll_date_pick_layout.setVisibility(View.GONE);
			fhlv_time_pick_list.setVisibility(View.GONE);
			rl_more_time.setVisibility(View.GONE);
		}
	}

	private void getMember() {
		WebAPIManager.getInstance().getFamily(pileId, new WebResponseHandler<List<User>>(self) {

			@Override
			public void onSuccess(WebResponse<List<User>> response) {
				super.onSuccess(response);
				member = response.getResultObj();
			}

		});
	}

	private void getCollectedTag() {
		WebAPIManager.getInstance().getFavorById(sp.getString("user_id", ""), pileId, new WebResponseHandler<Boolean>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e, R.string.get_collectedtag_fail);
			}

			@Override
			public void onFailure(WebResponse<Boolean> response) {
				super.onFailure(response);
				TipsUtil.showTips(self, response, R.string.get_collectedtag_fail);
			}

			@Override
			public void onSuccess(WebResponse<Boolean> response) {
				super.onSuccess(response);
				if (response.getResultObj()) {
					iv_ppp_favor.setImageResource(R.drawable.collect_heart_active);
					isCollectedPile = true;
				} else {
					iv_ppp_favor.setImageResource(R.drawable.collect_heart);
					isCollectedPile = false;

				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_pile_info_pull_to_zoom);
		pzs_center_container = (PullToZoomScrollViewEx) findViewById(R.id.pzs_center_container);
		pzs_center_container.setZoomView(LayoutInflater.from(this).inflate(R.layout.layout_photo_pager, null));
		pzs_center_container.setScrollContentView(LayoutInflater.from(this).inflate(R.layout.layout_pile_info, null));
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		LayoutInflater.from(this).inflate(R.layout.layout_pile_info_bottom, rl_bottom);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		rl_loading_fail = (RelativeLayout) findViewById(R.id.rl_loading_fail);
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		mScreenHeight = localDisplayMetrics.heightPixels;
		mScreenWidth = localDisplayMetrics.widthPixels;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mScreenWidth, (int) getResources().getDimension(R.dimen.w180));
		pzs_center_container.setHeaderLayoutParams(layoutParams);
		// topBar控件
		iv_left = (ImageView) findViewById(R.id.iv_left);// 左边返回按钮
		iv_edit_grade = (ImageView) findViewById(R.id.iv_edit_grade);// 右边编辑按钮
		iv_favor = (ImageView) findViewById(R.id.iv_favor);// 右边心形按钮
		iv_share = (ImageView) findViewById(R.id.iv_share);// 右边分享按钮
		tv_center = (TextView) findViewById(R.id.tv_center);// 中间title
		// 电桩信息栏控件
		tv_pile_type = (TextView) pzs_center_container.findViewById(R.id.tv_pile_type);
		tv_location = (TextView) pzs_center_container.findViewById(R.id.tv_location);// 位置
		tv_distance = (TextView) pzs_center_container.findViewById(R.id.tv_distance);// 距离
		ll_price = (LinearLayout) pzs_center_container.findViewById(R.id.ll_price);// 费率layout
		tv_price = (TextView) pzs_center_container.findViewById(R.id.tv_price);// 费用
		tv_price_sign = (TextView) pzs_center_container.findViewById(R.id.tv_price_sign);// 费用
		tv_price_unit = (TextView) pzs_center_container.findViewById(R.id.tv_price_unit);// 费用
		//GPRS充电时间
		ll_electricity = (LinearLayout) pzs_center_container.findViewById(R.id.ll_electricity);
		sb_soc = (SeekBar) pzs_center_container.findViewById(R.id.sb_soc);
		sb_soc.setEnabled(false);
		tv_electricity_percent = (TextView) pzs_center_container.findViewById(R.id.tv_electricity_percent);
		// 充电信息栏
		rlyt_charge_time = (RelativeLayout) pzs_center_container.findViewById(R.id.rlyt_charge_time);// 充电时间layout
		btn_charge_time = (Button) pzs_center_container.findViewById(R.id.btn_charge_time);
		// 充电日期选择
		ll_date_pick_layout = (LinearLayout) pzs_center_container.findViewById(R.id.ll_date_pick_layout);// 日期选择栏layout
		ll_date_pick = (LinearLayout) pzs_center_container.findViewById(R.id.ll_date_pick);// 日期选择栏
		tv_no_time_split_message = (TextView) pzs_center_container.findViewById(R.id.tv_no_time_split_message);// 没有可预约时间段的文字信息
		// 充电时间段选择
		fhlv_time_pick_list = (FixHeightListView) pzs_center_container.findViewById(R.id.fhlv_time_pick_list);// 充电时间选择栏
		rl_more_time = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_more_time);// 更多充电时间按钮
		tv_more_time = (TextView) pzs_center_container.findViewById(R.id.tv_more_time);// 更多充电时间文字
		iv_more_time_arrow = (ImageView) pzs_center_container.findViewById(R.id.iv_more_time_arrow);// 更多充电时间箭头
		// 评分栏
		rl_pile_grade = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_pile_grade);// 评分Layout
		tv_grade = (TextView) pzs_center_container.findViewById(R.id.tv_grade);//评分标题
		eva_star_point = (EvaluateColumn) pzs_center_container.findViewById(R.id.eva_star_point);// 星星栏
		tv_grade_number = (TextView) pzs_center_container.findViewById(R.id.tv_grade_number);// 评分人数
		tv_grade_data = (TextView) pzs_center_container.findViewById(R.id.tv_grade_data);// 评分
		iv_grade_arrow = (ImageView) pzs_center_container.findViewById(R.id.iv_grade_arrow);// 右侧箭头
		civ_user_image = (ImageView) pzs_center_container.findViewById(R.id.civ_user_image);//用户头像
		rl_user_comment = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_user_comment);//用户评价
		tv_user_name = (TextView) pzs_center_container.findViewById(R.id.tv_user_name);//用户名
		eva_user_star_point = (EvaluateColumn) pzs_center_container.findViewById(R.id.eva_user_star_point);// 星星栏
		tv_date_y_m = (TextView) pzs_center_container.findViewById(R.id.tv_date_y_m);//评价年月
		tv_user_command_content = (TextView) pzs_center_container.findViewById(R.id.tv_user_command_content);//评价内容
		rl_view_all_comment = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_view_all_comment);// 评分查看所有评价

		// 所有者
		rl_pile_owner = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_pile_owner);// 拥有者layout
		tv_owner_detail = (TextView) pzs_center_container.findViewById(R.id.tv_owner_detail);// 所有者标题
		tv_owner_name = (TextView) pzs_center_container.findViewById(R.id.tv_owner_name);// 所有者名字
		tv_owner_phone_number = (TextView) pzs_center_container.findViewById(R.id.tv_owner_phone_number);// 所有者联系方式
		iv_phone_icon = (ImageView) pzs_center_container.findViewById(R.id.iv_phone_icon);// 电话icon
		tv_producer = (TextView) pzs_center_container.findViewById(R.id.tv_producer);//生产厂家
		// 电桩信息
		rl_pile_meassage = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_pile_meassage);// 电桩信息
		tv_message = (TextView) pzs_center_container.findViewById(R.id.tv_message);// 公共桩信息
		tv_power = (TextView) pzs_center_container.findViewById(R.id.tv_power);// 功率
		tv_voltage = (TextView) pzs_center_container.findViewById(R.id.tv_voltage);// 电压
		tv_current = (TextView) pzs_center_container.findViewById(R.id.tv_current);// 电流
		tv_charging_port_num = (TextView) pzs_center_container.findViewById(R.id.tv_charging_port_num);
		tv_function_area = (TextView) pzs_center_container.findViewById(R.id.tv_function_area);
		ll_wifi = (LinearLayout) pzs_center_container.findViewById(R.id.ll_wifi);
		ll_convenience_store = (LinearLayout) pzs_center_container.findViewById(R.id.ll_convenience_store);
		ll_parking_space = (LinearLayout) pzs_center_container.findViewById(R.id.ll_parking_space);
		ll_camera = (LinearLayout) pzs_center_container.findViewById(R.id.ll_camera);
		ll_market = (LinearLayout) pzs_center_container.findViewById(R.id.ll_market);
		ll_restaurant = (LinearLayout) pzs_center_container.findViewById(R.id.ll_restaurant);
		ll_hotel = (LinearLayout) pzs_center_container.findViewById(R.id.ll_hotel);
		tv_nothing = (TextView) pzs_center_container.findViewById(R.id.tv_nothing);

		// 电桩详细说明
		rl_pile_detail = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_pile_detail);
		tv_deail = (TextView) pzs_center_container.findViewById(R.id.tv_detail);
		tv_detail_data = (TextView) pzs_center_container.findViewById(R.id.tv_detail_data);
		// 电桩图片
		ll_indicator = (LinearLayout) pzs_center_container.findViewById(R.id.ll_indicator);
		ppp_photo = (PilePhotoPager) pzs_center_container.findViewById(R.id.ppp_photo);
		iv_ppp_favor = (ImageView) pzs_center_container.findViewById(R.id.iv_ppp_favor);
		iv_background_loading = (ImageView) pzs_center_container.findViewById(R.id.iv_background_loading);
		backgroundLoadingAdAnimation = (AnimationDrawable) iv_background_loading.getBackground();
		btn_charge_or_comment = (Button) findViewById(R.id.btn_charge_or_comment);// 立即充电(预约)按钮

		btn_navigation = (Button) findViewById(R.id.btn_navigation);// 导航按钮
		btn_try_to_refresh = (Button) findViewById(R.id.btn_try_to_refresh);//重试按钮
		iv_loading = (ImageView) findViewById(R.id.iv_loading);
		loadingAdAnimationDrawable = (AnimationDrawable) iv_loading.getBackground();

		iv_edit_grade.setVisibility(View.GONE);
		tv_center.requestFocus();
		tv_center.setText("");

		gprsPileDurationDialog = new GPRSPileDurationDialog(this);
		durationDialog = new DurationDialog(this);
		timePickAdapter = new TimePickAdapter<>(this, new ArrayList<adapterItem>());
		updateDuration(duration);
		fhlv_time_pick_list.setAdapter(timePickAdapter);
		iv_share.setVisibility(View.GONE);
		iv_favor.setVisibility(View.GONE);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		iv_left.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		iv_edit_grade.setOnClickListener(this);
		iv_ppp_favor.setOnClickListener(this);
		rl_pile_grade.setOnClickListener(this);
		btn_charge_or_comment.setOnClickListener(this);
		btn_navigation.setOnClickListener(this);
		btn_try_to_refresh.setOnClickListener(this);
		rl_more_time.setOnClickListener(this);
		iv_phone_icon.setOnClickListener(this);
		btn_charge_time.setOnClickListener(this);
		durationDialog.setListener(new DurationDialog.OnSelectDurationListener() {
			@Override
			public void onSelected(double hour) {
				updateDuration(hour);
				loadTimeSpilt();
			}
		});
		pzs_center_container.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
			@Override
			public void onPullZooming(int newScrollValue) {

			}

			@Override
			public void onPullZoomEnd() {
				if (!isBackgroundRefreshing) {
					isBackgroundRefreshing = true;
					loadPileDetail();
					loadPileEvaluate();
					if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
						loadTimeSpilt();
					} else {
						loadState++;
					}
				}
			}
		});

		ppp_photo.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (ll_indicator != null) {
					int childCount = ll_indicator.getChildCount();
					for (int i = 0; i < childCount; i++) {
						ll_indicator.getChildAt(i).setSelected(false);
					}
					ll_indicator.getChildAt(position).setSelected(true);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void updateDuration(double duration) {

		if (duration <= 0) {
			this.duration = DEFAULT_DURATION;
		} else {
			this.duration = duration;
		}
		if (this.duration < 1) {
			btn_charge_time.setText(BigDecimal.valueOf(this.duration * 60).stripTrailingZeros().toPlainString() + "分钟");
		} else {
			btn_charge_time.setText(BigDecimal.valueOf(this.duration).stripTrailingZeros().toPlainString() + "小时");
		}
	}

	/**
	 * 初始化日期选择栏的数据
	 */
	private void initDatePickList() {
		dateChangeListener = new datePickChangeListener();
		datePickList = new ArrayList<>(ll_date_pick.getChildCount());
		Calendar value = (Calendar) currentC.clone();
		// 由于ll_date_pick中checkBox在偶数项View中,所以每次加2
		for (int i = 0; i < ll_date_pick.getChildCount(); i += 2) {
			CheckBox cb = (CheckBox) ll_date_pick.getChildAt(i);
			int month = value.get(Calendar.MONTH) + 1;
			int dayOfMonth = value.get(Calendar.DAY_OF_MONTH);
			int dayOfWeek = value.get(Calendar.DAY_OF_WEEK);
			String monthDay = month + "/" + dayOfMonth;
			String weekDay = "";

			if (i == 0) {
				weekDay = this.getResources().getString(R.string.today);
			} else {
				switch (dayOfWeek) {
					case Calendar.SUNDAY: {
						weekDay = this.getResources().getString(R.string._sunday);
						break;
					}
					case Calendar.MONDAY: {
						weekDay = this.getResources().getString(R.string._monday);
						break;
					}

					case Calendar.THURSDAY: {
						weekDay = this.getResources().getString(R.string._thursday);
						break;
					}
					case Calendar.WEDNESDAY: {
						weekDay = this.getResources().getString(R.string._wednesday);
						break;
					}
					case Calendar.TUESDAY: {
						weekDay = this.getResources().getString(R.string._thuesday);
						break;
					}
					case Calendar.FRIDAY: {
						weekDay = this.getResources().getString(R.string._friday);
						break;
					}
					case Calendar.SATURDAY: {
						weekDay = this.getResources().getString(R.string._saturday);
						break;
					}
				}
			}
			String cbStr = this.getResources().getString(R.string.book_pile_date_pick, weekDay, monthDay);
			SpannableString sbs = new SpannableString(cbStr);
			sbs.setSpan(new TextAppearanceSpan(self, R.style.style_timepicker_big), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			sbs.setSpan(new TextAppearanceSpan(self, R.style.style_timepicker_small), 2, sbs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			cb.setText(sbs, CheckBox.BufferType.SPANNABLE);
//			cb.setText(cbStr);
			cb.setOnCheckedChangeListener(dateChangeListener);
			cb.setEnabled(false);
			datePickList.add(cb);
			value.add(Calendar.DAY_OF_YEAR, 1);// 每次增长1天
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		option = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
				getResources().getDimension(R.dimen.h30))).build();

		pzs_center_container.setVisibility(View.INVISIBLE);
		rl_bottom.setVisibility(View.GONE);
		rl_loading.setVisibility(View.VISIBLE);
		rl_view_all_comment.setVisibility(View.GONE);
		checkedIndex = -1;
		loadPileDetail();
		loadPileEvaluate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/** Joke 如果用户没有登录，在登录后返回这个界面时重新获取userId **/
		userId = sp.getString("user_id", null);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
			case R.id.iv_left: {
				back();
				break;
			}
			case R.id.iv_edit_grade: {
				/**
				 * 个人桩暂时不能直接评价，只能进行订单后评价，所以屏蔽了按钮，这段代码不会执行
				 */
				if (isLogin()) {
					Intent goGrade = new Intent(this, MultipleEvaluateActivity.class);
					goGrade.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
					goGrade.putExtra(KEY.INTENT.PILE_NAME, pile.getPileName());
					goGrade.putExtra(KEY.INTENT.EVALUATE_TYPE, MultipleEvaluateActivity.EVALUATE_TYPE_PILE);
					startActivityForResult(goGrade, REQUEST_REFRESH);
				} else {
					Intent intentLogin = new Intent(this, LoginActivity.class);
					startActivity(intentLogin);
				}

				break;
			}
			case R.id.iv_share: {

				WebAPIManager.getInstance().getDownloadQrCode(new WebResponseHandler<String>() {
					@Override
					public void onStart() {
						super.onStart();
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						loadingDialog = new LoadingDialog(self, R.string.loading);
						loadingDialog.show();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
					}

					@Override
					public void onSuccess(WebResponse<String> response) {
						super.onSuccess(response);
						String url = response.getResult();
						if (!TextUtils.isEmpty(url)) {
							url = url.replaceAll("\"", "");
							ShareApiManager.oneKeyShareDownloadImage(PileInfoNewActivity.this, url);
						}
					}
				});
				break;
			}
			case R.id.btn_charge_or_comment: {
				if (isLogin()) {
					if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
						if (pile.getUserid().equals(userId)) {
							ToastUtil.show(self, "不能预约自己的桩");
						} else if (isMember()) {
							ToastUtil.show(self, "家人账户无须预约");
						} else {
							submit();
						}
					} else {
						//GPRS桩跳至扫码充电
						for (Activity activity : EasyActivityManager.getInstance().getAllActivitys()) {
							if (activity instanceof SearchPileAndAddressActivity) {
								activity.finish();
							}
						}
						sendBroadcast(new Intent(KEY.ACTION.ACTION_IMMEDIATELY_CHARGE));
						Intent intent = getIntent() ;
						setResult(1,intent);
						finish();
					}
				} else {
					Intent intentLogin = new Intent(this, LoginActivity.class);
					startActivity(intentLogin);
				}

				break;
			}
			case R.id.iv_ppp_favor: {
				if (isCollectedPile) {
					cancelCollect();
				} else {
					collect();
				}

				break;
			}
			case R.id.iv_phone_icon: {
				IntentUtil.openTelephone(this, tv_owner_phone_number.getText().toString());
				break;
			}
			case R.id.btn_order: {
				// 下订单
				if (isLogin()) {
					if (pile.getUserid().equals(userId)) {
						ToastUtil.show(self, "不能预约自己的桩");
					} else if (isMember()) {
						ToastUtil.show(self, "家人账户无须预约");
					} else {
						submit();
					}
				} else {
					Intent intentLogin = new Intent(this, LoginActivity.class);
					startActivity(intentLogin);
				}

				break;
			}
			case R.id.btn_charge_time: {
				durationDialog.show();
				break;
			}
			case R.id.btn_navigation: {
				callNavigation();
				break;
			}
			case R.id.btn_try_to_refresh: {
				loadPileDetail();
				loadPileEvaluate();
				break;
			}
			case R.id.rlyt_charge_time: {
//				selectChargeTime();
				break;
			}
			case R.id.rl_pile_grade: {
				if (isNetworkConnected()) {
					Intent intent = new Intent(this, SpecifiedEvaluateAndMomentsActivity.class);
					intent.putExtra(SpecifiedEvaluateAndMomentsActivity.KEY_SPECIIED_TYPE, SpecifiedEvaluateAndMomentsActivity.SPECIIED_TYPE_PILE_OR_STATION);
					intent.putExtra(KEY.INTENT.PILE_NAME, pile.getPileName());
					intent.putExtra(KEY.INTENT.PILE_ID, pileId);
					startActivityForResult(intent,REQUEST_REFRESH);
				} else {
					return;
				}
				break;
			}
			case R.id.rl_more_time: {
				timePickAdapter.clear();
				if (isShowingMoreTime) {// 当前处于展示所有时间的状态,则收起
					for (TimeGroup timeGroup : currentTimeGroupList) {
						if (timeGroup.isBookable()) {
							timePickAdapter.addAll(getAdapterItemListWithOutGroup(true, 1, currentTimeGroupList.toArray(new TimeGroup[0])));
							break;
						}
					}
					isShowingMoreTime = false;
					tv_more_time.setText(R.string.charge_time_more);
					iv_more_time_arrow.setImageResource(R.drawable.triangle_down_white);
				} else {// 当前处于收起状态,则展开
					timePickAdapter.addAll(getAdapterItemListWithOutGroup(true, 0, currentTimeGroupList.toArray(new TimeGroup[0])));
					isShowingMoreTime = true;
					tv_more_time.setText(R.string.charge_time_flod);
					iv_more_time_arrow.setImageResource(R.drawable.triangle_up_white);
				}
				break;
			}
//            case R.id.rl_gprs_charging_time:
//                gprsPileDurationDialog.show();
//                break;
			default:
				break;
		}
	}

	private boolean isMember() {
		if (member == null) {
			return false;
		}

		for (Iterator iterator = member.iterator(); iterator.hasNext(); ) {
			User user = (User) iterator.next();
			if (user.getUserid().equals(userId)) {
				// return true;
				return false;
			}
		}
		return false;
	}

	private void collect() {
		if (pile == null) {
			return;
		}

		String userid = sp.getString("user_id", null);
		if (!isLogin() || userid == null) {
			Intent intentLogin = new Intent(this, LoginActivity.class);
			startActivity(intentLogin);
			return;
		}

		WebAPIManager.getInstance().addFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(self) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				ToastUtil.show(self, R.string.collect_fail);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				ToastUtil.show(self, R.string.collect_fail);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(self, R.string.collect_success);
				// setResult(RESULT_OK);
				iv_ppp_favor.setImageResource(R.drawable.collect_heart_active);
				isCollectedPile = true;
//				Intent intent = new Intent(KEY.ACTION.ACTION_REFRESH_PILE_LIST);
//				intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
//				intent.putExtra(KEY.INTENT.IS_FAVOR, PileStation.FAVOR_YES);
//				sendBroadcast(intent);
			}

			@Override
			public void onStart() {
				super.onStart();
				loadingDialog = new LoadingDialog(self, R.string.loading);
				loadingDialog.showDialog();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadingDialog != null) loadingDialog.dismiss();
			}
		});
	}

	private void cancelCollect() {
		if (pile == null) {
			return;
		}

		String userid = sp.getString("user_id", null);
		if (!isLogin() || userid == null) {
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.putExtra("isMainActivity", false);
			startActivityForResult(loginIntent, 9);
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			return;
		}

		WebAPIManager.getInstance().removeFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(self) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				ToastUtil.show(self, R.string.cancel_collect_fail);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				ToastUtil.show(self, R.string.cancel_collect_fail);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(self, R.string.cancel_collect_success);
				iv_ppp_favor.setImageResource(R.drawable.collect_heart);
				isCollectedPile = false;
//				Intent intent = new Intent(KEY.ACTION.ACTION_REFRESH_PILE_LIST);
//				intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
//				intent.putExtra(KEY.INTENT.IS_FAVOR, PileStation.FAVOR_NOT);
//				sendBroadcast(intent);
				// setResult(RESULT_OK);
			}

			@Override
			public void onStart() {
				super.onStart();
				loadingDialog = new LoadingDialog(self, R.string.loading);
				loadingDialog.showDialog();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadingDialog != null) loadingDialog.dismiss();
			}
		});
	}

	private boolean isLogin() {
		return sp.getBoolean("isLogin", false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
			case PileFindFragment.REQUEST_TIME: {
				long startTime = data.getLongExtra(KEY.INTENT.START_TIME, -1);
				long endTime = data.getLongExtra(KEY.INTENT.END_TIME, -1);
				double duration = data.getDoubleExtra(KEY.INTENT.DURATION, DEFAULT_DURATION);
				updateDuration(duration);
				initTimeStting(startTime, endTime, duration);
				timePickAdapter.clear();
				if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
					loadTimeSpilt();
				}
				break;
			}
			case REQUEST_REFRESH:
				Log.i("tag", "num=======0");
				loadPileDetail();
				loadPileEvaluate();
				if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
					loadTimeSpilt();
				}
				break;
			default:
				break;
		}
	}

	private void initTimeStting(long startTime, long endTime, double duration) {
		this.duration = duration;
		int hour = (int) duration;
		int min = (int) ((duration - hour) * 60);
		startC.setTimeInMillis(startTime);
		String time = "";
		if (startTime > -1) {
			time = sdf.format(startC.getTime());
		}
		endC.setTimeInMillis(endTime);
		if (endTime > -1) {
			time += " - ";
			int minutes = endC.get(Calendar.HOUR_OF_DAY) * 60 + endC.get(Calendar.MINUTE);
			// 如果结束时间是0点证明是第二天的0点，这里显示为24点
			if (minutes == 0) {
				time += "24:00";
			} else {
				time += sdf.format(endC.getTime());
			}
		}
//		tv_time.setText(time);
//		this.duration = duration;
//		if (duration > -1) {
//			if (hour == 0) {
//				tv_duration.setText(min + "m");
//			} else if (min == 0) {
//				tv_duration.setText(hour + "h");
//			} else {
//				tv_duration.setText(hour + "h" + min + "m");
//			}
//		} else {
//			tv_duration.setText("");
//		}
	}

	private void submit() {
		if (!isBookingPile) {
			bookingStartC.set(Calendar.YEAR, dateC.get(Calendar.YEAR));
			bookingStartC.set(Calendar.MONTH, dateC.get(Calendar.MONTH));
			bookingStartC.set(Calendar.DAY_OF_MONTH, dateC.get(Calendar.DAY_OF_MONTH));

			bookingEndC.set(Calendar.YEAR, dateC.get(Calendar.YEAR));
			bookingEndC.set(Calendar.MONTH, dateC.get(Calendar.MONTH));
			bookingEndC.set(Calendar.DAY_OF_MONTH, dateC.get(Calendar.DAY_OF_MONTH));
			// 当结束时间早于开始时间,那么表明结束时间实际上是次日的,所以加1日期
			if (bookingEndC.before(bookingStartC)) {
				bookingEndC.add(Calendar.DAY_OF_MONTH, 1);
			}
			WebAPIManager.getInstance().addOrder(pile.getPileId(), userId, bookingStartC.getTimeInMillis(), bookingEndC.getTimeInMillis(), new
					WebResponseHandler<ChargeOrder>(self) {

				@Override
				public void onStart() {
					super.onStart();
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
					isBookingPile = true;
//				btn_charge_or_comment.setEnabled(false);
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
				}

				@Override
				public void onFailure(WebResponse<ChargeOrder> response) {
					super.onFailure(response);
					TipsUtil.showTips(self, response);
					if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
						loadTimeSpilt();
					}
				}

				@Override
				public void onSuccess(WebResponse<ChargeOrder> response) {
					super.onSuccess(response);
					if (response.getResultObj() != null && pile != null) {
						Intent orderAddSuccess = new Intent(self, OrderAddSuccessActivity.class);
						orderAddSuccess.putExtra(KEY.INTENT.PILE, pile);
						orderAddSuccess.putExtra(KEY.INTENT.CHARGE_ORDER, response.getResultObj());
						orderAddSuccess.putExtra(KEY.INTENT.IS_PILE_COLLECTED, isCollectedPile);
						startActivity(orderAddSuccess);
						finish();
					}
				}

				@Override
				public void onFinish() {
					super.onFinish();
					if (loadingDialog != null) loadingDialog.dismiss();
//				btn_charge_or_comment.setEnabled(true);
					isBookingPile = false;
				}
			});
		}
	}

	private class datePickChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int newIndex = datePickList.indexOf(buttonView);
			if (isChecked && checkedIndex != newIndex) {// 当chackButton由非选中状态转向选中状态时
				if (checkedIndex != -1) {// 当前有有已选中的item,把它设为非选中状态
					CheckBox cb = datePickList.get(checkedIndex);// 先取出来
					checkedIndex = newIndex;// 再设定当前点击index
					cb.setChecked(false);// 再把之前的checkBox置false
				} else {
					checkedIndex = newIndex;
				}
				dateC.set(Calendar.YEAR, currentC.get(Calendar.YEAR));
				dateC.set(Calendar.MONTH, currentC.get(Calendar.MONTH));
				dateC.set(Calendar.DAY_OF_YEAR, currentC.get(Calendar.DAY_OF_YEAR));
				dateC.add(Calendar.DAY_OF_YEAR, newIndex);
				if (dateBookTimeList != null && dateBookTimeList.size() > checkedIndex) {
					currentDateBookTime = dateBookTimeList.get(checkedIndex);
					currentTimeGroupList = currentDateBookTime.getTimeGroupList();
					currentTimeItemsMap = currentDateBookTime.getGroupToItemsMap();
					timePickAdapter.clear();
					for (TimeGroup timeGroup : currentTimeGroupList) {
						if (timeGroup.isBookable()) {
							timePickAdapter.addAll(getAdapterItemListWithOutGroup(true, 1, currentTimeGroupList.toArray(new TimeGroup[0])));
							break;
						}
					}
					tv_more_time.setText(R.string.charge_time_more);
					iv_more_time_arrow.setImageResource(R.drawable.triangle_down_white);
					isShowingMoreTime = false;
				}
			} else {
				CheckBox cb = datePickList.get(newIndex);
				if (checkedIndex == newIndex && cb.isEnabled()) {
					datePickList.get(checkedIndex).setChecked(true);
					return;
				}
			}
		}
	}

	/**
	 * 根据所提供的可预约时间段,生成时间段选择界面适配器的item,每个item显示两个时间段
	 *
	 * @param isShowTimeGroup 是否显示时间组信息在item的开头,只用于每个时间组的第一个item的开头
	 * @param onlyShowFree    是否只显示空闲的时间段
	 * @param maxCount        最大的显示item数量 0代表无限制
	 * @param timeGroups      时间组
	 * @return
	 */
	private List<adapterItem> getAdapterItemList(boolean isShowTimeGroup, boolean onlyShowFree, int maxCount, TimeGroup... timeGroups) {
		List<adapterItem> adapterItemList = new ArrayList<>();
		boolean setSelected = false;
		for (TimeGroup timeGroup : timeGroups) {
			Set<TimeItem> items = currentTimeItemsMap.get(timeGroup);
			Iterator<TimeItem> iter = items.iterator();
			while (!setSelected && iter.hasNext()) {
				TimeItem item = iter.next();
				if (item.getItemState() == TimeItem.ITEM_STATE_FREE) {
					item.setSelected(true);
					setSelected = true;
					break;
				}
			}
			iter = items.iterator();
			if (onlyShowFree) {// 把时间段中非free的去掉
				Set<TimeItem> freeItems = new TreeSet<>();
				while (iter.hasNext()) {
					TimeItem item = iter.next();
					if (item.getItemState() == TimeItem.ITEM_STATE_FREE) {
						freeItems.add(item);
					}
				}
				iter = freeItems.iterator();
			}
			if (iter.hasNext()) {// 如果迭代器非空
				adapterItem item = new adapterItem();
				TimeItem timeItme;
				// 首个item,设定时间组的第一个item
				StringBuilder timeGroupStrb = new StringBuilder();
				timeGroupStrb.append(timeGroup.getStartTime()).append("-");

				if (timeGroup.getEndTime().equals("00:00")) {
					timeGroupStrb.append("24:00");
				} else {
					timeGroupStrb.append(timeGroup.getEndTime());
				}
				item.isShowTimeGroup(true);
				item.setTimeGroup(timeGroupStrb.toString());
				if (iter.hasNext()) {
					timeItme = iter.next();
					item.setLeftItem(timeItme);
				}
				if (iter.hasNext()) {
					timeItme = iter.next();
					item.setRightItem(timeItme);
				}
				item.isShowTimeGroup(isShowTimeGroup);
				adapterItemList.add(item);
				// 时间组的后续item
				while (iter.hasNext()) {// 每个item两个时间段
					item = new adapterItem();
					timeItme = iter.next();// 第一个时间段,在左边
					item.setLeftItem(timeItme);
					if (iter.hasNext()) {// 第二个时间段,如果有的话,在右边
						timeItme = iter.next();
						item.setRightItem(timeItme);
					}
					adapterItemList.add(item);
				}
			}

		}
		if (maxCount != 0) {// 根据给定的最大item数量进行调整
			adapterItemList = adapterItemList.size() > maxCount ? adapterItemList.subList(0, maxCount) : adapterItemList;
		}
		return adapterItemList;
	}

	/**
	 * 生成不区分时间组的时间段列表
	 *
	 * @param onlyShowFree 是否只显示空闲的时间段
	 * @param maxCount     最大的显示item数量,0代表无限制
	 * @param timeGroups   时间组
	 * @return
	 */
	private List<adapterItem> getAdapterItemListWithOutGroup(boolean onlyShowFree, int maxCount, TimeGroup... timeGroups) {
		List<adapterItem> adapterItemList = new ArrayList<>();
//		boolean setSelected = false;
		Set<TimeItem> items = new TreeSet<>();
		for (TimeGroup timeGroup : timeGroups) {
			items.addAll(currentTimeItemsMap.get(timeGroup));
		}
		Iterator<TimeItem> iter = items.iterator();

		while (/*!setSelected && */iter.hasNext()) {
			TimeItem item = iter.next();
			if (item.getItemState() == TimeItem.ITEM_STATE_FREE) {
				item.setSelected(true);
//				setSelected = true;
				break;
			}
		}
		iter = items.iterator();
		if (onlyShowFree) {// 把时间段中非free的去掉
			Set<TimeItem> freeItems = new TreeSet<>();
			while (iter.hasNext()) {
				TimeItem item = iter.next();
				if (item.getItemState() == TimeItem.ITEM_STATE_FREE) {
					freeItems.add(item);
				}
			}
			iter = freeItems.iterator();
		}
		adapterItem item;
		TimeItem timeItme;
		while (iter.hasNext()) {// 每个item两个时间段
			item = new adapterItem();
			timeItme = iter.next();// 第一个时间段,在左边
			item.setLeftItem(timeItme);
			if (iter.hasNext()) {// 第二个时间段,如果有的话,在右边
				timeItme = iter.next();
				item.setRightItem(timeItme);
			}
			adapterItemList.add(item);
		}

		if (maxCount != 0) {// 根据给定的最大item数量进行调整
			adapterItemList = adapterItemList.size() > maxCount ? adapterItemList.subList(0, maxCount) : adapterItemList;
		}
		return adapterItemList;
	}

	private void selectChargeTime() {
		Intent timeIntent = new Intent(self, TimeCircleActivity.class);
		long startTime = startC.getTimeInMillis();
		long endTime = endC.getTimeInMillis();
		long dateTime = dateC.getTimeInMillis();
		timeIntent.putExtra(KEY.INTENT.DATE_TIME, dateTime);
		if (startTime > -1) {
			timeIntent.putExtra(KEY.INTENT.START_TIME, startTime);
		}
		if (endTime > -1) {
			timeIntent.putExtra(KEY.INTENT.END_TIME, endTime);
		}
		if (duration > -1) {
			timeIntent.putExtra(KEY.INTENT.DURATION, duration);
		}
		self.startActivityForResult(timeIntent, PileFindFragment.REQUEST_TIME);
	}

	public static class adapterItem {
		private TimeItem leftItem;
		private TimeItem rightItem;
		private boolean isShowTimeGroup;
		private String timeGroup;

		public adapterItem() {
		}

		;

		public adapterItem(TimeItem leftItem, TimeItem rightItem, boolean isShowTimeGroup) {
			this.leftItem = leftItem;
			this.rightItem = rightItem;
			this.isShowTimeGroup = isShowTimeGroup;
		}

		public TimeItem getLeftItem() {
			return leftItem;
		}

		public void setLeftItem(TimeItem leftItem) {
			this.leftItem = leftItem;
		}

		public TimeItem getRightItem() {
			return rightItem;
		}

		public void setRightItem(TimeItem rightItem) {
			this.rightItem = rightItem;
		}

		public boolean isShowTimeGroup() {
			return isShowTimeGroup;
		}

		public void isShowTimeGroup(boolean isFirstOfTheGroup) {
			this.isShowTimeGroup = isFirstOfTheGroup;
		}

		public String getTimeGroup() {
			return timeGroup;
		}

		public void setTimeGroup(String timeGroup) {
			this.timeGroup = timeGroup;
		}
	}

	@Override
	public void setBookingStartTimeEndTime(Calendar startC, Calendar endC) {
		bookingStartC.setTimeInMillis(startC.getTimeInMillis());
		bookingEndC.setTimeInMillis(endC.getTimeInMillis());
	}

	/**
	 * 定位并调百度地图导航;
	 */
	private void callNavigation() {

		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (isFinishing()) {
					return;
				}
				if (location != null) {
					mLocation = location;
					WaterBlueDialogComfirmNav waterBlueDialogComfirmNav = new WaterBlueDialogComfirmNav(PileInfoNewActivity.this);
					waterBlueDialogComfirmNav.show(mLocation.getLatitude(), mLocation.getLongitude(), pile.getLatitude(), pile.getLongitude());
				} else {
					ToastUtil.show(PileInfoNewActivity.this, "定位失败,请重试");
				}
			}

		});
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		back();
	}

	private void back() {
		if (pile != null) {
			Intent intent = new Intent();
			intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
			intent.putExtra(KEY.INTENT.POSITION, position);
			intent.putExtra(KEY.INTENT.IS_FAVOR, isCollectedPile);
			intent.putExtra(KEY.INTENT.RUN_STATUS, pile.getRunStatus());
			intent.putExtra(KEY.INTENT.AVERAGE_LEVEL, pile.getAvgLevel());
			intent.putExtra(KEY.INTENT.OPERATOR, operator);
			setResult(RESULT_OK, intent);
		}
		finish();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}
}
