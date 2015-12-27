package com.xpg.hssy.bt;

import com.ivt.bluetooth.ibridge.BluetoothIBridgeDevice;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年11月5日
 * @version 1.0.0
 */

public interface BTDiscoveryListener {
	void onDiscoveryFinished();

	void onDeviceFound(BluetoothIBridgeDevice bluetoothibridgedevice);
}
