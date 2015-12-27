package com.xpg.hssy.dialog;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.popup.EasyPopup;
import com.xpg.hssychargingpole.R;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年4月2日
 */

public class WaterBlueListPop extends EasyPopup implements OnItemClickListener {
	private Context context;
	private ListView lv;
	private FilterAdapter adapter;
	private OnItemClickListener onItemClickListener;

	public WaterBlueListPop(Context context, List<Integer> items) {
		super(context, R.layout.filter_list_white, LayoutParams.MATCH_PARENT, LayoutParams
				.WRAP_CONTENT);
		this.context = context;
		adapter = new FilterAdapter(context, items);
		lv = (ListView) getContentView();
		lv.addHeaderView(new View(context));
		lv.setOnItemClickListener(this);
		lv.setAdapter(adapter);
		lv.addFooterView(new View(context));
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		adapter.select(position - 1);
		dismiss();
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(av, v, position, getSelectedItem());
		}
	}

	public int getItem(int position) {
		Integer id = adapter.get(position);
		return id == null ? -1 : id;
	}

	public int getSelectedItem() {
		Integer id = adapter.getSelectedItem();
		return id == null ? -1 : id;
	}

	public void select(Integer item) {
		adapter.select(item);
	}

	/**
	 * 筛选项
	 *
	 * @author Joke
	 */
	static class FilterAdapter extends EasyAdapter<Integer> {
		public FilterAdapter(Context context, List<Integer> items) {
			super(context, items);
			setMode(EasyAdapter.MODE_RADIO_GROUP);
			select(0);
		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				TextView tv;
				ImageView iv_check;

				@Override
				protected View init(LayoutInflater arg0) {
					View v = arg0.inflate(R.layout.filter_adapter_white, null);
					tv = (TextView) v.findViewById(R.id.tv);
					iv_check = (ImageView) v.findViewById(R.id.iv_check);
					return v;
				}

				@Override
				protected void update() {
					tv.setText(get(position));
					if (isSelected) {
						iv_check.setVisibility(View.VISIBLE);
					} else {
						iv_check.setVisibility(View.INVISIBLE);
					}
				}
			};
		}
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

}
