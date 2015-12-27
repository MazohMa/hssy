package com.xpg.hssy.engine;

import android.util.Log;

import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.bt.BTReceiveListener;
import com.xpg.hssy.cmdparse.CommandConst.CMPMEnum;
import com.xpg.hssy.cmdparse.CommandParse;
import com.xpg.hssy.cmdparse.DataUtil;
import com.xpg.hssy.db.pojo.ChargeRecordCache;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.PileStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年1月23日
 */

public class CmdManager implements BTReceiveListener {
	private List<OnCmdListener> onCmdListeners = Collections.synchronizedList(new ArrayList<OnCmdListener>());
	private Key key;
	/**
	 * 当key鉴权通过或验证有效性通过时，此值为true
	 */
	private boolean isValid;

	private int HISTORY_DATA_LENGTH = 74;
	private int CONFIRM_HISTORY_DATA_LENGTH = 3;

	/**
	 * 获取桩id
	 */
	public void aquirePileId() {
		byte[] data = CommandParse.CreateAquirePoleNOCMD();
		BTManager.getInstance().send(data);
	}

	/**
	 * 主人鉴权
	 */
	public void authenticate() {
		byte[] data = CommandParse.CreateAuthenticateCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 同步家人列表
	 */
	public void updateWhiteList(List<Key> keys) {
		if (!isValid()) {
			return;
		}

		ArrayList<byte[]> keyBytes = new ArrayList<byte[]>();
		for (Key key : keys) {
			if (key.getKeyType() == Key.TYPE_FAMILY) {
				keyBytes.add(DataUtil.hexStringToBytes(key.getKey()));
			}
		}
		byte[] data = CommandParse.CreateUpdateWhiteListCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())),
				keyBytes);
		BTManager.getInstance().send(data);
	}

	/**
	 * 验证key的有效性
	 */
	public void checkLegality() {
		if (key == null) {
			return;
		}
		byte[] data = CommandParse.CreateCheckLegalityCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 获取设备状态
	 */
	public void acqDeviceState() {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateAcqDeviceStateCMDBytes(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 开始充电
	 */
	public void startCharge() {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateStartChargeCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 延迟充电
	 */
	public void delayCharge(Date date) {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateDelayChargeCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())), date);
		BTManager.getInstance().send(data);
	}

	/**
	 * 获取实时充电状态
	 */
	public void acqChargingState() {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateAcqDeviceChargingStateCMDBytes(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key
				.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 停止充电
	 */
	public void stopCharge() {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateStopChargeCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 获取最后一条充电记录
	 */
	public void acqHistoryLast() {
		acqHistoryById(0);
	}

	/**
	 * 获取充电记录
	 */
	public void acqHistoryById(int id) {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateAcqHistoryByIDCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())), id);
		BTManager.getInstance().send(data);
	}

	public void confirmHistoryByKey(byte[] keyBytes) {
		if (!isValid()) {
			return;
		}
		byte[] data = CommandParse.CreateConfirmHistoryByKeyCMD(DataUtil.revertBytes(keyBytes));
		BTManager.getInstance().send(data);
	}

	/**
	 * 获取充电记录状态
	 */
	public void acqHistoryState() {
		if (!isValid()) {
			return;
		}

		byte[] data = CommandParse.CreateAcqHistoryStateCMDBytes(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 获取当前桩的当前时间
	 */
	public void acqCurrentTimeInPile() {
		if (!isValid()) {
			return;
		}
		byte[] data = CommandParse.CreateAcqTimeCMD(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())));
		BTManager.getInstance().send(data);
	}

	/**
	 * 设置当前充电桩时间
	 */
	public void setTimeInPile(Date date) {
		if (!isValid()) {
			return;
		}
		byte[] data = CommandParse.CreateSetTimeCMDBytes(key.getKeyType().byteValue(), DataUtil.revertBytes(DataUtil.hexStringToBytes(key.getKey())), date);
		BTManager.getInstance().send(data);
	}

	/**
	 * 回调
	 *
	 * @param
	 */
	public static abstract class OnCmdListener {
		protected void onAquirePileId(String pileId) {
		}

		protected void onAquireCurrentTimeInPile(long date) {
		}

		protected void onSettingCurrentTimeInPile(boolean success) {
		}

		protected void onAuthenticate(boolean success) {
		}

		protected void onUpdateWhiteList(boolean success) {
		}

		protected void onCheckLegality(boolean success) {
		}

		protected void onAcqDeviceState(PileStatus status) {
		}

		protected void onStartCharge(boolean success) {
		}

		protected void onDelayCharge(boolean success) {
		}

		protected void onAcqChargingState(float voltage, float current, float amount, float power, int min) {
		}

		protected void onStopCharge(boolean success) {
		}

		protected void onAcqHistoryById(ChargeRecordCache record) {
		}

		protected void onAcqHistoryState(int currentIndex, int count, List<Integer> historyIds, int currentSerial) {
		}

		protected void onConfirmHistoryByKey(boolean success) {
		}

		protected void onTimeOut() {
		}

	}

	/**
	 * 接收数据
	 */
	@Override
	public void onReceive(byte[] data) {
		Log.i(CmdManager.class.getSimpleName(), "receive: " + DataUtil.bytesToHexString(data));
		if (onCmdListeners.isEmpty()) {
			return;
		}
		byte[] cmdByte = Arrays.copyOfRange(data, 0, 2);

		// 读取桩Id返回
		if (CMPMEnum.Acquire_Pole_NO.isEnum(cmdByte)) {
			Log.e("cmd", "receive: Acquire_Pole_NO");
			byte[] bytes_PoleNo = new byte[8];
			System.arraycopy(data, 2, bytes_PoleNo, 0, 8);
			String pileId = new String(bytes_PoleNo);
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAquirePileId(pileId);
			}
			return;
		}

		// 鉴权返回
		if (CMPMEnum.Authenticate.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			isValid = success;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAuthenticate(success);
			}
			return;
		}

		// 同步家人列表返回
		if (CMPMEnum.Update_WhiteList.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onUpdateWhiteList(success);
			}
			return;
		}

		// 验证key有效性返回
		if (CMPMEnum.Legality.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			isValid = success;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onCheckLegality(success);
			}
			return;
		}

		// 获取设备状态返回
		if (CMPMEnum.State_Device.isEnum(cmdByte)) {
			PileStatus status = new PileStatus();
			// 告警
			byte alarmByte = data[2];
			status.setAlarmByte(alarmByte);
			status.setVoltageOver(DataUtil.isHightBit(alarmByte, 0));
			status.setVoltageLower(DataUtil.isHightBit(alarmByte, 1));
			status.setCurrentOver(DataUtil.isHightBit(alarmByte, 2));
			status.setCurrentLeak(DataUtil.isHightBit(alarmByte, 3));
			status.setCurrentShort(DataUtil.isHightBit(alarmByte, 4));

			// 继电器
			byte chargeByte = data[3];
			status.setChargeStatus(chargeByte);
			// 状态字
			byte statusByte = data[4];
			status.setGunConnected(!DataUtil.isHightBit(statusByte, 0));
			status.setCarConnected(!DataUtil.isHightBit(statusByte, 1));
			status.setCarReady(!DataUtil.isHightBit(statusByte, 2));
			status.setCmdStatus((byte) (statusByte >> 3 & 0x01));
			// 定时状态
			byte delayByte = data[9];
			status.setDelayStatus(delayByte);
			// 定时时间
			long delayTime = DataUtil.getLong(new byte[]{data[10], data[11], data[12], data[13]}, true);
			status.setDelayTime(delayTime * 1000l);
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAcqDeviceState(status);
			}
			return;
		}

		// 开始充电返回
		if (CMPMEnum.Charging_Start.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onStartCharge(success);
			}
			return;
		}

		// 延迟充电返回
		if (CMPMEnum.Charging_Delay.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onDelayCharge(success);
			}
			return;
		}

		// 获取充电状态返回
		if (CMPMEnum.State_Charging.isEnum(cmdByte)) {
			int voltage = DataUtil.getInt(new byte[]{data[2], data[3]}, true);
			int current = DataUtil.getInt(new byte[]{data[4], data[5]}, true);
			int amount = DataUtil.getInt(new byte[]{data[6], data[7]}, true);
			int power = DataUtil.getInt(new byte[]{data[8], data[9]}, true);
			int minute = DataUtil.getInt(new byte[]{data[10], data[11]}, true);

			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAcqChargingState(voltage / 10f, current / 10f, amount / 100f, power / 100f, minute);
			}
			return;
		}

		// 设置桩当前时间
		if (CMPMEnum.Setting_Deivce_Time.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onSettingCurrentTimeInPile(success);
			}
			return;
		}

		// 获取桩当前时间
		if (CMPMEnum.Acquire_Device_Time.isEnum(cmdByte)) {
			long date = DataUtil.getLong(new byte[]{data[2], data[3], data[4], data[5]}, true);
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAquireCurrentTimeInPile(date);
			}
			return;
		}

		// 停止充电返回
		if (CMPMEnum.Charging_Stop.isEnum(cmdByte)) {
			boolean success = data[2] == 0x01;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onStopCharge(success);
			}
			return;
		}

		// 历史记录返回
		if (CMPMEnum.History_Of_ID.isEnum(cmdByte)) {
			ChargeRecordCache record = new ChargeRecordCache();
			// 记录交易流水号
			byte[] chargeSNBytes = new byte[4];
			System.arraycopy(data, 2, chargeSNBytes, 0, 4);
			chargeSNBytes = DataUtil.revertBytes(chargeSNBytes);
			int chareSN = DataUtil.getInt(chargeSNBytes, false);
			record.setSequence(chareSN);

			// 用户名
			byte[] userIDBytes = new byte[8];
			System.arraycopy(data, 6, userIDBytes, 0, 8);
			// userIDBytes = DataUtil.revertBytes(userIDBytes);
			long userID = DataUtil.getLongByBCD(userIDBytes);
			// String userIDStr = "" + userID;
			// String userId = new String(userIDBytes);
			record.setUserid(userID + "");

			// 订单号
			byte[] orderIdBytes = new byte[8];
			System.arraycopy(data, 14, orderIdBytes, 0, 8);
			long orderId = DataUtil.getLong(orderIdBytes, false);
			record.setOrderId(orderId + "");

			// 桩编号
			byte[] pileIdBytes = new byte[8];
			System.arraycopy(data, 22, pileIdBytes, 0, 8);
			String pileId = new String(pileIdBytes);
			record.setPileId(pileId);

			// 充电电量
			byte[] quantityBytes = new byte[4];
			System.arraycopy(data, 30, quantityBytes, 0, 4);
			quantityBytes = DataUtil.revertBytes(quantityBytes);
			long quantity = DataUtil.getInt(quantityBytes, false);
			record.setQuantity((float) quantity);

			// 起始时间
			byte[] startTimeBytes = new byte[4];
			System.arraycopy(data, 34, startTimeBytes, 0, 4);
			startTimeBytes = DataUtil.revertBytes(startTimeBytes);
			long secs = DataUtil.getLong(startTimeBytes, false);
			// Date theDate = new Date(secs * 1000l);
			// SimpleDateFormat format = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			// String startTimeStr = format.format(theDate);
			record.setStartTime(secs * 1000l);

			// 结束时间
			byte[] endTimeBytes = new byte[4];
			System.arraycopy(data, 38, endTimeBytes, 0, 4);
			endTimeBytes = DataUtil.revertBytes(endTimeBytes);
			secs = DataUtil.getLong(endTimeBytes, false);
			// theDate = new Date((long) secs * 1000);
			// format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String endTimeStr = format.format(theDate);
			record.setEndTime(secs * 1000l);

			byte[] startReason = new byte[1];
			System.arraycopy(data, 42, startReason, 0, 1);
			record.setStartReason(startReason[0]);
			byte[] stopReason = new byte[1];
			System.arraycopy(data, 43, stopReason, 0, 1);
			record.setStopReason(stopReason[0]);

			// 密文
			byte[] encodeData = new byte[42];
			System.arraycopy(data, 44, encodeData, 0, 42);
			record.setData(DataUtil.bytesToHexString(encodeData));
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAcqHistoryById(record);
			}
			return;
		}

		//确认历史记录返回
		if (CMPMEnum.Confirm_History.isEnum(cmdByte)) {
			if (data.length != CONFIRM_HISTORY_DATA_LENGTH) return;
			boolean success = data[2]==0x01;
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onConfirmHistoryByKey(success);
			}
		}

		// 历史记录状态返回
		if (CMPMEnum.History_State.isEnum(cmdByte)) {
			if (data.length != HISTORY_DATA_LENGTH) return;//不符合充电记录状态帧长度,丢弃
			int currentId = DataUtil.getInt(new byte[]{data[2], data[3]}, true);
			int count = DataUtil.getInt(new byte[]{data[4], data[5]}, true);
			int currentSerial = DataUtil.getInt(new byte[]{data[70], data[71], data[72], data[73]}, true);
			byte[] saveState = new byte[64];
			System.arraycopy(data, 6, saveState, 0, 64);
			List<Integer> unUploadHistoryIds = getUnUploadHistoryIdList(saveState);
			for (OnCmdListener onCmdListener : onCmdListeners) {
				onCmdListener.onAcqHistoryState(currentId, count, unUploadHistoryIds, currentSerial);
			}
			return;
		}
	}

	/**
	 * 指令超时
	 */
	@Override
	public void onTimeOut() {
		for (OnCmdListener onCmdListener : onCmdListeners) {
			onCmdListener.onTimeOut();
		}
	}

	/**
	 * 单例
	 */
	private volatile static CmdManager instance;

	private CmdManager() {
	}

	public static CmdManager getInstance() {
		if (instance == null) {
			synchronized (CmdManager.class) {
				if (instance == null) {
					instance = new CmdManager();
				}
			}
		}
		return instance;
	}

	public void init() {
		BTManager.getInstance().setReceiveListener(instance);
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
		this.isValid = false;
	}

	public void addOnCmdListener(OnCmdListener onCmdListener) {
		onCmdListeners.add(onCmdListener);
	}

	public void removeOnCmdListener(OnCmdListener onCmdListener) {
		onCmdListeners.remove(onCmdListener);
	}

	public void clearOnCmdListener(OnCmdListener onCmdListener) {
		onCmdListeners.clear();
	}

	// public OnCmdListener getOnCmdListener() {
	// return onCmdListener;
	// }
	//
	// public void setOnCmdListener(OnCmdListener onCmdListener) {
	// this.onCmdListener = onCmdListener;
	// }

	public boolean isValid() {
		if (!isValid) Log.e("CmdManager", "isValid: " + isValid);
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * 整个saveState数组,每个位都代表一个充电记录是否上传,第n个位为1,就代表第n+1条记录为未上传
	 *
	 * @param saveState
	 * @return
	 */
	private List<Integer> getUnUploadHistoryIdList(byte[] saveState) {
		List<Integer> historyIds = new ArrayList<>();
		for (int i = 0; i < saveState.length; i++) {
			byte stateByte = saveState[i];
			for (int j = 0; j < 8; j++) {
				if (((1 << j) & stateByte) > 0) {
					int historyId = j + i * 8 + 1;
					historyIds.add(historyId);
				}
			}
		}
		return historyIds;
	}
}
