package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.DialogUtil;
import com.xpg.hssy.dialog.WaterBlueDialog;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.TimeSetting2View;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * PileDisplayActivity
 *
 * @author Mazoh 电桩发布
 *
 */
public class PileDisplayActivity extends BaseActivity implements
		OnClickListener {
	private static final int CHARGEFREE = 10;
	public final static int ADDTIMEREQUESTCODE = 3;
	private Activity mActivity;
	private RelativeLayout lianxiren_tv_layout_id;// 联系人
	private RelativeLayout settingtime_tv_layout_id; // 设置时间段
	private RelativeLayout time_setting_layout; // 时间设置界面
	private RelativeLayout display_statue2;// 发布状态
	private RelativeLayout display_statue;// 发布状态
	private SharedPreferences sp;
	private Pile pile;
	private String pile_id;
	private Button bt_canceldisplay;
	private Button bt_mashangdisplay;
	private Button bt_mashangdisplays;
	private List<ShareTime> shareTimes;
	private RelativeLayout bt_addtime_layout;
	private List<Integer> weeks;
	private LinearLayout lv_content_layout;
	private RelativeLayout chongdianfuwufei_layout_id;
	private TextView chongdianfuwufei_tvs;
	private TextView username_tv;// 用户名
	private TextView dianzhuangtype_tvs;// 电桩类型
	private TextView edinggonglv_tvs;// 额定功率
	private TextView edingdianya_tvs;// 电压
	private TextView electric_current;// 电流
	private ImageButton btn_left;// 返回
	private ImageButton dianzhuangtype_img; // 电桩类型（pic）
	private String price;
	private String user_id;
	private String ratedPower;
	private String ratedVoltage;
	private String current;
	private String type;
	private User user;
	private String username;
	private boolean timeConflict;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		pile_id = getIntent().getStringExtra("pile_id");
		user_id = sp.getString("user_id", "");
		if (EmptyUtil.notEmpty(user_id)) {
			user = DbHelper.getInstance(this).getUserDao().load(user_id);
		}
		pile = DbHelper.getInstance(this).getPileDao().load(this.pile_id); // 数据库获取个人桩
//		if (pile.getGprsType() != Pile.TYPE_BLUETOOTH) {
//			initGprsShareTime();
//		} else {
			shareTimes = pile.getPileShares();
//		}
		mActivity = this;
		timeConflict = true;
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.pile_display_layout, null);
		setContentView(view);
		hideSoftInput(view);
		setTitle("电桩发布");
		btn_left = (ImageButton) view.findViewById(R.id.btn_left);
		lianxiren_tv_layout_id = (RelativeLayout) view
				.findViewById(R.id.lianxiren_tv_layout_id); // 联系人
		lv_content_layout = (LinearLayout) view
				.findViewById(R.id.lv_content_layout);// 时间
		chongdianfuwufei_layout_id = (RelativeLayout) view// 充电服务费
				.findViewById(R.id.chongdianfuwufei_layout_id);
		display_statue2 = (RelativeLayout) view// 发布状态
				.findViewById(R.id.display_statue2);
		display_statue = (RelativeLayout) view// 发布状态
				.findViewById(R.id.display_statue);
		settingtime_tv_layout_id = (RelativeLayout) view
				.findViewById(R.id.settingtime_tv_layout_id);// 设置时间段
		username_tv = (TextView) view.findViewById(R.id.username_tv);// 用户名
		chongdianfuwufei_tvs = (TextView) view
				.findViewById(R.id.chongdianfuwufei_tvs);// textView 充电服务费
		dianzhuangtype_tvs = (TextView) view
				.findViewById(R.id.dianzhuangtype_tvs);// textView 电桩类型
		dianzhuangtype_img = (ImageButton) view
				.findViewById(R.id.dianzhuangtype_img);
		edinggonglv_tvs = (TextView) view.findViewById(R.id.edinggonglv_tvs);// 额定功率
		edingdianya_tvs = (TextView) view.findViewById(R.id.edingdianya_tvs);// 额定电压
		electric_current = (TextView) view.findViewById(R.id.electric_current);// 电流
		bt_canceldisplay = (Button) view.findViewById(R.id.bt_canceldisplay);
		bt_mashangdisplay = (Button) view.findViewById(R.id.bt_updatedisplay);
		bt_mashangdisplays = (Button) view.findViewById(R.id.bt_mashangdisplays);
		lv_content_layout.removeAllViews();
		if (isNetworkConnected()) {
			WebAPIManager.getInstance().getPileById(pile_id,
					new WebResponseHandler<Pile>(this) {

						@Override
						public void onStart() {
							super.onStart();
							DialogUtil.showLoadingDialog(
									PileDisplayActivity.this, R.string.loading);

						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							DialogUtil.dismissDialog();
						}

						@Override
						public void onFailure(WebResponse<Pile> response) {
							super.onFailure(response);
							DialogUtil.dismissDialog();

						}

						@Override
						public void onSuccess(WebResponse<Pile> response) {
							super.onSuccess(response);
							DialogUtil.dismissDialog();

							Pile pileOwn = response.getResultObj();
							if (pileOwn != null) {
								DbHelper.getInstance(PileDisplayActivity.this)
										.insertPile(pileOwn);
								updateUI();
							}
						}

					});
		} else {
			updateUI();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void updateUI() {
		pile = DbHelper.getInstance(this).getPileDao().load(this.pile_id); // 数据库获取个人桩

		price = pile.getPileShares().size() != 0 ? (pile.getPileShares().get(0)
				.getServicePay() + "") : "0";
		if (price == null || price.equals("null") || price.equals("")) {
			price = "0";
		}

//		if(pile.getGprsType()!=Pile.TYPE_BLUETOOTH)
//		{
//			settingtime_tv_layout_id.setVisibility(View.GONE);
//		}

		ratedPower = pile.getRatedPower() == null ? "未知" : pile.getRatedPower()
				+ "";
		ratedVoltage = pile.getRatedVoltage() == null ? "未知" : pile
				.getRatedVoltage() + "";
		current = pile.getRatedCurrent() == null ? "未知 " : pile
				.getRatedCurrent() + "";
		type = pile.getType() + "";
//		if (pile.getGprsType() != Pile.TYPE_BLUETOOTH) {
//			initGprsShareTime();
//		} else {
			shareTimes = pile.getPileShares();
//		}
		Log.i("tag======", shareTimes.size() + "");
		refreshShareTimeView();

		username_tv.setText(pile.getContactNameAsString());
		// if (user_id != null || !"".equals(user_id)) {
		// user = DbHelper.getInstance(mActivity).getUserDao().load(user_id);
		// username = user.getName();
		// username_tv.setText(username);
		// }

		chongdianfuwufei_tvs.setText(price + "");
		dianzhuangtype_img
				.setBackgroundResource("1".equals(type) ? R.drawable.icon_direct : R.drawable.icon_communication);
		edinggonglv_tvs.setText(ratedPower);
		edingdianya_tvs.setText(ratedVoltage);
		electric_current.setText(current);

		if (pile.getShareState() != null
				&& pile.getShareState() == Pile.SHARE_STATUS_YES) {
			display_statue2.setVisibility(View.GONE);
			display_statue.setVisibility(View.VISIBLE);
		} else {
			display_statue.setVisibility(View.GONE);
			display_statue2.setVisibility(View.VISIBLE);
		}
	}

//	private void initGprsShareTime() {
//		shareTimes = new ArrayList<>(1);
//		ShareTime shareTime = new ShareTime();
//		shareTime.setStartTime("00:00");
//		shareTime.setEndTime("24:00");
//		ArrayList<Integer> weeks = new ArrayList<>();
//		int endWeek = 7;
//		for (int i = 0; i < endWeek; i++) {
//			weeks.add(i + 1);
//		}
//		shareTime.setWeeks(weeks);
//		shareTime.setServicePay(pile.getPrice());
//		shareTimes.add(shareTime);
//	}

	@Override
	protected void initEvent() {
		super.initEvent();
		settingtime_tv_layout_id.setOnClickListener(this);
		chongdianfuwufei_layout_id.setOnClickListener(this);
		bt_canceldisplay.setOnClickListener(this);
		bt_mashangdisplay.setOnClickListener(this);
		bt_mashangdisplays.setOnClickListener(this);
		btn_left.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.chongdianfuwufei_layout_id:// 充电服务费
			Intent _intentEditUser = new Intent(mActivity,
					EditChargeFreeActivity.class);
			_intentEditUser.putExtra("price", price);
			startActivityForResult(_intentEditUser, CHARGEFREE);
			mActivity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;

		case R.id.settingtime_tv_layout_id:// 设置发布桩时间段
			Intent _intent = new Intent(mActivity, SettingTimeActivity.class);
			_intent.putExtra("shareTimes", (Serializable) shareTimes);
			mActivity.startActivityForResult(_intent, ADDTIMEREQUESTCODE);
			mActivity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;

		case R.id.bt_canceldisplay:
			unshare();
			break;
		case R.id.bt_updatedisplay:
			if (shareTimes.size() == 0) {
				ToastUtil.show(mActivity, "请设置时间段");
			} /*
			 * else if (price == null || price.equals("")) {
			 * ToastUtil.show(mActivity, "请设置充电服务费"); }
			 */else {
				display_now();
			}
			break;
		case R.id.bt_mashangdisplays:
			if (shareTimes.size() == 0) {
				ToastUtil.show(mActivity, "请设置时间段");
			} /*
			 * else if (price == null || price.equals("") || price.equals("0"))
			 * { ToastUtil.show(mActivity, "请设置充电服务费"); }
			 */else {
				share1();
			}
			break;
		case R.id.btn_left:
			if (timeConflict) {
				WaterBlueDialog timeConflictDialog = new WaterBlueDialog(
						mActivity);
				timeConflictDialog
						.setContent(R.string.pile_display_activity_timeConflict);
				timeConflictDialog.show();
				timeConflictDialog.setCanceledOnTouchOutside(true);
			} else {
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (timeConflict) {
			WaterBlueDialog timeConflictDialog = new WaterBlueDialog(mActivity);
			timeConflictDialog
					.setContent(R.string.pile_display_activity_timeConflict);
			timeConflictDialog.show();
			timeConflictDialog.setCanceledOnTouchOutside(true);
		} else {
			finish();
		}
	}

	private void share(List<ShareTime> shareTimes) {
		share3(shareTimes);
	}

	private void share3(List<ShareTime> shareTimes) {
		WebAPIManager.getInstance().share(pile.getPileId(), shareTimes,
				user == null ? null : user.getName(),
				user == null ? null : user.getPhone(),
				new WebResponseHandler<Pile>(mActivity) {
					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(mActivity, e);
						DialogUtil.dismissDialog();

					}

					@Override
					public void onFailure(WebResponse<Pile> response) {
						super.onFailure(response);
						TipsUtil.showTips(mActivity, response);
						DialogUtil.dismissDialog();
					}

					@Override
					public void onSuccess(WebResponse<Pile> response) {
						super.onSuccess(response);
						/*
						 * 设置时间,更新本地数据库,需要返回Pile对象
						 */
						DialogUtil.dismissDialog();
						pile = response.getResultObj();
						DbHelper.getInstance(mActivity).insertPile(pile); // 插入数据库
						updateUI();
						display_statue2.setVisibility(View.GONE);
						display_statue.setVisibility(View.VISIBLE);
						ToastUtil.show(mActivity, response.getMessage());
					}
				});
	}

	private void display_now() {

		final WaterBlueDialog waterBlueDialog = new WaterBlueDialog(mActivity);
		waterBlueDialog.setContent("确定更新充电桩的信息发布？");
		waterBlueDialog.setLeftBtnText("取消");
		waterBlueDialog.setRightBtnText("确认");
		waterBlueDialog.setRightListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				waterBlueDialog.dismiss();
				DialogUtil.showLoadingDialog(mActivity, R.string.loading);
				share(shareTimes);
			}
		});
		waterBlueDialog.setLeftListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				waterBlueDialog.dismiss();
			}
		});
		waterBlueDialog.show();
	}

	// 取消发布
	private void unshare() {
		DialogUtil.showLoadingDialog3(mActivity, R.string.canceling, 0);

		WebAPIManager.getInstance().unshare(pile.getPileId(),
				user == null ? null : user.getName(),
				user == null ? null : user.getPhone(),
				new WebResponseHandler<Pile>(mActivity) {
					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(mActivity, e);
						DialogUtil.dismissDialog();
					}

					@Override
					public void onFailure(WebResponse<Pile> response) {
						super.onFailure(response);
						DialogUtil.dismissDialog();
						TipsUtil.showTips(mActivity, response);
					}

					@Override
					public void onSuccess(WebResponse<Pile> response) {
						super.onSuccess(response);
						DialogUtil.dismissDialog();
						pile = response.getResultObj();
						DbHelper.getInstance(mActivity).insertPile(pile);
						updateUI();
						chongdianfuwufei_tvs.setText(0.00 + "");

						display_statue2.setVisibility(View.VISIBLE);
						display_statue.setVisibility(View.GONE);
						ToastUtil.show(mActivity, response.getMessage());
					}

				});
	}

	// 发布
	private void share1() {
		DialogUtil.showLoadingDialog(this, R.string.loading);
		WebAPIManager.getInstance().share(pile.getPileId(), shareTimes,
				user == null ? null : user.getName(),
				user == null ? null : user.getPhone(),
				new WebResponseHandler<Pile>(mActivity) {

					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(mActivity, e);
						DialogUtil.dismissDialog();
					}

					@Override
					public void onFailure(WebResponse<Pile> response) {
						super.onFailure(response);
						TipsUtil.showTips(mActivity, response);
						DialogUtil.dismissDialog();
					}

					@Override
					public void onSuccess(WebResponse<Pile> response) {
						super.onSuccess(response);
						DialogUtil.dismissDialog();
						ToastUtil.show(mActivity, "发布成功");
						display_statue2.setVisibility(View.GONE);
						display_statue.setVisibility(View.VISIBLE);
						pile = response.getResultObj();
						DbHelper.getInstance(mActivity).insertPile(pile); // 更新数据库
						updateUI();
					}

				});
	}

	/*
	 * activity回调
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;

		}
		if (requestCode == ADDTIMEREQUESTCODE) {
			shareTimes = null;
			shareTimes = (List<ShareTime>) data
					.getSerializableExtra("shareTimes");
			refreshShareTimeView();
		}
		if (requestCode == CHARGEFREE) {
			price = data.getStringExtra("et_charge_free");
			if (price == null || price.equals("null") || price.equals("")) {
				price = "0.0";
			}
			chongdianfuwufei_tvs.setText(price + "");
		}
		float pricefree;

		if (!TextUtils.isEmpty(price)) {
			try {
				pricefree = Float.parseFloat(price);
				if (shareTimes.size() != 0) {
					for (int i = 0; i < shareTimes.size(); i++) {
						shareTimes.get(i).setServicePay(pricefree);
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				ToastUtil.show(mActivity, R.string.charge_free_format_error);
				return;
			}
		} else {
			ToastUtil.show(mActivity, R.string.charge_free_empty);
		}
	}

	private void refreshShareTimeView() {
		timeConflict = false;
		lv_content_layout.removeAllViews();
		if (shareTimes != null) {
			int size = shareTimes.size();
			for (int i = 0; i < size; i++) {
				boolean isConflict = false;
				ShareTime shareTime = shareTimes.get(i);// 要显示的分享时间
				Set<Integer> weeksSet = shareTime.createWeeksSet();
				for (int j = 0; j < i; j++) {
					ShareTime compareTime = shareTimes.get(j);// 用于对比的分享时间
					Set<Integer> compareWeeksSet = compareTime.createWeeksSet();
					compareWeeksSet.retainAll(weeksSet);// 求交集
					if (compareWeeksSet.size() > 0) {// 如果两时间段存在交集
						isConflict = isConflict
								|| !TimeUtil.checkShareTimeIndependence(
										shareTime.getStartTime(),
										shareTime.getEndTime(),
										compareTime.getStartTime(),
										compareTime.getEndTime());
					} else {// 没有存在交集,不存在冲突
						isConflict = isConflict || false;
					}
				}
				timeConflict = timeConflict || isConflict;
				View tsv = new TimeSetting2View(this, shareTime, isConflict);
				lv_content_layout.addView(tsv);
			}
		}
		bt_mashangdisplay.setEnabled(!timeConflict);
		bt_mashangdisplays.setEnabled(!timeConflict);
	}
}
