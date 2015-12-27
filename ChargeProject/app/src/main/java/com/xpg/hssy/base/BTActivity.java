package com.xpg.hssy.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xpg.hssy.bt.BTManager;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年11月5日
 * @version 1.0.0
 */

public class BTActivity extends BaseActivity {

	private IntentFilter mBTIntentFilter = new IntentFilter();

	private BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// 蓝牙状态
			if (BluetoothAdapter.ACTION_STATE_CHANGED
					.equals(intent.getAction())) {
				int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						-1);
				onBTStatusChange(status);
			}
			// 开始搜索
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent
					.getAction())) {
				onBTDiscoveryStart();
			}
			// 搜索完成
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent
					.getAction())) {
				onBTDiscoveryFinish();
			}
			// 找到设备
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				log("ACTION_FOUND");
				onBTFound(device);
			}
			// 配对
			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent
					.getAction())) {
				onBTBondStatusChange(device);
			}
			// 连接
			if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())) {
				log("ACTION_ACL_CONNECTED");
				onBTConnect(device);
			}
			// 断开连接
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent
					.getAction())) {
				log("ACTION_ACL_DISCONNECTED");
				onBTDisconnect(device);
			}
		}
	};

	// private BTReceiveListener mBtReceiveListener = new BTReceiveListener() {
	// @Override
	// public void receive(byte[] data) {
	// onBTReceive(data);
	// }
	// };

	@Override
	protected void onStart() {
		super.onStart();
		mBTIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mBTIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		mBTIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mBTIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		mBTIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		mBTIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		mBTIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mBTBroadcastReceiver, mBTIntentFilter);

		// BTManager.getInstance().addReceiveListener(mBtReceiveListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBTBroadcastReceiver);
		// BTManager.getInstance().removeReceiveListener(mBtReceiveListener);
	}

	// protected void onBTReceive(byte[] data) {
	// }

	protected void onBTStatusChange(int status) {
		log("onBTStatusChange: " + status);
	}

	protected void onBTDiscoveryStart() {
		log("onBTDiscoveryStart");
	}

	protected void onBTDiscoveryFinish() {
		log("onBTDiscoveryFinish");
	}

	protected void onBTFound(BluetoothDevice device) {
		log("onBTFound: " + device.getName() + "/" + device.getAddress() + "/"
				+ device.getBondState());
	}

	protected void onBTBondStatusChange(BluetoothDevice device) {
		log("onBTBondStatusChange: " + device.getBondState());
	}

	protected void onBTConnect(BluetoothDevice device) {
		log("onBTConnect");
	}

	protected void onBTDisconnect(BluetoothDevice device) {
		BTManager.getInstance().disconnect();
		log("onBTDisconnect");
	}
}
