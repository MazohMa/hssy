package com.xpg.hssy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xpg.hssy.bean.TimeItem;
import com.xpg.hssy.main.activity.PileInfoNewActivity.adapterItem;
import com.xpg.hssy.main.activity.callbackinterface.IBookingPileOperator;
import com.xpg.hssychargingpole.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@SuppressLint("InflateParams")
public class TimePickAdapter<Operator extends Context & IBookingPileOperator>
		extends ArrayAdapter<adapterItem> {

	private LayoutInflater mInflater;
	private StringBuilder strb;
	private SimpleDateFormat sdf;
	private final String dateFormat = "HH:mm";
	private View selectedItemView;
	private TimeItem selectedItem;
	private Operator operator;

	@SuppressLint("SimpleDateFormat")
	public TimePickAdapter(Operator operator, List<adapterItem> objects) {
		super(operator, R.layout.rl_pile_info_time_pick_item, objects);
		this.operator = operator;
		mInflater = LayoutInflater.from(operator);
		strb = new StringBuilder();
		sdf = new SimpleDateFormat(dateFormat);

		selectedItemView = null;
		selectedItem = null;
	}

	private class viewHolder {
		TextView tv_time_group;
		TextView tv_time_item_left;
		TextView tv_time_item_right;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.rl_pile_info_time_pick_item_rectangle, null);
			viewHolder holder = new viewHolder();
			holder.tv_time_group = (TextView) convertView
					.findViewById(R.id.tv_time_group);
			holder.tv_time_item_left = (TextView) convertView
					.findViewById(R.id.tv_time_item_left);
			holder.tv_time_item_right = (TextView) convertView
					.findViewById(R.id.tv_time_item_right);
			convertView.setTag(holder);
		}
		@SuppressWarnings("unchecked")
		viewHolder holder = (viewHolder) convertView.getTag();
		final adapterItem item = getItem(position);
		if (item.isShowTimeGroup()) {
			holder.tv_time_group.setVisibility(View.VISIBLE);
			holder.tv_time_group.setText(item.getTimeGroup());
		} else {
			holder.tv_time_group.setVisibility(View.GONE);
		}

		initTimeItem(holder.tv_time_item_left, item.getLeftItem());

		if (item.getRightItem() != null) {
			holder.tv_time_item_right.setVisibility(View.VISIBLE);
			initTimeItem(holder.tv_time_item_right, item.getRightItem());
		} else {
			holder.tv_time_item_right.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private void initTimeItem(TextView tv_time_item, final TimeItem item) {
		String time = getTimeString(item);
		tv_time_item.setText(time);
		tv_time_item.setSelected(false);
		tv_time_item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedItem == null) {
					selectedItemView = v;
					selectedItem = item;
				} else {
					selectedItem.setSelected(false);
					selectedItemView.setSelected(false);
					selectedItemView = v;
					selectedItem = item;
				}
				selectedItemView.setSelected(true);
				selectedItem.setSelected(true);
				operator.setBookingStartTimeEndTime(item.getStartTime(),
						item.getEndTime());
			}
		});
		if (item.getItemState() != TimeItem.ITEM_STATE_FREE) {
			tv_time_item.setEnabled(false);
		} else {
			tv_time_item.setEnabled(true);
			if (item.isSelected()) {
				if (selectedItem == null) {
					selectedItemView = tv_time_item;
					selectedItem = item;
				} else {
					selectedItemView.setSelected(false);
					selectedItem.setSelected(false);
					selectedItemView = tv_time_item;
					selectedItem = item;
				}
				selectedItemView.setSelected(true);
				selectedItem.setSelected(true);
				operator.setBookingStartTimeEndTime(item.getStartTime(),
						item.getEndTime());
			}

		}
	}

	@Override
	public void clear() {
		if (selectedItem != null) {
			selectedItem.setSelected(false);
			selectedItemView.setSelected(false);
		}
		selectedItem = null;
		selectedItemView = null;
		super.clear();
	}

	private String getTimeString(TimeItem timeItem) {
		Calendar startC = timeItem.getStartTime();
		startC.getTime();
		Calendar endC = timeItem.getEndTime();
		strb.append(sdf.format(startC.getTime())).append("-");
		if (sdf.format(endC.getTime()).equals("00:00")) {
			strb.append("24:00");
		} else {
			strb.append(sdf.format(endC.getTime()));
		}

		String result = strb.toString();
		strb.setLength(0);
		return result;
	}
}
