package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssychargingpole.R;

import java.util.List;

public class AppointmentAdapter extends EasyAdapter<String> {

	public AppointmentAdapter(Context context) {
		super(context);
	}

	public AppointmentAdapter(Context context, List<String> items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {
		return new itemHolder();
	}

	private class itemHolder extends ViewHolder {
		TextView textView;

		@Override
		protected View init(LayoutInflater arg0) {
			View view = arg0.inflate(R.layout.layout_appoint_time_pop_item,
					null);
			textView = (TextView) view.findViewById(R.id.tv_menu);
			return view;
		}

		@Override
		protected void update() {
			String item = get(position);
			textView.setText(item);
			textView.setSelected(isSelected);
		}
	}
}
