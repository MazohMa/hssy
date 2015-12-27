package com.xpg.hssy.bt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年11月4日
 * @version 1.0.0
 */

public class BTConnection {
	private static final String tag = "BTConnection";
	private static final UUID SPP_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private BluetoothDevice mBTDevice;
	private BluetoothSocket mBTSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;

	public BTConnection(BluetoothDevice device) {
		this.mBTDevice = device;
	}

	public boolean connect() {
		if (mBTSocket != null) {
			Log.e(tag, "reconnect!");
			return false;
		}

		try {
			mBTSocket = mBTDevice
					.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
			mBTSocket.connect();
			mInputStream = mBTSocket.getInputStream();
			mOutputStream = mBTSocket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
		}
		return mInputStream != null ? true : false;
	}

	public void disconnect() {
		if (mBTSocket != null) {
			try {
				mInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mBTSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mBTSocket = null;
			mInputStream = null;
			mOutputStream = null;
		}
	}

	public InputStream getInputStream() {
		return mInputStream;
	}

	public OutputStream getOutputStream() {
		return mOutputStream;
	}
}
