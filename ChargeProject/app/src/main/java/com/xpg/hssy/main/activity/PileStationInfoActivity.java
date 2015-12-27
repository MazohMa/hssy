package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.easy.util.EmptyUtil;
import com.easy.util.IntentUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.ecloud.pulltozoomview.PullToZoomBase;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.adapter.PileStationAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.Command;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileList;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialogComfirmNav;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.main.fragment.PileFindFragment;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.LogUtils;
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

import java.util.ArrayList;
import java.util.List;

public class PileStationInfoActivity extends BaseActivity {
	private static final int REQUEST_REFRESH = 0x99;
	// topBar控件
	private ImageView iv_left, iv_edit_grade, iv_favor, iv_share;// 左边返回按钮,右边编辑按钮,心形按钮,右边分享按钮
	private TextView tv_center;// 中间title
	private RelativeLayout rl_loading;
	private AnimationDrawable loadingAdAnimation;
	private AnimationDrawable backgroundLoadingAdAnimation;
	private ImageView iv_loading;
	private ImageView iv_background_loading;
	private RelativeLayout rl_loading_fail;
	private Button btn_try_to_refresh;// 重试一下
	// 带下拉回弹效果的scrollView
	private PullToZoomScrollViewEx pzs_center_container;
	// 电桩信息栏控件
	private TextView tv_location;// 位置
	private TextView tv_pile_type;//电桩类型
	private TextView tv_isIdle;//是否空闲
	private TextView tv_distance;// 距离
	private LinearLayout ll_price;// 费率layout
	private TextView tv_price;// 费用
	private TextView tv_price_sign;
	private TextView tv_price_unit;
	private TextView tv_dc; //直流
	private TextView tv_ac; //交流
	private TextView tv_idle;//空闲
	private ImageView iv_more_pile; //下拉显示更多空闲桩
	private LinearLayout ll_more_pile;
	private RelativeLayout ll_pile_type_layout;
	private FixHeightListView lv_pile;

	//电桩评价
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

	// 电桩详细说明
	private RelativeLayout rl_pile_detail;// 电桩信息
	private TextView tv_deail;//描述头
	private TextView tv_detail_data;// 描述

	// 电桩信息
	private TextView tv_station_type;// 站点类型
	private TextView tv_pay_way;// 计费方式
	private TextView tv_function_area;//功能区
	private LinearLayout ll_wifi, ll_convenience_store, ll_parking_space, ll_camera, ll_market, ll_restaurant, ll_hotel;//便利设施
	private TextView tv_nothing;


	// 运营商
	private LinearLayout ll_pile_owner;// 运营商layout
	private TextView tv_owner_operator;// 所有者名字
	private ImageView iv_dial;// 电话

	// 电桩图片
	private PilePhotoPager ppp_photo;
	private LinearLayout ll_indicator;
	private ImageView iv_ppp_favor;

	// 底部预约按钮栏
	private RelativeLayout rl_bottom;
	private Button btn_comment;//立即充电(预约)
	private Button btn_navigation;// 导航按钮,跟预约按钮大小一样
	private PileStation pileStation;
	private String pileId;
	private SPFile sp;
	private boolean isCollect = true;
	private int isCollected = 0;
	private boolean isCollectedPile = false;

	private String userId;

	private BDLocation mLocation;

	private int mScreenWidth;

	private DisplayImageOptions option;
	private final double DEFAULT_DURATION = .5;

	private boolean isShowMorePile;
	private PileStationAdapter pileStationAdapter;

	private LoadingDialog loadingDialog;
	private int position;//记录找桩列表桩在Adapter的位置
	private int operator;

	private int loadState;
	private static final int STATE_LOAD_COMPLETED = 1;

	private boolean isBackgroundRefreshing = false;

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
		position = getIntent().getIntExtra(KEY.INTENT.POSITION, -1);
		operator = getIntent().getIntExtra(KEY.INTENT.OPERATOR, 0);
		isCollected = getIntent().getIntExtra("collectedTag", 2);
		isCollectedPile = getIntent().getBooleanExtra(KEY.INTENT.IS_COLLECT_PILE, false);
		sp = new SPFile(self, "config");
		userId = sp.getString("user_id", null);
		pileStationAdapter = new PileStationAdapter(self, new ArrayList<Pile>());

		if (isLogin()) {
			// getCollectedTag();
		}
	}

	private void loadPileStationDetail() {
		WebAPIManager.getInstance().getPileStationById(pileId, userId, new WebResponseHandler<PileStation>() {
			@Override
			public void onFinish() {
				super.onFinish();
				if (loadState > STATE_LOAD_COMPLETED) {
					if (!isBackgroundRefreshing) {
						loadingAdAnimation.stop();
						rl_loading_fail.setVisibility(View.GONE);
						rl_bottom.setVisibility(View.VISIBLE);
						iv_share.setVisibility(View.VISIBLE);
						rl_loading.setVisibility(View.GONE);
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
					loadingAdAnimation.stop();
					rl_loading.setVisibility(View.GONE);
					rl_loading_fail.setVisibility(View.VISIBLE);
				} else {
					backgroundLoadingAdAnimation.stop();
					iv_background_loading.setVisibility(View.GONE);
					isBackgroundRefreshing = false;
				}
			}


			@Override
			public void onFailure(WebResponse<PileStation> response) {
				super.onFailure(response);
				if (!isBackgroundRefreshing) {
					loadingAdAnimation.stop();
					rl_loading.setVisibility(View.GONE);
					rl_loading_fail.setVisibility(View.VISIBLE);
				} else {
					backgroundLoadingAdAnimation.stop();
					iv_background_loading.setVisibility(View.GONE);
					isBackgroundRefreshing = false;
				}
			}

			@Override
			public void onSuccess(WebResponse<PileStation> response) {
				super.onSuccess(response);
				loadState++;
				if (response.getResultObj() == null) {
					return;
				}
//				tv_isIdle.setText(pileStation.getIsIdleAsStringID());
//				iv_favor.setVisibility(View.VISIBLE);
				pzs_center_container.setVisibility(View.VISIBLE);
				LayoutInflater inflater = LayoutInflater.from(self);
				pileStation = response.getResultObj();
				tv_center.setText(pileStation.getStationName());
				tv_location.setText(pileStation.getLocation());
				tv_ac.setText(pileStation.getAlterNum() + "");
				tv_dc.setText(pileStation.getDirectNum() + "");
				tv_idle.setText(pileStation.getFreeNum() + "");
				if (pileStation.getChargeFee() != null) {
					tv_price.setText(CalculateUtil.formatDefaultNumber(pileStation.getChargeFee()));
					tv_price_sign.setVisibility(View.VISIBLE);
					tv_price_unit.setVisibility(View.VISIBLE);
				} else {
					tv_price.setText("");
					tv_price_sign.setVisibility(View.INVISIBLE);
					tv_price_unit.setVisibility(View.INVISIBLE);
				}
				if (pileStation.getFavor() == PileStation.FAVOR_YES) {
					iv_ppp_favor.setImageResource(R.drawable.collect_heart_active);
				} else {
					iv_ppp_favor.setImageResource(R.drawable.collect_heart);
				}

				tv_distance.setVisibility(View.VISIBLE);
				LatLng eLatLng = new LatLng(pileStation.getLatitude(), pileStation.getLongitude());
				CalculateUtil.infuseDistance(self, tv_distance, eLatLng);
				// 评分
				if (pileStation.getAvgLevel() != null) {
					String eva = String.format("%.1f", pileStation.getAvgLevel());
					tv_grade_data.setText(eva);
					eva_star_point.setEvaluate(pileStation.getAvgLevel());

				} else {
					rl_pile_grade.setEnabled(false);
				}
				btn_navigation.setVisibility(View.VISIBLE);
				if (EmptyUtil.isEmpty(pileStation.getRemark())) {
					tv_detail_data.setText(R.string.studio_detail_default);
				} else {
					tv_detail_data.setText(pileStation.getRemark());
				}
				String pileType = MyApplication.getInstance().getPileStationTypeByKey(pileStation.getType());
				tv_pile_type.setText(pileType);
				tv_station_type.setText(pileType);
				if (pileStation.getIsIdle() == PileStation.STATE_STATION_HAVE_FREE) {
					tv_isIdle.setText(R.string.has_idle);
					tv_isIdle.setTextColor(getResources().getColor(R.color.sky_blue));
				} else if (pileStation.getIsIdle() == PileStation.STATE_STATION_FULL_LOAD) {
					tv_isIdle.setText(R.string.full_load);
					tv_isIdle.setTextColor(getResources().getColor(R.color.yellow));
				} else if (pileStation.getIsIdle() == PileStation.STATE_STATION_OFFLINE) {
					tv_isIdle.setText(R.string.offline);
					tv_isIdle.setTextColor(getResources().getColor(R.color.text_gray_light));
					ll_pile_type_layout.setEnabled(false);
					iv_more_pile.setVisibility(View.INVISIBLE);
				} else {
					tv_isIdle.setText(R.string.unknown);
					tv_isIdle.setTextColor(getResources().getColor(R.color.yellow));
				}
				tv_pay_way.setText(pileStation.getPaymentAsString(self));

				tv_function_area.setText(pileStation.getFuncTypeAsStringID());

				tv_owner_operator.setText(pileStation.getOperatorNameAsString());

				int[] amenities = pileStation.getFacilitiesAsInt();
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


//                btn_charge_or_comment.setEnabled(true);
				btn_navigation.setVisibility(View.VISIBLE);
				// TODO begin bug collection
				if (isCollectedPile) {
					iv_ppp_favor.setImageResource(R.drawable.collect_heart_active);
				} else {
					if (isLogin()) {
						getCollectedTag();
					}

				}

				LogUtils.e("PileStationInfoActivityya", "pileStation.getAcount" +
						"():" + pileStation.getAcount());
				if (pileStation.getAcount() > 0) {
					if (pileStation.getAcount() == 1) {
						rl_view_all_comment.setVisibility(View.GONE);
						rl_pile_grade.setEnabled(false);
					} else {
						rl_view_all_comment.setVisibility(View.VISIBLE);
						String str = self.getResources().getString(R.string.pile_comment_all, pileStation.getAcount());
						tv_grade_number.setText(Html.fromHtml(str));
						rl_pile_grade.setEnabled(true);
					}
				} else {
					rl_pile_grade.setEnabled(false);
				}

				//图片加载
				if (!isBackgroundRefreshing) {
					ppp_photo.clearItems();
					ll_indicator.removeAllViews();
					String coverImgUrl = pileStation.getCoverImg();
					if (!TextUtils.isEmpty(coverImgUrl)) {
						ppp_photo.loadPhoto(coverImgUrl);
						ll_indicator.addView(inflater.inflate(R.layout.iv_indicator, null));
					}
					List<String> imgUrls = pileStation.getImgurl();
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

			@Override
			public void onStart() {
				loadState = 0;
				super.onStart();
				if (!isBackgroundRefreshing) {
					loadingAdAnimation.start();
				} else {
					iv_background_loading.setVisibility(View.VISIBLE);
					backgroundLoadingAdAnimation.start();
				}
			}

		});
	}

	private void loadEvaluate() {
		if (isNetworkConnected()) {
			WebAPIManager.getInstance().getEvaluate(pileId, null, null, 0, 1, new WebResponseHandler<List<Command>>() {
				@Override
				public void onStart() {
					super.onStart();
				}

				@Override
				public void onFinish() {
					super.onFinish();
					if (loadState > STATE_LOAD_COMPLETED) {
						if (!isBackgroundRefreshing) {
							rl_loading_fail.setVisibility(View.GONE);
							rl_bottom.setVisibility(View.VISIBLE);
							iv_share.setVisibility(View.VISIBLE);
							rl_loading.setVisibility(View.GONE);
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
						loadingAdAnimation.stop();
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
						loadingAdAnimation.stop();
						rl_loading.setVisibility(View.GONE);
						rl_loading_fail.setVisibility(View.VISIBLE);
					} else {
						backgroundLoadingAdAnimation.stop();
						iv_background_loading.setVisibility(View.GONE);
						isBackgroundRefreshing = false;
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
			});
		}
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

	private void refreshPileState() {
		WebAPIManager.getInstance().getPileStateById(pileId, new WebResponseHandler<PileList>() {
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

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				ToastUtil.show(self, R.string.get_pile_list_failec);
				hidePileList();
			}

			@Override
			public void onFailure(WebResponse<PileList> response) {
				super.onFailure(response);
				ToastUtil.show(self, R.string.get_pile_list_failec);
				hidePileList();
			}

			@Override
			public void onSuccess(WebResponse<PileList> response) {
				super.onSuccess(response);
				PileList pileList = response.getResultObj();
				if (pileList != null) {
					List<Pile> piles = pileList.getPileList();
					if (piles != null && piles.size() > 0) {
						pileStationAdapter.clear();
						pileStationAdapter.add(piles);
					} else {
						ToastUtil.show(self, R.string.not_pile);
						hidePileList();
					}
					tv_ac.setText(pileList.getAlterNum() + "");
					tv_dc.setText(pileList.getDirectNum() + "");
					tv_idle.setText(pileList.getFreeNum() + "");
				} else {
					ToastUtil.show(self, R.string.not_pile);
					hidePileList();
				}
			}


		});
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_pile_info_pull_to_zoom);
		pzs_center_container = (PullToZoomScrollViewEx) findViewById(R.id.pzs_center_container);
		pzs_center_container.setZoomView(LayoutInflater.from(this).inflate(R.layout.layout_photo_pager, null));
		pzs_center_container.setScrollContentView(LayoutInflater.from(this).inflate(R.layout.layout_pile_station_info, null));
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		LayoutInflater.from(this).inflate(R.layout.layout_pile_station_info_bottom, rl_bottom);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
		rl_loading_fail = (RelativeLayout) findViewById(R.id.rl_loading_fail);
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
//		mScreenHeight = localDisplayMetrics.heightPixels;
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
		tv_isIdle = (TextView) pzs_center_container.findViewById(R.id.tv_isIdle);
		tv_location = (TextView) pzs_center_container.findViewById(R.id.tv_location);// 位置
		tv_distance = (TextView) pzs_center_container.findViewById(R.id.tv_distance);// 距离
		ll_price = (LinearLayout) pzs_center_container.findViewById(R.id.ll_price);// 费率layout
		tv_price = (TextView) pzs_center_container.findViewById(R.id.tv_price);// 费用
		tv_price_sign = (TextView) pzs_center_container.findViewById(R.id.tv_price_sign);// 费用
		tv_price_unit = (TextView) pzs_center_container.findViewById(R.id.tv_price_unit);// 费用
		tv_dc = (TextView) pzs_center_container.findViewById(R.id.tv_dc);//直流
		tv_ac = (TextView) pzs_center_container.findViewById(R.id.tv_ac); //交流
		tv_idle = (TextView) pzs_center_container.findViewById(R.id.tv_idle);//空闲
		iv_more_pile = (ImageView) pzs_center_container.findViewById(R.id.iv_more_pile); //下拉显示更多空闲桩
		ll_more_pile = (LinearLayout) pzs_center_container.findViewById(R.id.ll_more_pile);
		ll_pile_type_layout = (RelativeLayout) pzs_center_container.findViewById(R.id.ll_pile_type_layout);
		lv_pile = (FixHeightListView) pzs_center_container.findViewById(R.id.lv_pile);
		lv_pile.setAdapter(pileStationAdapter);

		//电桩评价
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

		// 电桩详细说明
		rl_pile_detail = (RelativeLayout) pzs_center_container.findViewById(R.id.rl_pile_detail);
		tv_deail = (TextView) pzs_center_container.findViewById(R.id.tv_detail);
		tv_detail_data = (TextView) pzs_center_container.findViewById(R.id.tv_detail_data);

		// 电桩信息
		tv_station_type = (TextView) pzs_center_container.findViewById(R.id.tv_station_type);// 站点类型
		tv_pay_way = (TextView) pzs_center_container.findViewById(R.id.tv_pay_way);
		tv_function_area = (TextView) pzs_center_container.findViewById(R.id.tv_function_area);//功能区
		ll_wifi = (LinearLayout) pzs_center_container.findViewById(R.id.ll_wifi);
		ll_convenience_store = (LinearLayout) pzs_center_container.findViewById(R.id.ll_convenience_store);
		ll_parking_space = (LinearLayout) pzs_center_container.findViewById(R.id.ll_parking_space);
		ll_camera = (LinearLayout) pzs_center_container.findViewById(R.id.ll_camera);//便利设施
		ll_market = (LinearLayout) pzs_center_container.findViewById(R.id.ll_market);
		ll_restaurant = (LinearLayout) pzs_center_container.findViewById(R.id.ll_restaurant);
		ll_hotel = (LinearLayout) pzs_center_container.findViewById(R.id.ll_hotel);
		tv_nothing = (TextView) pzs_center_container.findViewById(R.id.tv_nothing);

		// 电桩图片
		ll_indicator = (LinearLayout) pzs_center_container.findViewById(R.id.ll_indicator);
		ppp_photo = (PilePhotoPager) pzs_center_container.findViewById(R.id.ppp_photo);
		iv_ppp_favor = (ImageView) pzs_center_container.findViewById(R.id.iv_ppp_favor);

		iv_background_loading = (ImageView) pzs_center_container.findViewById(R.id.iv_background_loading);
		backgroundLoadingAdAnimation = (AnimationDrawable) iv_background_loading.getBackground();

		btn_comment = (Button) findViewById(R.id.btn_comment);// 立即充电(预约)按钮
		btn_navigation = (Button) findViewById(R.id.btn_navigation);// 导航按钮
		btn_try_to_refresh = (Button) findViewById(R.id.btn_try_to_refresh);//重试按钮
		iv_loading = (ImageView) findViewById(R.id.iv_loading);
		loadingAdAnimation = (AnimationDrawable) iv_loading.getBackground();


		tv_center.requestFocus();
		tv_center.setText("");


		// 运营商
		ll_pile_owner = (LinearLayout) pzs_center_container.findViewById(R.id.ll_pile_owner);// 运营商layout
		tv_owner_operator = (TextView) pzs_center_container.findViewById(R.id.tv_owner_operator);// 运营商名字
		iv_dial = (ImageView) pzs_center_container.findViewById(R.id.iv_dial);// 电话icon

		iv_edit_grade.setVisibility(View.GONE);
		iv_share.setVisibility(View.GONE);
		iv_favor.setVisibility(View.GONE);

		pzs_center_container.setZoomEnabled(true);
		pzs_center_container.setHideHeader(false);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		iv_left.setOnClickListener(this);
//		iv_edit_grade.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		iv_ppp_favor.setOnClickListener(this);
		rl_pile_grade.setOnClickListener(this);
		btn_comment.setOnClickListener(this);
		btn_navigation.setOnClickListener(this);
		btn_try_to_refresh.setOnClickListener(this);
//		iv_more_pile.setOnClickListener(this);
		iv_dial.setOnClickListener(this);
		ll_pile_type_layout.setOnClickListener(this);
		pzs_center_container.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
			@Override
			public void onPullZooming(int newScrollValue) {

			}

			@Override
			public void onPullZoomEnd() {
				if (!isBackgroundRefreshing) {
					isBackgroundRefreshing = true;
					loadPileStationDetail();
					loadEvaluate();
					if (isShowMorePile) refreshPileState();
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pzs_center_container.setVisibility(View.INVISIBLE);
		rl_loading.setVisibility(View.VISIBLE);
		rl_view_all_comment.setVisibility(View.GONE);
		rl_bottom.setVisibility(View.GONE);
		loadPileStationDetail();
		loadEvaluate();
		option = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
				getResources().getDimension(R.dimen.h30))).build();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/** Joke 如果用户没有登录，在登录后返回这个界面时重新获取userId **/
		userId = sp.getString("user_id", null);
	}

	private void showMorePileList() {
		refreshPileState();
		iv_more_pile.setImageDrawable(getResources().getDrawable(R.drawable.find_xiangshang));
		ll_more_pile.setVisibility(View.VISIBLE);
		isShowMorePile = true;
	}

	private void hidePileList() {
		iv_more_pile.setImageDrawable(getResources().getDrawable(R.drawable.find_xiala));
		ll_more_pile.setVisibility(View.GONE);
		isShowMorePile = false;
		LogUtils.e("PileStationInfoActivity", "hidePileList");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
			case R.id.iv_left: {
				back();
//				finish();
//				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				break;
			}
			case R.id.iv_edit_grade: {
				if (isLogin()) {
					Intent goGrade = new Intent(this, MultipleEvaluateActivity.class);
					goGrade.putExtra(KEY.INTENT.PILE_ID, pileStation.getId());
					goGrade.putExtra(KEY.INTENT.PILE_NAME, pileStation.getStationName());
					goGrade.putExtra(KEY.INTENT.EVALUATE_TYPE, MultipleEvaluateActivity.EVALUATE_TYPE_STATION);
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
						loadingDialog.showDialog();
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
						if (!TextUtils.isEmpty(url) && self != null) {
							url = url.replaceAll("\"", "");
							ShareApiManager.oneKeyShareDownloadImage(self, url);
						}
					}
				});
				break;
			}
			case R.id.btn_comment: {
				if (isLogin()) {
					Intent goGrade = new Intent(this, MultipleEvaluateActivity.class);
					goGrade.putExtra(KEY.INTENT.PILE_ID, pileStation.getId());
					goGrade.putExtra(KEY.INTENT.PILE_NAME, pileStation.getStationName());
					goGrade.putExtra(KEY.INTENT.EVALUATE_TYPE, MultipleEvaluateActivity.EVALUATE_TYPE_STATION);
					startActivityForResult(goGrade, REQUEST_REFRESH);
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
			case R.id.ll_pile_type_layout:
				if (isShowMorePile) {
					hidePileList();
				} else {
					showMorePileList();
				}
				break;
			case R.id.iv_dial: {
				String phoneNum = pileStation.getOperatorPhone();
				if (EmptyUtil.isEmpty(phoneNum)) {
					ToastUtil.show(self, "暂无联系电话");
					return;
				}
				IntentUtil.openTelephone(this, phoneNum);
				break;
			}
			case R.id.btn_navigation: {
				callNavigation();
				break;
			}
			case R.id.btn_try_to_refresh: {
				loadPileStationDetail();
				loadEvaluate();
				break;
			}
			case R.id.rl_pile_grade: {
				if (isNetworkConnected()) {
					Intent intent = new Intent(this, SpecifiedEvaluateAndMomentsActivity.class);
					intent.putExtra(SpecifiedEvaluateAndMomentsActivity.KEY_SPECIIED_TYPE, SpecifiedEvaluateAndMomentsActivity.SPECIIED_TYPE_PILE_OR_STATION);
					intent.putExtra(KEY.INTENT.PILE_NAME, pileStation.getStationName());
					intent.putExtra(KEY.INTENT.PILE_ID, pileId);
					startActivityForResult(intent,REQUEST_REFRESH);
				} else {
					return;
				}
				break;
			}
			default:
				break;
		}
	}


	private void collect() {
		if (pileStation == null) {
			return;
		}

		String userid = sp.getString("user_id", null);
		if (!isLogin() || userid == null) {
			Intent intentLogin = new Intent(this, LoginActivity.class);
			startActivity(intentLogin);
			return;
		}

		WebAPIManager.getInstance().addFavoritesPile(userid, pileStation.getId(), new WebResponseHandler<Object>(self) {
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
		if (pileStation == null) {
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

		WebAPIManager.getInstance().removeFavoritesPile(userid, pileStation.getId(), new WebResponseHandler<Object>(self) {
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
//                initTimeStting(startTime, endTime, duration);
				break;
			}
			case REQUEST_REFRESH:
				Log.i("tag", "num=======0");
				loadPileStationDetail();
				loadEvaluate();
				break;
			default:
				break;
		}
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
					WaterBlueDialogComfirmNav waterBlueDialogComfirmNav = new WaterBlueDialogComfirmNav(PileStationInfoActivity.this);
					waterBlueDialogComfirmNav.show(mLocation.getLatitude(), mLocation.getLongitude(), pileStation.getLatitude(), pileStation.getLongitude());
				} else {
					ToastUtil.show(PileStationInfoActivity.this, "定位失败,请重试");
				}
			}

		});
	}


	@Override
	public void onBackPressed() {
		back();
	}

	private void back() {
		if (pileStation != null) {
			Intent intent = new Intent();
			intent.putExtra(KEY.INTENT.PILE_ID, pileStation.getId());
			intent.putExtra(KEY.INTENT.POSITION, position);
			intent.putExtra(KEY.INTENT.IS_FAVOR, isCollectedPile);
			intent.putExtra(KEY.INTENT.RUN_STATUS, pileStation.getIsIdle());
			intent.putExtra(KEY.INTENT.AVERAGE_LEVEL, pileStation.getAvgLevel());
			intent.putExtra(KEY.INTENT.OPERATOR, operator);
			setResult(RESULT_OK, intent);
		}
		finish();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}
}
