package com.xpg.hssy.popwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.easy.util.MeasureUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.bean.searchconditon.CenterPointSearchCondition;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.dialog.WaterBlueDialogComfirmNav;
import com.xpg.hssy.main.activity.PileInfoNewActivity;
import com.xpg.hssy.main.activity.PileStationInfoActivity;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.ExtensionUtils;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssychargingpole.MyApplication;
import com.xpg.hssychargingpole.R;

/**
 * 用法 PileInfoMapPop pp = new PileInfoMapPop(getActivity()) ;
 * pp.setAnimationStyle(R.style.mypopwindow_anim_style);
 * pp.showAtLocation(iv_usericon, Gravity.BOTTOM, 0, 0) ;
 *
 * @author Mazoh
 */
public class PileInfoMapPop extends BasePop implements OnClickListener {
	private Context context;
	private Pile pile;
	private BDLocation mLocation;

	private TextView tv_location, tv_name, tv_pile_type, tv_idle, tv_distance, tv_price, tv_grade_data;
	private Button btn_book_charge, btn_navigation;
	//	private ImageView iv_own_type, iv_current_type;
	//	private LinearLayout rl_pile_info;
	private EvaluateColumn ll_star_point;


	private RelativeLayout rl_pile_info;
	private ImageView iv_pile_image;
	private double distance = -1;

	private BaiduMap baiduMap;
	private CenterPointSearchCondition searchCondition;

	private final DisplayImageOptions opion;

	public BaiduMap getBaiduMap() {
		return baiduMap;
	}

	public void setBaiduMap(BaiduMap baiduMap) {
		this.baiduMap = baiduMap;
	}

	public Pile getPile() {
		return pile;
	}

	public void setPile(Pile pile) {
		this.pile = pile;
	}


	@SuppressLint("DefaultLocale")
	public PileInfoMapPop(Context context, CenterPointSearchCondition searchCondition, BDLocation mLocation, Pile pile) {
		super(context);// 必须调用父类的构造函数
		this.context = context;
		this.searchCondition = searchCondition;
		this.mLocation = mLocation;
		this.pile = pile;
		opion = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.find_sanyoubg2).showImageOnFail(R.drawable.find_sanyoubg2).showImageOnLoading(R.drawable.find_sanyoubg2).displayer(new RoundedBitmapDisplayer
				(context.getResources().getDimensionPixelSize(R.dimen.w8))).build();
	}

	public void init() {
		String imgUrl = pile.getCoverCropImg();

		if (imgUrl == null) {
			if (pile.getIntroImgs() != null && pile.getIntroImgs().size() > 0) {
				imgUrl = pile.getIntroImgs().get(0);
			}
		}
		if (imgUrl == null) {
			imgUrl = "";
		}
		ImageLoaderManager.getInstance().displayImage(imgUrl, iv_pile_image, opion);
		tv_name.setText(pile.getPileNameAsString());
		// 地址
		tv_location.setText(pile.getLocationAsString());

		// 评分
		if (pile.getAvgLevel() != null && pile.getAvgLevel() != 0) {
			tv_grade_data.setText(pile.getAvgLevelAsString());
			ll_star_point.setEvaluate(pile.getAvgLevel());
		} else {
			tv_grade_data.setText(context.getResources().getString(R.string.no_grade));
			ll_star_point.setEvaluate(0);
		}

		// 拥有者类别图标
		if (pile.getOperator() != null) {
			if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
				if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
					btn_book_charge.setText(R.string.book_charge);
					if (pile.getShareState() != null && pile.getShareState() == Pile.SHARE_STATUS_YES) {
						tv_price.setVisibility(View.VISIBLE);
						btn_book_charge.setEnabled(true);
						tv_idle.setText(R.string.had_shared);
						tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
					} else {
						tv_price.setVisibility(View.INVISIBLE);
						btn_book_charge.setEnabled(false);
						tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
						tv_idle.setText(R.string.not_shared);
					}
				} else {
					btn_book_charge.setText(R.string.immediately_charge);
					btn_book_charge.setEnabled(true);
					if (pile.getRunStatus() == Pile.STATE_PERSION_FREE) {
						tv_idle.setText(R.string.idle);
						tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
					} else if (pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() == Pile.STATE_PERSION_CHARGE_FINISHED) {
						tv_idle.setText(R.string.charging);
						tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
					} else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT) {
						tv_idle.setText(R.string.occupy);
						tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
					} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_BOOK) {
						tv_idle.setText(R.string.booked);
						tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
					} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT) {
						tv_idle.setText(R.string.maintenance);
						tv_idle.setTextColor(context.getResources().getColor(R.color.gray));
					} else if (pile.getRunStatus() == Pile.STATE_PERSION_GPRS_DISCONNECT) {
						tv_idle.setText(R.string.offline);
						tv_idle.setTextColor(context.getResources().getColor(R.color.gray));
					} else {
						tv_idle.setText(R.string.unknown);
						tv_idle.setTextColor(context.getResources().getColor(R.color.gray));
					}

//
				}
				//电桩类型
				String pileType = MyApplication.getInstance().getPileTypeByKey(pile.getType());
				tv_pile_type.setText(pileType);
				btn_book_charge.setTextSize(ExtensionUtils.px2dip(context, context.getResources().getDimension(R.dimen.h17)));

			} else {
				//电站类型
				String pileType = MyApplication.getInstance().getPileStationTypeByKey(pile.getType());
				tv_pile_type.setText(pileType);
				if (pile.getIsIdle() == PileStation.STATE_STATION_HAVE_FREE) {
					tv_idle.setText(R.string.has_idle);
					tv_idle.setTextColor(context.getResources().getColor(R.color.sky_blue));
				} else if (pile.getIsIdle() == PileStation.STATE_STATION_FULL_LOAD) {
					tv_idle.setText(R.string.full_load);
					tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
				} else if (pile.getIsIdle() == PileStation.STATE_STATION_OFFLINE) {
					tv_idle.setText(R.string.offline);
					tv_idle.setTextColor(context.getResources().getColor(R.color.text_gray_light));
				} else {
					tv_idle.setText(R.string.unknown);
					tv_idle.setTextColor(context.getResources().getColor(R.color.yellow));
				}
//				tv_price.setVisibility(View.INVISIBLE);
				if (pile.getPrice() != null) {
					tv_price.setText(context.getResources().getString(R.string.price_unit_1, pile.getPrice() + ""));
				}
				int gprsNum = 0;
				int freeNum = 0;
				if (pile.getGprsNum() != null) {
					gprsNum = pile.getGprsNum();
				}
				if (pile.getFreeNum() != null) {
					freeNum = pile.getFreeNum();
				}
				int dcNum = 0;
				int acNum = 0;
				if (pile.getDirectNum() != null) {
					dcNum = pile.getDirectNum();
				}
				if (pile.getAlterNum() != null) {
					acNum = pile.getAlterNum();
				}
				btn_book_charge.setTextSize(ExtensionUtils.px2dip(context, context.getResources().getDimension(R.dimen.h13)));
				btn_book_charge.setText(context.getResources().getString(R.string.default_studio_annotation, dcNum, acNum));
//				if (gprsNum > 0) {
//					btn_book_charge.setEnabled(true);
//					btn_book_charge.setText(context.getResources().getString(R.string.studio_pile_num, gprsNum, freeNum));
//				} else {
//					btn_book_charge.setText(R.string.studio_offline);
//					btn_book_charge.setEnabled(false);
//				}
			}
		} else {
			tv_pile_type.setText("");
		}
		// 充电价格
		if (pile.getPrice() != null) {
			CalculateUtil.infusePrice(tv_price, pile.getPrice());
		} else {
			tv_price.setVisibility(View.INVISIBLE);
		}
		// 计算距离
		if (pile.getLatitude() != null && pile.getLongitude() != null) {
			tv_distance.setVisibility(View.VISIBLE);
//			LatLng sLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
			LatLng eLatLng = new LatLng(pile.getLatitude(), pile.getLongitude());
//			distance = DistanceUtil.getDistance(sLatLng, eLatLng);
			CalculateUtil.infuseDistance(context, tv_distance, eLatLng);
			// 设定距离文本,利用html文本用橙色颜色标记距离数
//			String distanceInfo = context.getString(R.string.to_tag_distance, String.format("%.1f", distance));
//			tv_distance.setText(Html.fromHtml(String.format("%.1f", distance) + "km"));
		} else {
			tv_distance.setText(R.string.distance_unknow);
		}
	}

	@Override
	protected void initData() {
		super.initData();

	}

	public BDLocation getmLocation() {
		return mLocation;
	}

	public void setmLocation(BDLocation mLocation) {
		this.mLocation = mLocation;
	}

	public CenterPointSearchCondition getSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(CenterPointSearchCondition searchCondition) {
		this.searchCondition = searchCondition;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void initUI() {
		super.initUI();
		View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_pile_info_map_pop, null);
		// TODO
		// ps，关于pop，我们也可以在构造函数中传入view，而不必setContentView，因为构造函数中的view，其实最终也要setContentView
		setContentView(v);
		setWidth((MeasureUtil.screenWidth));
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(false);
		rl_pile_info = (RelativeLayout) v.findViewById(R.id.rl_pile_info);
		ll_star_point = (EvaluateColumn) v.findViewById(R.id.eva_star_point);
		tv_location = (TextView) v.findViewById(R.id.tv_location);
		tv_name = (TextView) v.findViewById(R.id.tv_name);
		tv_pile_type = (TextView) v.findViewById(R.id.tv_pile_type);
		tv_idle = (TextView) v.findViewById(R.id.tv_idle);
		tv_distance = (TextView) v.findViewById(R.id.tv_distance);
		iv_pile_image = (ImageView) v.findViewById(R.id.iv_pile_image);
		tv_price = (TextView) v.findViewById(R.id.tv_price);
		tv_grade_data = (TextView) v.findViewById(R.id.tv_grade_data);
		btn_book_charge = (Button) v.findViewById(R.id.btn_book_charge);
		btn_navigation = (Button) v.findViewById(R.id.btn_navigation);

		tv_name.requestFocus();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		rl_pile_info.setOnClickListener(this);
		btn_book_charge.setOnClickListener(this);
		btn_navigation.setOnClickListener(this);
	}

	private boolean isLogin() {
		return new SPFile(context, "config").getBoolean("isLogin", false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//这个两个点击触发同样的事件
			case R.id.btn_book_charge:
				if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
					if (pile.getGprsType() == Pile.TYPE_GPRS || pile.getGprsType() == Pile.TYPE_GPRS_BLUETOOTH) {
						if (isLogin()) {
							//GPRS桩跳至扫码充电
							context.sendBroadcast(new Intent(KEY.ACTION.ACTION_IMMEDIATELY_CHARGE));
						} else {
							Intent intentLogin = new Intent(context, LoginActivity.class);
							context.startActivity(intentLogin);
						}
						break;
					}
				}
			case R.id.rl_pile_info: {
				Intent intent = null;
				if (getPile() != null) {
					if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
						intent = new Intent(context, PileInfoNewActivity.class);
						intent.putExtra(KEY.INTENT.GPRS_TYPE, pile.getGprsType());
					} else {
						intent = new Intent(context, PileStationInfoActivity.class);
					}
					intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
//		intent.putExtra(KEY.INTENT.DISTANCE, distance);
					intent.putExtra(KEY.INTENT.START_TIME, searchCondition.getStartTime());
					intent.putExtra(KEY.INTENT.END_TIME, searchCondition.getEndTime());
					intent.putExtra(KEY.INTENT.DURATION, searchCondition.getDuration());
					context.startActivity(intent);
					((Activity) context).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				}
				break;
			}

			case R.id.btn_navigation: {
				callNavigation();
				break;
			}
			default:
				break;
		}
	}

	private void callNavigation() {

		WaterBlueDialogComfirmNav waterBlueDialogComfirmNav = new WaterBlueDialogComfirmNav(context);
		if (mLocation != null) {
			waterBlueDialogComfirmNav.show(mLocation.getLatitude(), mLocation.getLongitude(), pile.getLatitude(), pile.getLongitude());
		} else {
			ToastUtil.show(context, "尚未定位您的位置,请稍候");
		}
	}
}
