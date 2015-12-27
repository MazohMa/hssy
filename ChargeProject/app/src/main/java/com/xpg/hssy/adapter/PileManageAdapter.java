package com.xpg.hssy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.main.activity.ChargeRecordActivity;
import com.xpg.hssy.main.activity.PileSettingActivityNew;
import com.xpg.hssy.main.activity.PileShareActivityNew;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * @author Mazoh 桩管理模块
 */
public class PileManageAdapter extends EasyAdapter<Pile> {
	public static final int INTOPILESETTING = 1234;
	private Activity context;
	private String user_id;
	private SharedPreferences sp;

	public PileManageAdapter(Context context) {
		super(context);
		this.context = (Activity) context;
		initData(context);

	}

	public PileManageAdapter(Context context, List<Pile> items) {
		super(context, items);
		this.context = (Activity) context;
		initData(context);

	}

	private void initData(Context context) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
	}

	@Override
	protected ViewHolder newHolder() {
		return new ViewHolder() {

			private ImageView iv_icon;
			private TextView tv_location, tv_name, tv_display;
			private TextView appoint_order, pile_setting, charge_record;
			private Pile pile;
			private OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (view.getId()) {
						case R.id.appoint_order:
							if (!pile.getUserid().equals(user_id)) {
								return;
							} else {
								Intent intent = new Intent(context, PileShareActivityNew.class);
								intent.putExtra("user_id", user_id);
								intent.putExtra("pile_id", pile.getPileId());
								context.startActivityForResult(intent, INTOPILESETTING);
								context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
							}
							break;
						case R.id.pile_setting:
							if (!pile.getUserid().equals(user_id)) {
								return;

							} else {
								Intent intentSetting = new Intent(context, PileSettingActivityNew.class);
								intentSetting.putExtra("user_id", user_id);
								intentSetting.putExtra("pile_id", pile.getPileId());
								context.startActivityForResult(intentSetting, INTOPILESETTING);
								context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
							}
							break;
						case R.id.charge_record:
							Intent intent = new Intent(context, ChargeRecordActivity.class);
							intent.putExtra("pile_id", pile.getPileId());
							context.startActivityForResult(intent, position);
							context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
							break;
						default:
							break;
					}
				}
			};

			public void setEvent() {
				appoint_order.setOnClickListener(listener);
				pile_setting.setOnClickListener(listener);
				charge_record.setOnClickListener(listener);
			}

			@Override
			protected void update() {
				pile = get(position);
				String coverCrop = pile.getCoverCropImg();// pic 剪切图路径
				String url = coverCrop;
				DisplayImageOptions option = ImageLoaderManager.createDisplayOptionsWtichImageResurces(R.drawable.find_sanyoubg2, R.drawable.find_sanyoubg2, R
						.drawable.find_sanyoubg2);
				ImageLoaderManager.getInstance().displayImage(url, iv_icon, option, true);

				tv_location.setText(pile.getLocationAsString());
				tv_name.setText(pile.getPileName() == null ? "" : pile.getPileName());
				if (pile.getShareState() == null || pile.getShareState() == Pile.SHARE_STATUS_NO) {
					tv_display.setText("未发布");
					tv_display.setBackgroundResource(R.drawable.shape_orange_round_small);
				} else {
					tv_display.setText("已发布");
					tv_display.setBackgroundResource(R.drawable.shape_water_blue_round_middle);
				}
				if (!get(position).getUserid().equals(user_id)) {
					//不是桩主
					appoint_order.setEnabled(false);
					pile_setting.setEnabled(false);
				} else {
					//是桩主
					appoint_order.setEnabled(true);
					pile_setting.setEnabled(true);
				}
			}

			@Override
			protected View init(LayoutInflater arg0) {
				View view = arg0.inflate(R.layout.pile_manage_item, null);
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_location = (TextView) view.findViewById(R.id.tv_location);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				tv_display = (TextView) view.findViewById(R.id.tv_display);
				appoint_order = (TextView) view.findViewById(R.id.appoint_order);
				pile_setting = (TextView) view.findViewById(R.id.pile_setting);
				charge_record = (TextView) view.findViewById(R.id.charge_record);
				setEvent();
				return view;
			}
		};
	}

}
