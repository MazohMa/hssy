package com.xpg.hssy.adapter;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssy.main.activity.AddTimeActivity;
import com.xpg.hssy.main.activity.SettingTimeActivity;
import com.xpg.hssy.util.NumToAphabetUtil;
import com.xpg.hssychargingpole.R;

/**
 * SettingTimeAdapter
 * 
 * @author Mazoh 电桩设置时间
 * @version 2.0
 * 
 */
public class SettingTimeAdapter extends
		EasyAdapter<SettingTimeAdapter.AdapterItem> {

	private Context context;
	private AdapterItem item;
	private int modifyIndex = -1;
	private Activity mActivity;

	public SettingTimeAdapter(Activity activity) {
		super(activity);
		this.context = activity;
		mActivity = activity;

	}

	public SettingTimeAdapter(Activity activity,
			List<SettingTimeAdapter.AdapterItem> items) {
		super(activity, items);
		this.context = activity;
		mActivity = activity;
	}

	public int getModifyIndex() {
		return modifyIndex;
	}

	@Override
	protected ViewHolder newHolder() {
		return new ViewHolder() {

			private TextView tv_share_time;
			private TextView tv_share_weekly;
			private ImageButton ib_left_arrow;
			// private String alphabet;
			private TextView time_desc;
			private CheckBox cb_selected;
			private OnCheckedChangeListener checkedChangeListener;
			private OnClickListener onClickListener;

			{
				checkedChangeListener = new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							select(position);
						} else {
							unselect(position);
						}
					}
				};
				onClickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (mode) {
						case MODE_MULTIPLE_SELECT: {
							cb_selected.setChecked(!isSelected);
							break;
						}
						case MODE_NON: {
							modifyIndex = position;
							ShareTime shareTime = get(modifyIndex).shareTime;
							Intent modifyShareTime = new Intent(mActivity,
									AddTimeActivity.class);
							modifyShareTime.putExtra(
									AddTimeActivity.INTENT_KEY_BEGIN_TIME,
									shareTime.getStartTime());
							modifyShareTime.putExtra(
									AddTimeActivity.INTENT_KEY_END_TIME,
									shareTime.getEndTime());
							modifyShareTime.putExtra(
									AddTimeActivity.INTENT_KEY_WEEKS,
									(Serializable) shareTime.getWeeks());
							mActivity
									.startActivityForResult(
											modifyShareTime,
											SettingTimeActivity.REQUEST_MODIFY_SHARE_TIME);
							break;
						}
						}
					}
				};
			}

			@Override
			protected View init(LayoutInflater arg0) {
				View view = arg0.inflate(
						R.layout.setting_time_perid_list_layout, null);
				tv_share_time = (TextView) view
						.findViewById(R.id.tv_share_time);
				ib_left_arrow = (ImageButton) view
						.findViewById(R.id.ib_left_arrow);
				tv_share_weekly = (TextView) view
						.findViewById(R.id.tv_share_weekly);
				time_desc = (TextView) view.findViewById(R.id.time_desc);
				cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
				cb_selected.setOnCheckedChangeListener(checkedChangeListener);
				view.setOnClickListener(onClickListener);
				return view;
			}

			@Override
			protected void update() {

				item = get(position);
				int sn = position + 1;
				time_desc.setText("时间段" + sn);
				tv_share_time.setText(item.shareTime.getStartTimeAsString()
						+ "-" + item.shareTime.getEndTimeAsString());
				tv_share_weekly.setText(item.shareTime.getWeeks2String());
				// 显示冲突
				if (item.isConflict) {
					time_desc.setText(time_desc.getText() + item.conflictTips);
					time_desc.setTextColor(context.getResources().getColor(
							R.color.red));
					tv_share_time.setTextColor(context.getResources().getColor(
							R.color.red));
					tv_share_weekly.setTextColor(context.getResources()
							.getColor(R.color.red));
				} else {
					time_desc.setTextColor(context.getResources().getColor(
							R.color.water_blue));
					tv_share_time.setTextColor(context.getResources().getColor(
							R.color.text_gray));
					tv_share_weekly.setTextColor(context.getResources()
							.getColor(R.color.text_gray));
				}
				// 显示选择
				if (isSelected) {
					cb_selected.setChecked(true);
				} else {
					cb_selected.setChecked(false);

				}

				switch (mode) {
				case MODE_MULTIPLE_SELECT: {
					cb_selected.setVisibility(View.VISIBLE);
					ib_left_arrow.setVisibility(View.GONE);
					break;
				}
				case MODE_NON: {
					cb_selected.setVisibility(View.GONE);
					ib_left_arrow.setVisibility(View.VISIBLE);
					break;
				}
				}
			}
		};
	}

	static public class AdapterItem {
		private ShareTime shareTime;
		private boolean isConflict;
		private String conflictTips;

		public AdapterItem(ShareTime shareTime, boolean isConflict,
				String conflictTips) {
			this.shareTime = shareTime;
			this.isConflict = isConflict;
			this.conflictTips = conflictTips;
		}

		public ShareTime getShareTime() {
			return shareTime;
		}

		public void setShareTime(ShareTime shareTime) {
			this.shareTime = shareTime;
		}

		public boolean isConflict() {
			return isConflict;
		}

		public void setConflict(boolean isConflict) {
			this.isConflict = isConflict;
		}

		public String getConflictTips() {
			return conflictTips;
		}

		public void setConflictTips(String conflictTips) {
			this.conflictTips = conflictTips;
		}
	}

}
