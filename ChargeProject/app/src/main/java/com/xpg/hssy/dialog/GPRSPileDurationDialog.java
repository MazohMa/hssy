package com.xpg.hssy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by Jason on 2015/8/25.
 */
public class GPRSPileDurationDialog extends Dialog implements View.OnClickListener, AdapterView
		.OnItemClickListener {

	private DurationAdapter adapter;
	private OnSelectDurationListener listener;

	public GPRSPileDurationDialog(Context context) {
		super(context, R.style.dialog_no_frame);
		init(context);
	}

	public GPRSPileDurationDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	protected GPRSPileDurationDialog(Context context, boolean cancelable, OnCancelListener
			cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	private void init(Context context) {
		setContentView(getLayoutInflater().inflate(R.layout.dialog_duration, null));
		adapter = new DurationAdapter(context);
		adapter.setMode(EasyAdapter.MODE_SINGLE_SELECT);
		adapter.add(0.25);
		adapter.add(0.5);
		adapter.add(1.0);
		adapter.add(2.0);
		adapter.select(0);
		ListView lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		findViewById(R.id.ib_cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		dismiss();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		adapter.select(i);
		if (listener != null) {
			listener.onSelected(adapter.get(i));
		}
		dismiss();
	}

	class DurationAdapter extends EasyAdapter<Double> {

		public DurationAdapter(Context context) {
			super(context);
		}

		public DurationAdapter(Context context, List<Double> items) {
			super(context, items);
		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				TextView tv;

				@Override
				protected View init(LayoutInflater layoutInflater) {
					View v = layoutInflater.inflate(R.layout.adapter_duration, null);
					tv = (TextView) v.findViewById(R.id.tv);
					return v;
				}

				@Override
				protected void update() {
					double hour = get(position);
					if (hour < 1) {
						tv.setText((int) (hour * 60) + "分钟内");
					} else {
						tv.setText((int) hour + "小时内");
					}
					tv.setSelected(isSelected);
				}
			};
		}
	}

	public interface OnSelectDurationListener {
		void onSelected(double hour);
	}

	public OnSelectDurationListener getListener() {
		return listener;
	}

	public void setListener(OnSelectDurationListener listener) {
		this.listener = listener;
	}
}
