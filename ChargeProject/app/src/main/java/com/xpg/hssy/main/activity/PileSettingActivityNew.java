package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * PileSettingActivityNew
 *
 * @author Mazoh 电桩设置
 * @version 2.0.0
 * @Description
 * @Email 471977848@qq.com
 */
public class PileSettingActivityNew extends BaseActivity {

	public static final String TAG = "PileSettingActivityNew";
	private static final int REQUEST_SHOUQUAN = 555;
	private static final int PILENAME = 11;
	private static final int PILEDISPLAY = 11345;
	public final static int PILECURRENTTIMEPROOFREADNEW = 12345;
	private Pile pile;
	private String pile_id;
	private String user_id;
	private SharedPreferences sp;
	private RelativeLayout pilename_layout_id;//电桩名字
	private RelativeLayout shouquanyonghu_layout_id;// 授权用户
	private RelativeLayout dianzhuangxinxi_layout_id;// 电桩信息
	private RelativeLayout display_state_layout_id; // 电桩发布
	private RelativeLayout pile_current_time_layout_id; // 电桩当前时间
	private TextView shouquan_user_tv;
	private ImageButton dengguang_ib;
	private ImageButton door_ib;
	private ImageButton fuhe_ib;
	private TextView name_tv, display_state_tv;
    private LoadingDialog loadingDialog = null ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isNetworkConnected()) {

			WebAPIManager.getInstance().getPileById(pile_id, new WebResponseHandler<Pile>(this) {

						@Override
						public void onStart() {
							super.onStart();
							loadingDialog = new LoadingDialog(PileSettingActivityNew.this,R.string.loading) ;
							loadingDialog.showDialog();

						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							loadingDialog.dismiss();
						}

						@Override
						public void onFailure(WebResponse<Pile> response) {
							super.onFailure(response);
							loadingDialog.dismiss();
							ToastUtil.show(self, response.getMessage() + "");
						}

						@Override
						public void onSuccess(WebResponse<Pile> response) {
							super.onSuccess(response);
							loadingDialog.dismiss();

							Pile pileOwn = response.getResultObj();
							if (pileOwn != null) {
								DbHelper.getInstance(PileSettingActivityNew.this).insertPile(pileOwn);
								updateUI();

							}
						}

					});
		} else {
			// 数据库获取个人桩
			updateUI();
		}
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		pile_id = getIntent().getStringExtra("pile_id");
		user_id = sp.getString("user_id", "");

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.pile_setting_activity_layout);
		setTitle("电桩设置");
		pilename_layout_id = (RelativeLayout) findViewById(R.id.pilename_layout_id);
		shouquanyonghu_layout_id = (RelativeLayout) findViewById(R.id.shouquanyonghu_layout_id);
		dianzhuangxinxi_layout_id = (RelativeLayout) findViewById(R.id.dianzhuangxinxi_layout_id);
		display_state_layout_id = (RelativeLayout) findViewById(R.id.display_state_layout_id);
		pile_current_time_layout_id = (RelativeLayout) findViewById(R.id.pile_current_time_layout_id);

		display_state_tv = (TextView) findViewById(R.id.display_state_tv);
		name_tv = (TextView) findViewById(R.id.name_tv);
		shouquan_user_tv = (TextView) findViewById(R.id.shouquan_user_tv);
		dengguang_ib = (ImageButton) findViewById(R.id.dengguang_ib);
		door_ib = (ImageButton) findViewById(R.id.door_ib);
		fuhe_ib = (ImageButton) findViewById(R.id.fuhe_ib);

	}

	private void updateUI() {

		pile = DbHelper.getInstance(this).getPileDao().load(this.pile_id); //
		// 数据库获取个人桩
		if (pile != null) {
			name_tv.setText(pile.getPileNameAsString() + "");
			shouquan_user_tv.setText((pile.getFamilyNumber() == null || pile.getFamilyNumber().equals(0))
					? "0" : pile.getFamilyNumber() + "");
			if (pile.getShareState() == null || pile.getShareState() == Pile.SHARE_STATUS_NO) {
				display_state_tv.setText("未发布");
			} else {
				display_state_tv.setText("已发布");
			}
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		dianzhuangxinxi_layout_id.setOnClickListener(this);
		shouquanyonghu_layout_id.setOnClickListener(this);
		display_state_layout_id.setOnClickListener(this);
		pilename_layout_id.setOnClickListener(this);
		pile_current_time_layout_id.setOnClickListener(this);
		dengguang_ib.setOnClickListener(this);
		door_ib.setOnClickListener(this);
		fuhe_ib.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 != Activity.RESULT_OK) {
			return;
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
			case R.id.dengguang_ib:// 电桩信息事件
				boolean is_switchdengguang = sp.getBoolean("dengguang_ib", true);
				if (is_switchdengguang) {
					dengguang_ib.setBackgroundResource(R.drawable.icon_on);
					Editor et = sp.edit();
					et.putBoolean("dengguang_ib", false);
					et.commit();
				} else {
					dengguang_ib.setBackgroundResource(R.drawable.icon_off);

					Editor et = sp.edit();
					et.putBoolean("dengguang_ib", true);
					et.commit();
				}
				break;

			case R.id.door_ib:
				boolean is_switchdoor = sp.getBoolean("door_ib", true);
				if (is_switchdoor) {
					door_ib.setBackgroundResource(R.drawable.icon_on);
					Editor et = sp.edit();
					et.putBoolean("door_ib", false);
					et.commit();
				} else {
					door_ib.setBackgroundResource(R.drawable.icon_off);
					Editor et = sp.edit();
					et.putBoolean("door_ib", true);
					et.commit();
				}
				break;
			case R.id.fuhe_ib:// 电桩负荷
				boolean is_switchfuhe = sp.getBoolean("fuhe_ib", true);
				if (is_switchfuhe) {

					fuhe_ib.setBackgroundResource(R.drawable.icon_on);
					Editor et = sp.edit();
					et.putBoolean("fuhe_ib", false);
					et.commit();

				} else {
					fuhe_ib.setBackgroundResource(R.drawable.icon_off);

					Editor et = sp.edit();
					et.putBoolean("fuhe_ib", true);
					et.commit();
				}
				break;
			case R.id.pilename_layout_id:// 桩名字
				if (isNetworkConnected()) {
					Intent _intentEditPilename = new Intent(this, EditPileNameActivity.class);
					_intentEditPilename.putExtra("pileName", name_tv.getText() == null ? "" : name_tv.getText().toString());
					_intentEditPilename.putExtra("pile", pile);

					startActivityForResult(_intentEditPilename, PILENAME);
					overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				} else {
				}
				break;
			case R.id.shouquanyonghu_layout_id:// 授权用户事件
				if (isNetworkConnected()) {
					turnActivityGrantUser();
				} else {
				}
				break;
			case R.id.dianzhuangxinxi_layout_id:// 电桩信息事件
				turnActivityPileMes();
				break;
			case R.id.display_state_layout_id:// 电桩发布
				Intent displayIntent = new Intent(this, PileDisplayActivity.class);
				displayIntent.putExtra("pile_id", pile_id);
				startActivityForResult(displayIntent, PILEDISPLAY);
				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				break;
			case R.id.pile_current_time_layout_id:// 电桩当前时间校对
				if (isNetworkConnected()) {
					Intent intentPileCurrentTime = new Intent(this, PileCurrentTimeProofreadNewActivity.class);
					intentPileCurrentTime.putExtra("pile_id", pile_id);
					startActivityForResult(intentPileCurrentTime, PILECURRENTTIMEPROOFREADNEW);
					overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				} else {

				}
				break;
			default:
				break;
		}
	}

	/**
	 * 授权用户管理
	 */
	private void turnActivityGrantUser() {

		Intent shouquanyonghuguanli_intent = new Intent(this, AuthorizedUserManageActivity.class);
		shouquanyonghuguanli_intent.putExtra("pile_id", pile_id);
		startActivityForResult(shouquanyonghuguanli_intent, REQUEST_SHOUQUAN);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	/**
	 * 电桩信息
	 */
	private void turnActivityPileMes() {
		Intent dianzhuangsetting_settingmessage_intent = new Intent(this, PileManageToSettingInfoActivity.class);
		// dianzhuangsetting_settingmessage_intent.putExtra("pile", pile);
		dianzhuangsetting_settingmessage_intent.putExtra("pile_id", pile_id);
		dianzhuangsetting_settingmessage_intent.putExtra("firstFromSetting", true);

		startActivity(dianzhuangsetting_settingmessage_intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	@Override
	protected void onLeftBtn(View v) {
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
