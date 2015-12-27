package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.db.pojo.AddressSearchRecode;
import com.xpg.hssychargingpole.R;

import java.util.List;

public class AddressAdapter extends EasyAdapter<AddressSearchRecode> {

	private AddressSearchRecode locationAddressResult;

	public AddressAdapter(Context context) {
		super(context);
	}

	public AddressAdapter(Context context, List<AddressSearchRecode> items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {
		return new ViewHolder() {

			ImageView iv_left_icon;
			ImageView iv_right_icon;
			TextView tv_name;
			TextView tv_address;
			TextView tv_message;
			View view;

			@Override
			protected View init(LayoutInflater arg0) {
				view = arg0.inflate(R.layout.address_item, null);
				iv_left_icon = (ImageView) view.findViewById(R.id.iv_left_icon);
				iv_right_icon = (ImageView) view
						.findViewById(R.id.iv_right_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				tv_address = (TextView) view.findViewById(R.id.tv_address);
				tv_message = (TextView) view.findViewById(R.id.tv_message);
				return view;
			}

			@Override
			protected void update() {
				AddressSearchRecode searchResult = items.get(position);
				switch (searchResult.getRecodeType()) {
				case AddressSearchRecode.FROM_LOCATION: {
					view.setEnabled(true);
					iv_left_icon.setImageResource(R.drawable.find_icon_address);
					iv_left_icon.setVisibility(View.VISIBLE);
					tv_address.setVisibility(View.VISIBLE);
					iv_right_icon.setVisibility(View.GONE);
					tv_name.setVisibility(View.GONE);
					tv_message.setVisibility(View.GONE);
					tv_address.setText(searchResult.getAddress());
					tv_address.setTextColor(context.getResources().getColor(
							R.color.water_blue));
					break;
				}
				case AddressSearchRecode.FROM_SEARCH: {
					view.setEnabled(true);
					iv_left_icon.setImageResource(R.drawable.find_icon_search);
					iv_left_icon.setVisibility(View.VISIBLE);
					iv_right_icon.setVisibility(View.VISIBLE);
					tv_name.setVisibility(View.VISIBLE);
					tv_address.setVisibility(View.VISIBLE);
					tv_message.setVisibility(View.GONE);
					tv_name.setText(searchResult.getName());
					tv_address.setText(searchResult.getAddress());
					tv_address.setTextColor(context.getResources().getColor(
							R.color.gray));
					break;
				}
				case AddressSearchRecode.FROM_RECODE: {
					view.setEnabled(true);
					iv_left_icon.setImageResource(R.drawable.find_icon_history);
					iv_left_icon.setVisibility(View.VISIBLE);
					iv_right_icon.setVisibility(View.VISIBLE);
					tv_name.setVisibility(View.VISIBLE);
					tv_address.setVisibility(View.VISIBLE);
					tv_message.setVisibility(View.GONE);
					tv_name.setText(searchResult.getName());
					tv_address.setText(searchResult.getAddress());
					tv_address.setTextColor(context.getResources().getColor(
							R.color.gray));
					break;
				}
				case AddressSearchRecode.SHOW_TIPS: {
					view.setEnabled(false);
					iv_left_icon.setVisibility(View.GONE);
					iv_right_icon.setVisibility(View.GONE);
					tv_name.setVisibility(View.GONE);
					tv_address.setVisibility(View.GONE);
					tv_message.setText(searchResult.getName());
					tv_message.setVisibility(View.VISIBLE);
					break;
				}

				case AddressSearchRecode.DELETE_ALL_ADDRESS: {
					view.setEnabled(true);
					iv_left_icon.setVisibility(View.GONE);
					iv_right_icon.setVisibility(View.GONE);
					tv_name.setVisibility(View.GONE);
					tv_address.setVisibility(View.GONE);
					tv_message.setText(context
							.getText(R.string.delete_all_recode));
					tv_message.setVisibility(View.VISIBLE);
					break;
				}
				}
			}
		};
	}

	@Override
	public void clear() {
		super.clear();
		if (locationAddressResult != null) {
			add(0, locationAddressResult);
		}
	}

	public AddressSearchRecode getLocationAddressResult() {
		return locationAddressResult;
	}

	public void setLocationAddressResult(
			AddressSearchRecode locationAddressResult) {
		if (this.locationAddressResult != null) {
			remove(0);
		}
		this.locationAddressResult = locationAddressResult;
		add(0, locationAddressResult);
	}
}
