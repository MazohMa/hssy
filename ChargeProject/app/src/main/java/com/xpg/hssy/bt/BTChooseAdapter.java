package com.xpg.hssy.bt;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeDevice;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeDevice.BondStatus;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年11月5日
 * @version 1.0.0
 */

public class BTChooseAdapter extends BaseAdapter {
	private Context context;
	private List<BluetoothIBridgeDevice> items;
	private String connectingMac;

	public BTChooseAdapter(Context context, List<BluetoothIBridgeDevice> items) {
		this.context = context;
		this.items = items;
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	public void add(BluetoothIBridgeDevice device) {
		for (BluetoothIBridgeDevice item : items) {
			if (item.getDeviceAddress().equals(device.getDeviceAddress())) {
				return;
			}
		}
		items.add(device);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BluetoothIBridgeDevice device = items.get(position);

		TextView tv_name = null;
		TextView tv_mac = null;
		TextView tv_bond = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_bluetooth_choose, null);
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			tv_mac = (TextView) convertView.findViewById(R.id.tv_mac);
			tv_bond = (TextView) convertView.findViewById(R.id.tv_bond);
			convertView.setTag(R.id.tv_name, tv_name);
			convertView.setTag(R.id.tv_mac, tv_mac);
			convertView.setTag(R.id.tv_bond, tv_bond);
		} else {
			tv_name = (TextView) convertView.getTag(R.id.tv_name);
			tv_mac = (TextView) convertView.getTag(R.id.tv_mac);
			tv_bond = (TextView) convertView.getTag(R.id.tv_bond);
		}

		if (EmptyUtil.isEmpty(device.getDeviceName())) {
			tv_name.setText(device.getDeviceAddress());
		} else {
			tv_name.setText(device.getDeviceName());
		}
		tv_mac.setText("");
		if (device.getDeviceAddress().equals(connectingMac)) {
			tv_bond.setText("连接中...");
			tv_bond.setTextColor(context.getResources().getColor(R.color.blu_dialog_text_right));
		} else {
			if (device.getBondStatus() == BondStatus.STATE_BONDING) {
				tv_bond.setText("配对中...");
				tv_bond.setTextColor(context.getResources().getColor(R.color.blu_dialog_text_right));
			} else if (device.getBondStatus() == BondStatus.STATE_BONDED) {
				tv_bond.setText("已配对");
				tv_bond.setTextColor(context.getResources().getColor(R.color.text_wather_blue));
			} else {
				tv_bond.setText("未配对");
				tv_bond.setTextColor(context.getResources().getColor(R.color.blu_dialog_text_right));

			}
		}

		return convertView;
	}

	public String getConnectingMac() {
		return connectingMac;
	}

	public void setConnectingMac(String connectingMac) {
		this.connectingMac = connectingMac;
		notifyDataSetChanged();
	}
}
