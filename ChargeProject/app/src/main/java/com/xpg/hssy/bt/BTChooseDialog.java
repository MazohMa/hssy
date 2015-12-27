package com.xpg.hssy.bt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.easy.util.AppUtil;
import com.easy.util.EmptyUtil;
import com.easy.util.LogUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeAdapter;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeDevice;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.dialog.WaterBlueDialogBluFailed;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2014年11月5日
 */

public class BTChooseDialog extends Dialog implements OnItemClickListener, android.view.View
		.OnClickListener {

	private boolean allowStopDiscovery;
	private SPFile sp;

	private ListView lv_bluetooth;
	private View pb_small;
	private BTChooseAdapter btListAdapter;
	private BluetoothIBridgeAdapter btAdapter;

	private BTConnectListener onConnectLinstener;// chargefragment监听
	private ImageView cancel;
	private LayoutInflater inflater;
	private Context context;
	private String deviceAddress;
	private boolean isAutoConnect;

	public BTChooseDialog(Context context) {
		super(context, R.style.dialog_no_frame);
		this.context = context;
		inflater = LayoutInflater.from(context);
		init(context);
	}

	@SuppressLint("ShowToast")
	private void init(Context context) {
		sp = new SPFile(getContext(), "config");
		// setContentView(R.layout.bluetooth_choose_dialogs);
		View view = inflater.inflate(R.layout.bluetooth_choose_dialogs, null);
		setContentView(view);
		cancel = (ImageView) view.findViewById(R.id.cancel);
		setCanceledOnTouchOutside(false);
		cancel.setOnClickListener(this);
		btAdapter = BTManager.getInstance().getBTAdapter();
		btListAdapter = new BTChooseAdapter(context, new ArrayList<BluetoothIBridgeDevice>());
		pb_small = getLayoutInflater().inflate(R.layout.progress_bar_small, null);
		lv_bluetooth = (ListView) findViewById(R.id.lv_bluetooth);
		lv_bluetooth.addFooterView(pb_small);
		lv_bluetooth.setAdapter(btListAdapter);
		lv_bluetooth.setOnItemClickListener(this);

		btIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		btIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);// 开始搜索广播
		btIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);// 搜索结束广播
		btIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);// 找到设备广播
		btIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		// btIntentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

		getContext().registerReceiver(btBroadcastReceiver, btIntentFilter);
		BTManager.getInstance().setDiscoveryListener(onDiscoveryListener);// 监听bt发现
		BTManager.getInstance().setConnectListener(defaultConnectListener);// 监听默认连接
	}

	private IntentFilter btIntentFilter = new IntentFilter();
	private BroadcastReceiver btBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Log.e("btDialogReceiver", intent.getAction());
			// 蓝牙状态
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
				int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				if (status == BluetoothAdapter.STATE_ON) {
					// 如果用户正在打开蓝牙搜索列表，则不停搜索，否则自动搜索一次后停止
					if (isShowing()) {
						allowStopDiscovery = false;
					} else {
						allowStopDiscovery = true;
					}
					btAdapter.startDiscovery();
				}
			}
			// 搜索开始
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
				LogUtil.e("", "ACTION_DISCOVERY_STARTED");
			}
			// 搜索完成
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
				LogUtil.e("", "ACTION_DISCOVERY_FINISHED");
			}
			// 找到设备
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				LogUtil.e("", "ACTION_FOUND");
			}
			// 配对
			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
				switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING:
						Log.d("btDialogReceiver", "正在配对......");

						break;
					case BluetoothDevice.BOND_BONDED:
						Log.d("btDialogReceiver", "完成配对");
						// 连接设备
						// BTManager.getInstance().connect(device);
						btListAdapter.setConnectingMac(device.getAddress());
						break;
					case BluetoothDevice.BOND_NONE:
						Log.d("btDialogReceiver", "取消配对");
					default:
						break;
				}
				btListAdapter.notifyDataSetChanged();
			}
			// 请求用户配对
			// if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent
			// .getAction())) {
			//
			// }
		}
	};

	private BTDiscoveryListener onDiscoveryListener = new BTDiscoveryListener() {
		@Override
		public void onDiscoveryFinished() {
			Log.e("btDialogReceiver", "onDiscoveryFinished");
			isAutoConnect = false;
			if (!allowStopDiscovery && !BTManager.getInstance().isConnecting()) {
				btAdapter.startDiscovery();
			} else {
				if (deviceAddress != null) {
					ToastUtil.show(getContext(), "搜索不到该桩");
					Log.e("deviceAddress", "unDeviceFound: deviceAddress" + deviceAddress);
				}
			}
		}

		@Override
		public void onDeviceFound(BluetoothIBridgeDevice device) {
			Log.e("btDialogReceiver", "onDeviceFound: " + device.getDeviceName() + "(" + device
					.getDeviceAddress() + ")");
			Log.e("deviceAddress", "onDeviceFound:" + deviceAddress);
			String mACAddString = device.getDeviceAddress();
			if (mACAddString != null) {
				mACAddString = mACAddString.replaceAll(":", "").trim().toUpperCase();
			}
			//            if (mACAddString == null || mACAddString.startsWith("C01583")
			//                    || mACAddString.endsWith("831500")|| mACAddString.startsWith
			// ("6CECEB")|| mACAddString.startsWith("F4B85E")) {
			//                return;
			//            }
			if (mACAddString == null || !mACAddString.startsWith("001583")) {
				return;
			}

			btListAdapter.add(device);

			//如果和最后一次连接的mac地址相同，并且用户没有打开蓝牙列表，则自动连接
			if (isAutoConnect) {
				String lastMac = sp.getString(MyConstant.BT_MAC_LAST, "non");
				if (!isShowing() && device.getDeviceAddress() != null && device.getDeviceAddress()
						.equals(lastMac)) {
					if (AppUtil.isForeground(context)) {
						ToastUtil.show(getContext(), "发现上次连接的充电桩");
						connect(device);
						return;
					}
				}
			}
//			// TODO gprs+ble充电桩连接蓝牙,待开发
//			if (!isShowing() && device.getDeviceAddress() != null && deviceAddress != null &&
//					device.getDeviceAddress().equals(deviceAddress)) {
//
//				Log.e("deviceAddress", "onDeviceFound: deviceAddress" + deviceAddress);
//				Log.e("btDialogReceiver", "onDeviceFound: gprs+ble充电桩连接蓝牙");
//				if (AppUtil.isForeground(context)) {
//					connect(device);
//				}
//			} else {
//				//
//
//			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position > btListAdapter.getCount() - 1) {
			return;
		}
		BluetoothIBridgeDevice btDevice = (BluetoothIBridgeDevice) btListAdapter.getItem(position);
		dismiss();
		connect(btDevice);
	}

	private void connect(final BluetoothIBridgeDevice btDevice) {
		if (btDevice.getDeviceAddress().equals(btListAdapter.getConnectingMac())) {
			return;
		}

		LogUtil.e("btdialog", "btDevice: " + btDevice.getConnectStatus());
		allowStopDiscovery = true;
		BTManager.getInstance().disconnect();
		if (btDevice.isConnected()) {
			BTManager.getInstance().disconnect(btDevice);
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				btListAdapter.setConnectingMac(btDevice.getDeviceAddress());
				BTManager.getInstance().connect(btDevice);
			}
		}, 500);
	}

	private BTConnectListener defaultConnectListener = new BTConnectListener() {
		@Override
		public void onDisconnected() {
			btListAdapter.setConnectingMac(null);
			if (onConnectLinstener != null) {
				onConnectLinstener.onDisconnected();
			}
		}

		@Override
		public void onConnecting() {
			if (onConnectLinstener != null) {
				onConnectLinstener.onConnecting();
			}
		}

		@Override
		public void onConnected() {
			sp.put(MyConstant.BT_MAC_LAST, btListAdapter.getConnectingMac());
			btListAdapter.setConnectingMac(null);
			dismiss();
			if (onConnectLinstener != null) {
				onConnectLinstener.onConnected();
			}
		}

		@Override
		public void onConnectTimeOut() {
			btListAdapter.setConnectingMac(null);
			ToastUtil.show(getContext(), R.string.bt_conn_timeout);
			if (onConnectLinstener != null) {
				onConnectLinstener.onConnectTimeOut();
			}
		}

		@Override
		public void onConnectFailed() {
			btListAdapter.setConnectingMac(null);

			// ToastUtil.show(getContext(), R.string.bt_conn_fail);
			if (onConnectLinstener != null) {
				onConnectLinstener.onConnectFailed();
			}
		}
	};

	@Override
	public void show() {
		// 不支持蓝牙，退出
		if (btAdapter == null) {
			ToastUtil.show(getContext(), R.string.bt_unsupport);
			return;
		}

		// 开启蓝牙并搜索
		// if (!isRegisteredReceiver) {
		// getContext().registerReceiver(btBroadcastReceiver, btIntentFilter);
		// isRegisteredReceiver = true;
		// }
		if (!btAdapter.isEnabled()) {
			btAdapter.setEnabled(true);
		} else if (!BTManager.getInstance().isConnecting()) {
			allowStopDiscovery = false;
			btAdapter.startDiscovery();
		}
		super.show();
	}

	@Override
	public void dismiss() {
		// if (isRegisteredReceiver) {
		// getContext().unregisterReceiver(btBroadcastReceiver);
		// isRegisteredReceiver = false;
		// }
		allowStopDiscovery = true;
		btAdapter.stopDiscovery();
		pb_small.setVisibility(View.INVISIBLE);
		btListAdapter.clear();
		pb_small.setVisibility(View.VISIBLE);
		super.dismiss();
	}

	/**
	 * 判断gprs+蓝牙是否打开，进行指定连接
	 */
	public void tryConnectSingleBle(String deviceAddress) {
		this.deviceAddress = deviceAddress;
		// 不支持蓝牙，退出
		if (btAdapter == null) {
			ToastUtil.show(getContext(), R.string.bt_unsupport);
			return;
		}
		// 开始搜索
		allowStopDiscovery = true;
		//莫名其妙的crash
		try {
			btAdapter.startDiscovery();
		} catch (Exception e) {
		}
	}

	/**
	 * 判断蓝牙是否打开，进行自动连接
	 */
	public void tryAutoConnect() {
		// 不支持蓝牙，退出
		if (btAdapter == null) {
			ToastUtil.show(getContext(), R.string.bt_unsupport);
			return;
		}

		// 蓝牙已经打开并且存在最后一次连接的mac地址
		if (btAdapter.isEnabled() && !EmptyUtil.isEmpty(sp.getString(MyConstant.BT_MAC_LAST, "")
		)) {
			// 开始搜索
			allowStopDiscovery = true;
			//莫名其妙的crash
			try {
				btAdapter.startDiscovery();
				isAutoConnect = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void unregisterReceiver() {
		try {
			getContext().unregisterReceiver(btBroadcastReceiver);
		} catch (Exception e) {
		}
	}

	public BTConnectListener getOnConnectLinstener() {
		return onConnectLinstener;
	}

	public void setOnConnectLinstener(BTConnectListener onConnectLinstener) {
		this.onConnectLinstener = onConnectLinstener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cancel:
				dismiss();
				break;
			default:
				break;
		}
	}
}
