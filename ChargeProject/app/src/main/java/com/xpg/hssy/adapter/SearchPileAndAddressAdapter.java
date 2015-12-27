package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by black-Gizwits on 2015/08/19.
 */
public class SearchPileAndAddressAdapter extends EasyAdapter<SearchPileAndAddressAdapter.AdapterItem> {

	public SearchPileAndAddressAdapter(Context context) {
		super(context);
	}

	public SearchPileAndAddressAdapter(Context context, List items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {
		return new mViewHolder();
	}

	private class mViewHolder extends ViewHolder {
		private TextView tv_name;
		private TextView tv_address;
		private TextView tv_distance;

		@Override
		protected View init(LayoutInflater layoutInflater) {
			View v = layoutInflater.inflate(R.layout.layout_search_pile_and_adress_item, null);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_address = (TextView) v.findViewById(R.id.tv_address);
			tv_distance = (TextView) v.findViewById(R.id.tv_distance);
			return v;
		}

		@Override
		protected void update() {
			AdapterItem item = get(position);
			tv_name.setText(item.getName());
			tv_address.setText(item.getAddress());
			//计算距离
			if (item.getLatitude() != null && item.getLongitude() != null) {
				LatLng eLatLng = new LatLng(item.getLatitude(), item.getLongitude());
				CalculateUtil.infuseDistance(context, tv_distance, eLatLng);
			} else {
				tv_distance.setText(R.string.distance_unknow);
			}
		}
	}

	public abstract static class AdapterItem<T> {
		private T source;

		public AdapterItem(T source) {
			this.source = source;
		}

		public abstract String getName();

		public abstract String getAddress();

		public abstract Double getLongitude();

		public abstract Double getLatitude();

		public T getSource() {
			return source;
		}
	}
}
