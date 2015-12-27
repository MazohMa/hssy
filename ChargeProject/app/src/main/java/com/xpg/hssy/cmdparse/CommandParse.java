package com.xpg.hssy.cmdparse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.xpg.hssy.cmdparse.CommandConst.CMPMEnum;
import com.xpg.hssy.cmdparse.CommandConst.KEYTypeEnum;

public class CommandParse {

	public static final String CMDTYPE = "CMDTYPE";
	public static final String DEVICEID = "DEVICEID";
	public static final String EXEC_STATE = "CMDTYPE";
	private static final String TAG = "CommandParse";

	/*
	 * parse the response bytes into APPLICATION DATA NOTICE: suppose the bytes
	 * is a completed CMD response
	 */
	public static byte[] ParseCMDRspBytes(byte[] bytes) {
		// STX
		Log.i(TAG,
				"ParseCMDRspBytes got  bytes: "
						+ DataUtil.bytesToHexString(bytes));
		byte[] STXbyts = Arrays.copyOfRange(bytes, 0, 4);
		Log.i(TAG,
				"ParseCMDRspBytes got  STXbyts: "
						+ DataUtil.bytesToHexString(STXbyts));
		if (Arrays.equals(CommandConst.PackageHeader, STXbyts) == false) {
			// not start with official header???
			Log.i(TAG,
					"Error in STXbyts when ParseCMDRspBytes "
							+ DataUtil
									.bytesToHexString(CommandConst.PackageHeader)
							+ "<==>" + DataUtil.bytesToHexString(STXbyts));
			return null;
		}

		// length
		byte[] lengthBytes = new byte[2];
		lengthBytes[0] = bytes[4 + 0];
		lengthBytes[1] = bytes[4 + 1];
		long dataLength = toLong(lengthBytes);

		Log.i(TAG, "ParseCMDRspBytes got  dataLength: " + dataLength);
		if ((dataLength != bytes.length - 4 - 2 - 1 - 1)
				&& (dataLength != bytes.length - 4 - 2 - 1 - 1 - 1)) {
			// error in length ???
			Log.i(TAG,
					"Error in length when ParseCMDRspBytes: "
							+ DataUtil.bytesToHexString(lengthBytes) + " to "
							+ dataLength);
			return null;
		}

		// ETX
		int ETXstart = (int) (4 + 2 + dataLength);
		byte[] ETXBytes = Arrays.copyOfRange(bytes, ETXstart, ETXstart + 1);
		Log.i(TAG,
				"ParseCMDRspBytes got  ETXBytes: "
						+ DataUtil.bytesToHexString(ETXBytes));
		if (Arrays.equals(CommandConst.PackageTail, ETXBytes) == false) {
			// not end with official ending???
			Log.i(TAG,
					"Error in ETXBytes when ParseCMDRspBytes "
							+ DataUtil
									.bytesToHexString(CommandConst.PackageTail)
							+ " <> " + DataUtil.bytesToHexString(ETXBytes));
			return null;
		}

		// BCC
		int BCCstart = (int) (4 + 2 + dataLength + 1);
		byte oriBCCByte = bytes[BCCstart];
		byte[] dataToBCC = Arrays.copyOfRange(bytes, 0, BCCstart);
		byte calBCCByte = getBCC(dataToBCC);
		Log.i(TAG,
				"ParseCMDRspBytes got  dataToBCC: "
						+ DataUtil.bytesToHexString(dataToBCC));
		if (calBCCByte != oriBCCByte) {
			// BCC error !!
			Log.i(TAG,
					"Error in BCC when ParseCMDRspBytes: " + calBCCByte
							+ " <===> " + oriBCCByte + "["
							+ DataUtil.bytesToHexString(dataToBCC) + "]");
			return null;
		}

		// isolation byte
		int isolationstart = (int) (4 + 2 + dataLength + 1 + 1);
		byte[] isolationBytes;
		if (isolationstart == bytes.length - 1) {
			isolationBytes = Arrays.copyOfRange(bytes, isolationstart,
					isolationstart + 1);
			if (Arrays.equals(isolationBytes, CommandConst.PackageIsolation)) {
				// TODO: cache data if isolationBytes exist
				Log.i(TAG, "Have Isolation byte when ParseCMDRspBytes ");
			} else {
				Log.i(TAG,
						"Error in Isolation byte parsing when ParseCMDRspBytes ");
				return null;
			}
		} else {
			// not isolation byte
			Log.i(TAG, "No Isolation byte when ParseCMDRspBytes ");
		}

		// cmd data
		int dataStart = 4 + 2;
		byte[] cmdData = Arrays.copyOfRange(bytes, dataStart,
				(int) (dataStart + dataLength));
		byte[] cmdHeaderBytes = Arrays.copyOfRange(cmdData, 0, 2);
		CMPMEnum cmdTypEnum = getCMDByBytes(cmdHeaderBytes);
		if (cmdTypEnum == CMPMEnum.None) {
			// Error with data header
			Log.i(TAG, "Error with data header when ParseCMDRspBytes ");
			return null;
		}

		// TODO: continue parse the datas

		return cmdData;
	}

	/**
	 * 鉴权操作 检查KEY是否可用，同时用于第一次绑定桩的桩主ID
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateAuthenticateCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Authenticate,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * （桩主）更新KEY
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateUpdateKeyCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Update_KEY,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 写入白名单
	 *
	 * @param keyType
	 * @param ownerKey
	 * @param keyList
	 * @return
	 */
	public static byte[] CreateUpdateWhiteListCMD(byte keyType,
			byte[] ownerKey, ArrayList<byte[]> keyList) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Update_WhiteList,
				keyType, ownerKey);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, (byte) keyList.size());
		retCmdBytes = DataUtil.byteMerger(retCmdBytes,
				DataUtil.byteMerger(keyList));
		return pack(retCmdBytes);
	}

	/**
	 * 获取充电桩编码
	 *
	 * @return
	 */
	public static byte[] CreateAquirePoleNOCMD() {
		// byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Acquire_Pole_NO,
		// keyType, key);
		byte[] retCmdBytes = CMPMEnum.Acquire_Pole_NO.getValues();
		return pack(retCmdBytes);
	}

	/**
	 * KEY合法性测试
	 *
	 * @return
	 */
	public static byte[] CreateCheckLegalityCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Legality, keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 开始充电
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateStartChargeCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Charging_Start,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 停止充电
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateStopChargeCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Charging_Stop,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 暂停充电
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreatePauseChargeCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Charging_Pause,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 恢复充电
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateResumeChargeCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Charging_Resume,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 延时充电
	 *
	 * @param keyType
	 * @param key
	 * @param secondsTo1970
	 * @return
	 */
	public static byte[] CreateDelayChargeCMD(byte keyType, byte[] key,
			Date date) {
		int secondsTo1970 = (int) (date.getTime() / 1000);
		if (secondsTo1970 > Integer.MAX_VALUE || secondsTo1970 < 0) {
			Log.i(TAG, "Wrong Time？？ " + date.toString());
		}
		Log.i(TAG, "CreateDelayChargeCMD sec: " + secondsTo1970);
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Charging_Delay,
				keyType, key);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes,
				DataUtil.getBytes(secondsTo1970, false));
		return pack(retCmdBytes);
	}

	/**
	 * 调节A口PWM
	 *
	 * @param keyType
	 * @param key
	 * @param percent
	 * @return
	 */
	public static byte[] CreateAdjustPWMACMDBytes(byte keyType, byte[] key,
			byte percent) {
		if (percent < 0 || percent > 100) {
			Log.e(TAG, "pwm percent over range!");
			return null;
		}
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.AdjustPWM_APort,
				keyType, key);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, percent);
		return pack(retCmdBytes);
	}

	/**
	 * 调节B口PWM
	 *
	 * @param keyType
	 * @param key
	 * @param percent
	 * @return
	 */
	public static byte[] CreateAdjustPWMBCMDBytes(byte keyType, byte[] key,
			byte percent) {
		if (percent < 0 || percent > 100) {
			Log.e(TAG, "over range!");
			return null;
		}
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.AdjustPWM_BPort,
				keyType, key);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, percent);
		return pack(retCmdBytes);
	}

	/**
	 * 读取设备状态
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateAcqDeviceStateCMDBytes(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.State_Device,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 读取实时充电参数
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateAcqDeviceChargingStateCMDBytes(byte keyType,
			byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.State_Charging,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 读取充电记录存储信息
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateAcqHistoryStateCMDBytes(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.History_State,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 读取充电记录
	 *
	 * @param keyType
	 * @param key
	 * @param historyID
	 * @return
	 */
	public static byte[] CreateAcqHistoryByIDCMD(byte keyType, byte[] key,
			int historyID) {
		if (historyID < 0 || historyID > 65535) {
			Log.e(TAG, "history id over range!");
			return null;
		}
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.History_Of_ID,
				keyType, key);
		short id = (short) (historyID & 0x0000ffff);
		byte[] historyIDBytes = DataUtil.getBytes(id, false);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, historyIDBytes);
		return pack(retCmdBytes);

	}

	/**
	 * 确认充电记录
	 * @param keyType
	 * @param keyBytes
	 * @return
	 */
	public static byte[] CreateConfirmHistoryByKeyCMD(byte[] keyBytes) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Confirm_History, KEYTypeEnum.ConfirmHistory.getValue(), keyBytes);
		return pack(retCmdBytes);
	}

	/**
	 * 清空充电记录存储信息
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateCleanAllHistoryCMDBytes(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.History_Clean_All,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 设置充电桩时间
	 *
	 * @param keyType
	 * @param key
	 * @param time
	 * @author mazoh
	 * @return byte[]
	 */
	public static byte[] CreateSetTimeCMDBytes(byte keyType, byte[] key,
			Date date) {
		//date转为int类型，getByte转byte数组
		int secs = (int) (date.getTime() / 1000);
		if (secs > Integer.MAX_VALUE || secs < 0) {
			Log.i(TAG, "Wrong Time？？ " + date.toString());
		}
		Log.i(TAG, "CreateSetTimeCMDBytes sec: " + secs);
		byte[] retCmdBytes = combineCMDEnumWithKey(
				CMPMEnum.Setting_Deivce_Time, keyType, key);
		byte[] timeBytes = DataUtil.getBytes(secs, false);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, timeBytes);
		return pack(retCmdBytes);
	}

	/**
	 * 读取充电桩时间
	 *
	 * @param keyType
	 * @param key
	 * @author mazoh
	 * @return byte[]
	 */
	public static byte[] CreateAcqTimeCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(
				CMPMEnum.Acquire_Device_Time, keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 恢复到出厂状态
	 *
	 * @param keyType
	 * @param key
	 * @return
	 */
	public static byte[] CreateFactoryResetCMD(byte keyType, byte[] key) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Factory_Reset,
				keyType, key);
		return pack(retCmdBytes);
	}

	/**
	 * 写充电桩ID
	 *
	 * @param keyType
	 * @param key
	 * @param id
	 * @return
	 */
	public static byte[] CreateFactorySetPoleNO(byte keyType, byte[] key,
			byte[] idBytes) {
		byte[] retCmdBytes = combineCMDEnumWithKey(CMPMEnum.Factory_Set_ID,
				keyType, key);
		// byte[] idBytes = DataUtil.getBytes(id, false);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, idBytes);
		return pack(retCmdBytes);
	}

	public static byte[] CreateCMDBytes(CMPMEnum cmdType, String userID,
			String... args) {
		byte[] retCmdBytes = cmdType.getValues();
		byte[] userIDBytes = parseBCDIDStrToBytes(userID);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, userIDBytes);

		if (args != null && args.length >= 1) {
			if (cmdType == CMPMEnum.Authenticate) {
				byte[] verifyCodeBytes = parseBCDIDStrToBytes(args[0]);
				retCmdBytes = DataUtil.byteMerger(retCmdBytes, verifyCodeBytes);
			} else {
				byte[] deviceIDBytes = parseBCDIDStrToBytes(args[0]);
				retCmdBytes = DataUtil.byteMerger(retCmdBytes, deviceIDBytes);
			}
		} else {
			// error
		}

		switch (cmdType) {
		case Authenticate:
			break;
		case Charging_Start:
			break;
		case Charging_Stop:
			break;
		case Charging_Pause:
			break;
		case Charging_Resume:
			break;
		case AdjustPWM_APort:
			break;
		case AdjustPWM_BPort:
			break;
		case State_Device:
			break;
		case State_Charging:
			break;

		// TODO: add more parameters

		case History_Of_ID:
			break;
		case History_State:
			break;
		case Setting_Deivce_Time:
			break;
		case Acquire_Device_Time:
			if (args.length < 2) {
				Log.e(TAG, "set devie id fail, new id is null!");
				return null;
			}
			byte[] deviceNewIDBytes = parseBCDIDStrToBytes(args[1]);
			retCmdBytes = DataUtil.byteMerger(retCmdBytes, deviceNewIDBytes);
			break;
		case Setting_Adc:
			break;
		case Setting_Alarm_Voltage:
			break;
		case Setting_Alarm_Currents:
			break;
		// case Error_Verify:
		// break;
		// case Error_Command:
		// break;
		// case Error_nonAuthentication:
		// break;

		default:
			break;
		}

		return pack(retCmdBytes);
	}

	public static byte[] combineCMDEnumWithKey(CMPMEnum cmd_Enum, byte keyType,
			byte[] key) {
		byte[] retCmdBytes = cmd_Enum.getValues();
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, keyType);
		retCmdBytes = DataUtil.byteMerger(retCmdBytes,
				DataUtil.revertBytes(key));
		return retCmdBytes;
	}

	/**
	 * 打包包头包尾，添加长度信息，添加BCC校验，添加隔离字节
	 *
	 * @param retCmdBytes
	 * @return
	 */
	public static byte[] pack(byte[] retCmdBytes) {
		// add length
		int dataLength = retCmdBytes.length;
		byte[] lengthByte = DataUtil.getBytes((short) dataLength, false);
		retCmdBytes = DataUtil.byteMerger(lengthByte, retCmdBytes);

		// add header
		retCmdBytes = DataUtil.byteMerger(CommandConst.PackageHeader,
				retCmdBytes);

		// add tail
		retCmdBytes = DataUtil
				.byteMerger(retCmdBytes, CommandConst.PackageTail);

		// add BCC
		byte[] bccResult = { getBCC(retCmdBytes) };
		retCmdBytes = DataUtil.byteMerger(retCmdBytes, bccResult);

		// add isolation byte
		retCmdBytes = DataUtil.byteMerger(retCmdBytes,
				CommandConst.PackageIsolation);
		Log.i(TAG,
				"Created CMD bytes: "
						+ DataUtil.getStringFromBytes(retCmdBytes));
		return retCmdBytes;
	}

	// static method
	public static CMPMEnum getCMDByBytes(byte[] bytes) {
		for (CMPMEnum cmd : CMPMEnum.values()) {
			if (cmd.isEnum(bytes)) {
				return cmd;
			}
		}
		return CMPMEnum.None;
	}

	// 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
	public static long toLong(byte[] bRefArr) {
		long iOutcome = 0;
		byte bLoop;

		for (int i = 0; i < bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	public static byte getBCC(byte[] data) {
		byte[] BCC = new byte[1];
		for (int i = 0; i < data.length; i++) {
			BCC[0] = (byte) (BCC[0] ^ data[i]);
		}
		return BCC[0];
	}

	// get ID from 8421BCD bytes (8421 2421 余3 ？？？)
	public static String getBCDIDStrFromBytes(byte[] bytes) {
		StringBuffer idBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			idBuffer.append((b >> 4) & 0x0F);
			idBuffer.append(b & 0xF0);
		}
		return idBuffer.toString();
	}

	// parse ID String to 8421BCD bytes
	public static byte[] parseBCDIDStrToBytes(String id) {
		id = id.toLowerCase();
		byte[] retBytes = new byte[id.length() / 2];
		for (int i = 0; i < id.length(); i++) {
			char idNumChar = id.charAt(i);
			int idNum;
			if (idNumChar >= '0' && idNumChar <= '9') {
				idNum = idNumChar - '0';
			} else if (idNumChar >= 'a' && idNumChar <= 'f') {
				idNum = idNumChar - 'a' + 0x0a;
			} else {
				return null;
			}
			int byteIndex = i / 2;
			if (i % 2 == 0) {
				retBytes[byteIndex] = (byte) (idNum << 4);
			} else {
				retBytes[byteIndex] = (byte) (retBytes[byteIndex] | (idNum & 0x0F));
			}
		}
		return retBytes;
	}

	// ====================================== TEST
	// =================================================//
	// public static void testSendCMD() {
	// for (CMPMEnum enumType : CMPMEnum.values()) {
	// CreateCMDBytes(enumType, "9192939495969798", "818283848586878F");
	// // CreateCMDBytes(enumType, "9192939495969798", "8182838485868788");
	// }
	// }

	public static void testParseRespCMD() {
		// byte[] respCMD =
		// DataUtil.hexStringToBytes("48535359120001010123456789876543345678987654321045ec");
		byte[] respCMD = DataUtil
				.hexStringToBytes("485353590B00020234567898765432100145DC0A");
		ParseCMDRspBytes(respCMD);
	}

	public static void testAllCMD() {
		// owner Key
		byte[] poleIDBytes = DataUtil.hexStringToBytes("F000000000000000");
		byte[] ownerIDBytes = DataUtil.hexStringToBytes("0000000000000001");
		byte[] uuidBytes = DataUtil.hexStringToBytes("1234000000000000");
		byte[] key_owner = DataUtil.byteMerger(poleIDBytes, ownerIDBytes);
		key_owner = DataUtil.byteMerger(key_owner, uuidBytes);
		key_owner = DataUtil.revertBytes(key_owner);

		Log.i(TAG, "Owner KEY :" + DataUtil.getStringFromBytes(key_owner));

		// owner Key
		byte[] newuuidBytes = DataUtil.hexStringToBytes("1234000000000000");
		byte[] key_reset = DataUtil.byteMerger(poleIDBytes, ownerIDBytes);
		key_reset = DataUtil.byteMerger(key_reset, newuuidBytes);
		key_reset = DataUtil.revertBytes(key_reset);

		Log.i(TAG, "Reset KEY :" + DataUtil.getStringFromBytes(key_reset));

		// factory Key
		byte[] f_poleIDBytes = DataUtil.hexStringToBytes("0000000000000000");
		byte[] f_IDBytes = DataUtil.hexStringToBytes("0000000000000000");
		byte[] f_uuidBytes = DataUtil.hexStringToBytes("0000000000000000");
		byte[] key_fatory = DataUtil.byteMerger(f_poleIDBytes, f_IDBytes);
		key_fatory = DataUtil.byteMerger(key_fatory, f_uuidBytes);
		key_fatory = DataUtil.revertBytes(key_fatory);

		Log.i(TAG, "Factory KEY :" + DataUtil.getStringFromBytes(key_fatory));

		for (CMPMEnum enumType : CMPMEnum.values()) {
			Log.i(TAG, "Creating the cmd of command: " + enumType.name());
			switch (enumType) {
			case Authenticate:
				CreateAuthenticateCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Update_KEY:
				CreateUpdateKeyCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Update_WhiteList:
				// CreateUpdateWhiteListCMD(KEYTypeEnum.Owner.getValue(),
				// key_owner);
				break;
			case Acquire_Pole_NO:
				CreateAquirePoleNOCMD();
				break;
			case Charging_Start:
				CreateStartChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Charging_Stop:
				CreateStopChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Charging_Pause:
				CreatePauseChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Charging_Resume:
				CreateResumeChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Charging_Delay:
				Date date = new Date();
				CreateDelayChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner, date);
				break;
			case AdjustPWM_APort:
				byte percent_a = 100;
				CreateAdjustPWMACMDBytes(KEYTypeEnum.Owner.getValue(), key_owner, percent_a);
				break;
			case AdjustPWM_BPort:
				byte percent_b = 100;
				CreateAdjustPWMBCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner, percent_b);
				break;
			case State_Device:
				CreateAcqDeviceStateCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case State_Charging:
				CreateAcqDeviceChargingStateCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case History_State:
				CreateAcqHistoryStateCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case History_Of_ID:
				int historyID = 0;
				CreateAcqHistoryByIDCMD(KEYTypeEnum.Owner.getValue(), key_owner, historyID);
				break;
			case Setting_Deivce_Time:
				Date nowDate = new Date();
				CreateSetTimeCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner,
						nowDate);
				break;
			case Acquire_Device_Time:
				CreateAcqTimeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
				break;
			case Factory_Reset:
				CreateFactoryResetCMD(KEYTypeEnum.Factory.getValue(),
						key_fatory);
				break;
			case Factory_Set_ID:
				CreateFactorySetPoleNO(KEYTypeEnum.Factory.getValue(),
						key_fatory, poleIDBytes);
				break;

			default:
				break;
			}
		}
	}

	public static void testCMDbyEnum(CMPMEnum theCMDEnum) {
		// owner Key
		byte[] poleIDBytes = DataUtil.hexStringToBytes("F000000000000000");
		byte[] ownerIDBytes = DataUtil.hexStringToBytes("0000000000000001");
		byte[] uuidBytes = DataUtil.hexStringToBytes("1234000000000000");
		byte[] key_owner = DataUtil.byteMerger(poleIDBytes, ownerIDBytes);
		key_owner = DataUtil.byteMerger(key_owner, uuidBytes);
		key_owner = DataUtil.revertBytes(key_owner);

		Log.i(TAG, "Owner KEY :" + DataUtil.getStringFromBytes(key_owner));

		// owner Key
		byte[] newuuidBytes = DataUtil.hexStringToBytes("1234000000000000");
		byte[] key_reset = DataUtil.byteMerger(poleIDBytes, ownerIDBytes);
		key_reset = DataUtil.byteMerger(key_reset, newuuidBytes);
		key_reset = DataUtil.revertBytes(key_reset);

		Log.i(TAG, "Reset KEY :" + DataUtil.getStringFromBytes(key_reset));

		// factory Key
		byte[] f_poleIDBytes = DataUtil.hexStringToBytes("0000000000000000");
		byte[] f_IDBytes = DataUtil.hexStringToBytes("0000000000000000");
		byte[] f_uuidBytes = DataUtil.hexStringToBytes("0000000000000000");
		byte[] key_fatory = DataUtil.byteMerger(f_poleIDBytes, f_IDBytes);
		key_fatory = DataUtil.byteMerger(key_fatory, f_uuidBytes);
		key_fatory = DataUtil.revertBytes(key_fatory);

		Log.i(TAG, "Factory KEY :" + DataUtil.getStringFromBytes(key_fatory));

		key_owner = getDefaultOwnerKey();
		key_owner = DataUtil.revertBytes(key_owner);
		key_fatory = getDefaultFactoryKey();
		key_fatory = DataUtil.revertBytes(key_fatory);
		poleIDBytes = getDefaultPoleNO();

		byte[] bytes;
		switch (theCMDEnum) {
		case Authenticate:
			CreateAuthenticateCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Update_KEY:
			CreateUpdateKeyCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Update_WhiteList:
			// CreateUpdateWhiteListCMD(KEYTypeEnum.Owner.getValue(),
			// key_owner);
			break;
		case Acquire_Pole_NO:
			CreateAquirePoleNOCMD();
			break;
		case Charging_Start:
			CreateStartChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Charging_Stop:
			CreateStopChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Charging_Pause:
			CreatePauseChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Charging_Resume:
			CreateResumeChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Charging_Delay:
			Date date = new Date();
			CreateDelayChargeCMD(KEYTypeEnum.Owner.getValue(), key_owner, date);
			break;
		case AdjustPWM_APort:
			byte percent_a = 100;
			CreateAdjustPWMACMDBytes(KEYTypeEnum.Owner.getValue(), key_owner,
					percent_a);
			break;
		case AdjustPWM_BPort:
			byte percent_b = 100;
			CreateAdjustPWMBCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner,
					percent_b);
			break;
		case State_Device:
			CreateAcqDeviceStateCMDBytes(KEYTypeEnum.Owner.getValue(),
					key_owner);
			break;
		case State_Charging:
			CreateAcqDeviceChargingStateCMDBytes(KEYTypeEnum.Owner.getValue(),
					key_owner);
			break;
		case History_State:
			CreateAcqHistoryStateCMDBytes(KEYTypeEnum.Owner.getValue(),
					key_owner);
			break;
		case History_Of_ID:
			int historyID = 0;
			CreateAcqHistoryByIDCMD(KEYTypeEnum.Owner.getValue(), key_owner,
					historyID);
			break;
		case Setting_Deivce_Time:
			Date nowDate = new Date();
			CreateSetTimeCMDBytes(KEYTypeEnum.Owner.getValue(), key_owner,
					nowDate);
			break;
		case Acquire_Device_Time:
			CreateAcqTimeCMD(KEYTypeEnum.Owner.getValue(), key_owner);
			break;
		case Factory_Reset:
			CreateFactoryResetCMD(KEYTypeEnum.Factory.getValue(), key_fatory);
			break;
		case Factory_Set_ID:
			CreateFactorySetPoleNO(KEYTypeEnum.Factory.getValue(), key_fatory,
					poleIDBytes);
			break;

		default:
			break;
		}
	}

	private static byte[] DefaultPole_NO = new byte[] { (byte) 0x50,
			(byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,
			(byte) 0x30, (byte) 0x33 };
	private static byte[] DefaultOrder_ID = new byte[] { (byte) 0x4F,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x01 };
	private static byte[] DefaultOrderer_ID = new byte[] { (byte) 0x4F,
			(byte) 0x44, (byte) 0x45, (byte) 0x52, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x01 };

	private static byte[] DefaultFactory_ID = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00 };
	private static byte[] DefaultOwner_ID = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x03 };
	public static List<byte[]> defaultFamilyIDList = new ArrayList<byte[]>();
	public static List<byte[]> defaultFamilyUUIDList = new ArrayList<byte[]>();

	private static byte[] DefaultFactory_UUID = new byte[] { (byte) 0x00,
			(byte) 0x00 };
	private static byte[] DefaultOwner_UUID = new byte[] { (byte) 0x12,
			(byte) 0x34 };
	private static byte[] DefaultKEYUUID_APPEND = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

	public static void resetDefaultKeys() {
		DefaultPole_NO = new byte[] { (byte) 0x50, (byte) 0x30, (byte) 0x30,
				(byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x33 };

		DefaultFactory_ID = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		DefaultOwner_ID = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03 };

		resetFamilyIDList();
		resetFamilyUUIDList();

		DefaultFactory_UUID = new byte[] { (byte) 0x00, (byte) 0x00 };
		DefaultOwner_UUID = new byte[] { (byte) 0x12, (byte) 0x34 };
		DefaultKEYUUID_APPEND = new byte[] { (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

	}

	public static void resetFamilyIDList() {
		defaultFamilyIDList.add(new byte[]{(byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01});
		defaultFamilyIDList.add(new byte[] { (byte) 0x10, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x02 });
		defaultFamilyIDList.add(new byte[] { (byte) 0x10, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x03 });
		defaultFamilyIDList.add(new byte[] { (byte) 0x10, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x04 });
	}

	public static void resetFamilyUUIDList() {
		defaultFamilyUUIDList.add(new byte[] { (byte) 0x12, (byte) 0x34 });
		defaultFamilyUUIDList.add(new byte[] { (byte) 0x12, (byte) 0x34 });
		defaultFamilyUUIDList.add(new byte[] { (byte) 0x12, (byte) 0x34 });
		defaultFamilyUUIDList.add(new byte[] { (byte) 0x12, (byte) 0x34 });
	}

	public static byte[] getDefaultFactoryKey() {
		byte[] key = DefaultPole_NO.clone();
		key = DataUtil.byteMerger(key, DefaultFactory_ID);
		key = DataUtil.byteMerger(key, DefaultFactory_UUID);
		key = DataUtil.byteMerger(key, DefaultKEYUUID_APPEND);
		key = DataUtil.revertBytes(key);
		return key;

	}

	public static boolean setDefaultFactoryKey() {
		return false;
	}

	// Owner Key
	public static byte[] getDefaultOwnerKey() {
		byte[] key = DefaultPole_NO.clone();
		key = DataUtil.byteMerger(key, DefaultOwner_ID);
		key = DataUtil.byteMerger(key, DefaultOwner_UUID);
		key = DataUtil.byteMerger(key, DefaultKEYUUID_APPEND);
		return key;

	}

	// Owner Key
	public static boolean setDefaultOwnerKey() {
		return false;
	}

	// Owner Key: 获取桩主ID
	public static byte[] getDefaultOwnerID() {
		return DefaultOwner_ID.clone();
	}

	// Owner Key: 设置桩主ID
	public static boolean setDefaultOwnerID(byte[] idBs) {
		if (idBs == null || idBs.length != 8) {
			return false;
		}
		DefaultOwner_ID = idBs.clone();
		return true;
	}

	// Owner Key: 获取桩主UUID
	public static byte[] getDefaultOwnerUUID() {
		return DefaultOwner_UUID;
	}

	// Owner Key: 设置桩主UUID
	public static boolean setDefaultOwnerUUID(byte[] uuidBs) {
		if (uuidBs == null || uuidBs.length != 2) {
			return false;
		}
		DefaultOwner_UUID = uuidBs.clone();
		return true;
	}

	// Family keys: 获取家人KEY
	public static byte[] getDefaultFKeyByIndex(int index) {
		if (defaultFamilyIDList.size() != 4) {
			resetFamilyIDList();
		}
		byte[] key = DefaultPole_NO.clone();
		key = DataUtil.byteMerger(key, getDefaultFIDByIndex(index));
		key = DataUtil.byteMerger(key, getDefaultFUUIDByIndex(index));
		key = DataUtil.byteMerger(key, DefaultKEYUUID_APPEND);
		return key;

	}

	public static boolean setDefaultFKeyByOrder(int order) {
		return false;
	}

	// Family keys： 获取家人KEY的ID
	public static byte[] getDefaultFIDByIndex(int index) {
		if (defaultFamilyIDList.size() != 4) {
			resetFamilyIDList();
		}
		if (index > 3 || index < 0) {
			return null;
		}
		return defaultFamilyIDList.get(index);
	}

	// Family Key: 设置家人UUID
	public static boolean setDefaultFID(byte[] idBs, int index) {
		if (idBs == null || idBs.length != 8) {
			return false;
		}
		defaultFamilyIDList.set(index, idBs.clone());
		return true;
	}

	// Family keys： 获取家人KEY的UUID
	public static byte[] getDefaultFUUIDByIndex(int index) {
		if (defaultFamilyUUIDList.size() != 4) {
			resetFamilyUUIDList();
		}
		if (index > 3 || index < 0) {
			return null;
		}
		return defaultFamilyUUIDList.get(index);
	}

	// Family Key: 设置家人UUID
	public static boolean setDefaultFUUID(byte[] uuidBs, int index) {
		if (uuidBs == null || uuidBs.length != 2) {
			return false;
		}
		defaultFamilyUUIDList.set(index, uuidBs.clone());
		return true;
	}

	// Order Key
	public static byte[] getDefaultOrdererKey() {
		byte[] key = DefaultPole_NO.clone();
		key = DataUtil.byteMerger(key, DefaultOrder_ID);
		key = DataUtil.byteMerger(key, DefaultOrderer_ID);
		key = DataUtil.byteMerger(key, new byte[] { (byte) 0x00, (byte) 0xD3,
				(byte) 0x6C, (byte) 0x38, // 2000.01.01 00:00:00
				(byte) 0x80, (byte) 0x70, (byte) 0x0B, (byte) 0x5E }); // 2020.01.01
																		// 00:00:00
		return key;

	}

	public static boolean setDefaultOrderKey() {
		return false;
	}

	// 桩编号
	public static byte[] getDefaultPoleNO() {
		byte[] key = DefaultPole_NO.clone();
		return key;

	}

	public static boolean setDefaultPoleNO(byte[] newPoleId) {
		boolean ret = false;
		if (newPoleId != null && newPoleId.length == 8) {
			DefaultPole_NO = newPoleId.clone();
			ret = true;
		}
		return ret;
	}

	// 订单ID
	public static byte[] getDefaultOrderID() {
		byte[] key = DefaultOrder_ID.clone();
		return key;

	}

	public static boolean setDefaultOrderID(byte[] newOwnerId) {
		boolean ret = false;
		if (newOwnerId != null && newOwnerId.length == 8) {
			DefaultOwner_ID = newOwnerId.clone();
			ret = true;
		}
		return ret;
	}
}
