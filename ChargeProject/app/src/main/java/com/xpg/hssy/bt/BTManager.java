package com.xpg.hssy.bt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ivt.bluetooth.ibridge.BluetoothIBridgeAdapter;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeAdapter.DataReceiver;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeAdapter.EventReceiver;
import com.ivt.bluetooth.ibridge.BluetoothIBridgeDevice;
import com.xpg.hssy.cmdparse.CommandConst.CMPMEnum;
import com.xpg.hssy.cmdparse.CommandParse;
import com.xpg.hssy.cmdparse.DataUtil;
import com.xpg.hssy.util.ThreadUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2014年11月4日
 */

public class BTManager implements EventReceiver, DataReceiver {
	private static final String PINCODE = "1234";
	private static final int MINIMUM_DATA_LENGTH =11 ;
	private static BTManager instance;

	private BTManager() {
	}

	public static BTManager getInstance() {
		if (instance == null) {
			synchronized (BTManager.class) {
				if (instance == null) {
					instance = new BTManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化
	 */
	public void init(Context context) {
		if (mBTAdapter != null) {
			return;
		}
		mBTAdapter = new BluetoothIBridgeAdapter(context);
		if (Build.VERSION.SDK_INT >= 10) {
			mBTAdapter.setLinkKeyNeedAuthenticated(false);
		} else {
			mBTAdapter.setLinkKeyNeedAuthenticated(true);
		}
		mBTAdapter.setPincode(PINCODE);
		mBTAdapter.setAutoWritePincode(true);
		mBTAdapter.setAutoBondBeforConnect(true);
		mBTAdapter.setConnectType(BluetoothIBridgeAdapter.CONNECT_TYPE_I482E);
		mBTAdapter.registerEventReceiver(this);
		mBTAdapter.registerDataReceiver(this);
	}

	/**
	 * 销毁
	 */
	public void destroy() {
		instance = null;
		if (mBTAdapter == null) {
			return;
		}
		disconnect();
		mBTAdapter.unregisterEventReceiver(this);
		mBTAdapter.unregisterDataReceiver(this);
		mBTAdapter.destroy();
	}

	private static final String tag = "BTManager";
	private static final int MSG_CONNECT_TIME_OUT = 13;
	private static final int MSG_CMD_SEND = 21;
	private static final int MSG_CMD_RECEIVE = 22;
	private static final int MSG_CMD_TIME_OUT = 23;
	private static final int RETRY_CMD_MAX = 3;
	private static final int RETRY_CONNECT_MAX = 1;

	private int connectTimeOut = 30000;
	private int retryConnectCount = 0;
	private int cmdTimeOut = 5000;
	private int retryCmdCount = 0;

	private BluetoothIBridgeAdapter mBTAdapter;
	private BluetoothIBridgeDevice mCurrentBTDevice;

	private Timer mConnectTimer;
	private Timer mCmdTimer;

	private BTDiscoveryListener mDiscoveryListener;
	private BTConnectListener mConnectListener;
	private BTReceiveListener mReceiveListener;

	private boolean isConnecting;
	private boolean isConnected;

	private CMPMEnum mSentedCmd;
	private List<byte[]> mSendQueue = Collections.synchronizedList(new LinkedList<byte[]>());

	/********************************************
	 * handler
	 ******************************/

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_CONNECT_TIME_OUT:
					if (mConnectListener != null) {
						mConnectListener.onConnectTimeOut();
					}
					break;
				case MSG_CMD_SEND:
					byte[] sendData = (byte[]) msg.obj;
					onSend(sendData);
					break;
				case MSG_CMD_RECEIVE:
					byte[] receiveData = (byte[]) msg.obj;
					onReceive(receiveData);
					break;
				case MSG_CMD_TIME_OUT:
					retryCmdCount++;
					if (retryCmdCount > RETRY_CMD_MAX) {
						Log.e(tag, "retry cmd fail!");
						disconnect();
						if (mReceiveListener != null) {
							mReceiveListener.onTimeOut();
						}
						return;
					}
					Log.e(tag, "retry cmd: " + retryCmdCount);
					onSendTop();
					break;
				default:
					break;
			}
		}
	};

	/******************************************** handler ******************************/

	/********************************************
	 * 发现设备
	 ***********************************/

	@Override
	public void onDiscoveryFinished() {
		if (mDiscoveryListener != null) {
			mDiscoveryListener.onDiscoveryFinished();
		}
	}

	@Override
	public void onDeviceFound(BluetoothIBridgeDevice bluetoothibridgedevice) {
		if (mDiscoveryListener != null) {
			mDiscoveryListener.onDeviceFound(bluetoothibridgedevice);
		}
	}

	/******************************************** 发现设备 ***********************************/

	/********************************************
	 * 连接
	 ***********************************/

	public void connect(BluetoothIBridgeDevice device) {
		if (device.isConnected()) {
			Log.e(tag, "reconnect!");
			return;
		}

		boolean isValid = mBTAdapter.connectDevice(device, 10);

		if (isValid) {
			Log.v(tag, "connecting");
			//			startConnectTimer();
			isConnecting = true;
			mCurrentBTDevice = device;
			if (mConnectListener != null) {
				mConnectListener.onConnecting();
			}
		} else {
			Log.e(tag, "connect error");
			resetConnectState();
			if (mConnectListener != null) {
				mConnectListener.onConnectFailed();
			}
		}
	}

	public void disconnect(BluetoothIBridgeDevice device) {
		if (mBTAdapter != null) {
			mBTAdapter.disconnectDevice(device);
		}
	}

	public void disconnect() {
		if (mBTAdapter != null) {
			// mBTAdapter.cancleBondProcess();
			//			if (mCurrentBTDevice == null) {
			//				try {
			//                    mCurrentBTDevice = mBTAdapter.getLastConnectedDevice();
			//				} catch (Exception e) {
			//				}
			//			}
			if (mCurrentBTDevice != null && mCurrentBTDevice.isConnected()) {
				mBTAdapter.disconnectDevice(mCurrentBTDevice);
			}
		}
		resetConnectState();
		resetCmdState();
	}

	private void startConnectTimer() {
		cancelConnectTimer();
		mConnectTimer = new Timer();
		mConnectTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.e(tag, "connect time out!");
				handler.sendEmptyMessage(MSG_CONNECT_TIME_OUT);
				disconnect();
			}
		}, connectTimeOut);
	}

	private void cancelConnectTimer() {
		if (mConnectTimer != null) {
			mConnectTimer.cancel();
			mConnectTimer = null;
		}
	}

	@Override
	public void onDeviceConnected(BluetoothIBridgeDevice bluetoothibridgedevice) {
		Log.v(tag, "connect success");
		cancelConnectTimer();
		isConnecting = false;
		isConnected = true;
		if (mConnectListener != null) {
			mConnectListener.onConnected();
		}
	}

	@Override
	public void onDeviceDisconnected(BluetoothIBridgeDevice bluetoothibridgedevice, String s) {
		Log.e(tag, "disconnect");
		resetConnectState();
		resetCmdState();
		if (mConnectListener != null) {
			mConnectListener.onDisconnected();
		}
	}

	@Override
	public void onDeviceConnectFailed(BluetoothIBridgeDevice bluetoothibridgedevice, String s) {
		Log.v(tag, "connect error: " + retryConnectCount);
		if (retryConnectCount < RETRY_CMD_MAX) {
			retryConnectCount++;
			connect(bluetoothibridgedevice);
		} else {
			resetConnectState();
			if (mConnectListener != null) {
				mConnectListener.onConnectFailed();
			}
		}
	}

	private void resetConnectState() {
		if (mBTAdapter != null) {
			mBTAdapter.clearLastConnectedDevice();
		}
		mCurrentBTDevice = null;
		isConnecting = false;
		isConnected = false;
		retryConnectCount = 0;
		cancelConnectTimer();
	}

	private void resetCmdState() {
		mSentedCmd = null;
		mSendQueue.clear();
		retryCmdCount = 0;
		cancelCmdTimer();
	}
	/******************************************** 连接 ***********************************/

	/********************************************
	 * 发送指令
	 ***********************************/

	public void send(byte[] data) {
		// 判断是否主线程
		if (ThreadUtil.isMainThread()) {
			// 直接发送
			onSend(data);
		} else {
			// 给主线程发送
			Message msg = handler.obtainMessage();
			msg.what = MSG_CMD_SEND;
			msg.obj = data;
			handler.sendMessage(msg);
		}
	}

	private void onSend(byte[] data) {
		if (mSendQueue.size() > 100) {
			Log.e(tag, "too many pack to send!");
			return;
		}

		// TODO: duplicate CMD should not be stored, such as getting Device
		// State, getting Device Charing State

		mSendQueue.add(data);
		if (mSentedCmd == null || mSentedCmd == CMPMEnum.None) onSendTop();
	}

	private void onSendTop() {
		byte[] data = mSendQueue.get(0);
		// TODO 测试时注释
		mSentedCmd = CommandParse.getCMDByBytes(Arrays.copyOfRange(data, 6, 8));
		if (mSentedCmd == null || mSentedCmd == CMPMEnum.None) {
			mSendQueue.remove(0);
			if (mSendQueue.size() > 0) {
				onSendTop();
			}
			return;
		}
		// TODO 测试时注释
		if (mBTAdapter != null && mCurrentBTDevice != null) {
			Log.v(tag, "sendCmd:　" + mSentedCmd);
			Log.v(tag, "send: " + DataUtil.getStringFromBytes(data));
			startCmdTimer();
			mBTAdapter.send(mCurrentBTDevice, data, data.length);
		} else {
			Log.e(tag, "not connected!");
		}
	}

	private void startCmdTimer() {
		cancelCmdTimer();
		// Log.e(tag, "startCmdTimer");
		mCmdTimer = new Timer();
		mCmdTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.e(tag, "cmd time out!");
				handler.sendEmptyMessage(MSG_CMD_TIME_OUT);
			}
		}, cmdTimeOut);
	}

	private void cancelCmdTimer() {
		// Log.e(tag, "cancelCmdTimer");
		if (mCmdTimer != null) {
			mCmdTimer.cancel();
			mCmdTimer = null;
		}
	}

	@Override
	public void onWriteFailed(BluetoothIBridgeDevice bluetoothibridgedevice, String s) {
		Log.e(tag, "write error! disconnect!");
		disconnect();
	}

	/******************************************** 发送指令 ***********************************/

	/********************************************
	 * 接收指令
	 ***********************************/

	private void onReceive(byte[] data) {
       // 判断是否为空
		if (data == null) {
			Log.e(tag, "data is null!");
			return;
		}
		Log.v(tag, "receive: " + DataUtil.getStringFromBytes(data));

		// 判断长度
		if (data.length < MINIMUM_DATA_LENGTH) {
			Log.e(tag, "data too short!");
			return;
		}

		// 拆包
		data = CommandParse.ParseCMDRspBytes(data);
		if (data == null) {
			Log.e(tag, "unpack fail!");
			return;
		}

		// 判断命令
		// Log.e(tag, "mSentedCmd:　" + mSentedCmd);
		if (mSentedCmd == null || mSentedCmd == CMPMEnum.None || !mSentedCmd.isEnum(Arrays
				.copyOfRange(data, 0, 2))) {
			Log.e(tag, "invalid cmd!");
			// TODO 测试时注释
			return;
			// TODO 测试时注释
		}

		// 取消超时
		cancelCmdTimer();
		retryCmdCount = 0;

		// 发送下一条
		mSendQueue.remove(0);
		mSentedCmd = null;
		if (mSendQueue.size() > 0) {
			onSendTop();
		}

		// 返回数据
		if (mReceiveListener != null) {
			mReceiveListener.onReceive(data);
		}
	}

	@Override
	public void onDataReceived(BluetoothIBridgeDevice bluetoothibridgedevice, byte[] buffer, int
			len) {
		byte[] data = new byte[len];
		for (int i = 0; i < len; i++) {
			data[i] = buffer[i];
		}
		// 传给主线程
		Message msg = handler.obtainMessage();
		msg.what = MSG_CMD_RECEIVE;
		msg.obj = data;
		handler.sendMessage(msg);
	}

	/********************************************
	 * 接收指令
	 ***********************************/

	public int getCmdTimeOut() {
		return cmdTimeOut;
	}

	public void setCmdTimeOut(int cmdTimeOut) {
		this.cmdTimeOut = cmdTimeOut;
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public boolean isConnecting() {
		return isConnecting;
	}

	public BTConnectListener getConnectListener() {
		return mConnectListener;
	}

	public void setConnectListener(BTConnectListener connectListener) {
		this.mConnectListener = connectListener;
	}

	public BTReceiveListener getReceiveListener() {
		return mReceiveListener;
	}

	public void setReceiveListener(BTReceiveListener receiveListener) {
		this.mReceiveListener = receiveListener;
	}

	public BTDiscoveryListener getDiscoveryListener() {
		return mDiscoveryListener;
	}

	public void setDiscoveryListener(BTDiscoveryListener discoveryListener) {
		this.mDiscoveryListener = discoveryListener;
	}

	public BluetoothIBridgeAdapter getBTAdapter() {
		return mBTAdapter;
	}

	public BluetoothIBridgeDevice getCurrentBTDevice() {
		return mCurrentBTDevice;
	}
}
