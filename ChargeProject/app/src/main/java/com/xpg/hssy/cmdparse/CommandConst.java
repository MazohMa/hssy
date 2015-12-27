package com.xpg.hssy.cmdparse;

public class CommandConst {
	public static final byte[] PackageHeader = { 0x48, 0x53, 0x53, 0x59 };
	public static final byte[] PackageTail = { 0x45 };
	public static final byte[] PackageIsolation = { 0x0A };

	// 命令字 CM 和 命令参数PM 的枚举
	public enum CMPMEnum {
		None((byte) 0x00, (byte) 0x00), //
		Authenticate((byte) 0x01, (byte) 0x01), // 绑定操作
		Update_KEY((byte) 0x01, (byte) 0x02), // 更新桩主KEY_UUID
		Update_WhiteList((byte) 0x01, (byte) 0x03), // 添加家人信息
		Acquire_Pole_NO((byte) 0x01, (byte) 0x04), // 查询充电桩编号（ID）
		Legality((byte) 0x01, (byte) 0x05), // 合法测试（ID）
		Charging_Start((byte) 0x02, (byte) 0x01), // 启动充电
		Charging_Stop((byte) 0x02, (byte) 0x02), // 停止充电
		Charging_Pause((byte) 0x02, (byte) 0x03), // 暂停充电
		Charging_Resume((byte) 0x02, (byte) 0x04), // 恢复充电
		Charging_Delay((byte) 0x02, (byte) 0x05), // 延时充电
		AdjustPWM_APort((byte) 0x03, (byte) 0x01), // 调节A口PWM
		AdjustPWM_BPort((byte) 0x03, (byte) 0x02), // 调节B口PWM
		State_Device((byte) 0x04, (byte) 0x01), // 读设备状态,包括工作状态、告警状态。
		State_Charging((byte) 0x04, (byte) 0x02), // 读实时充电参数,包括充电电压、电量。
		History_State((byte) 0x04, (byte) 0x03), // 读充电记录号
		History_Of_ID((byte) 0x04, (byte) 0x04), // 读充电记录
		Confirm_History((byte)0x04,(byte)0x08), //确认充电记录
		History_Clean_All((byte) 0x04, (byte) 0x05), // 清除所有记录
		Setting_Deivce_Time((byte) 0x05, (byte) 0x01), // 设置充电设备时间
		Acquire_Device_Time((byte) 0x05, (byte) 0x02), // 读取设备时间
		Setting_Adc((byte) 0x05, (byte) 0x03), // 设置Adc系数
		Setting_Alarm_Voltage((byte) 0x05, (byte) 0x04), // 设置告警电压
		Setting_Alarm_Currents((byte) 0x05, (byte) 0x05), // 设置告警电流
		Factory_Reset((byte) 0x06, (byte) 0x01), // 恢复到出厂状态
		Factory_Set_ID((byte) 0x06, (byte) 0x02), // 设置充电设备ID
		Error_Verify((byte) 0x99, (byte) 0x01), // 校验错误
		Error_Command((byte) 0x99, (byte) 0x02), // 命令错误
		Error_nonAuthentication((byte) 0x99, (byte) 0x03);// 未授权账户

		private final byte Value_CM;
		private final byte Value_PM;

		private CMPMEnum(byte value_CM, byte value_PM) {
			this.Value_CM = value_CM;
			this.Value_PM = value_PM;
		}

		public byte[] getValues() {
			byte[] values = new byte[2];
			values[0] = Value_CM;
			values[1] = Value_PM;
			return values;
		}

		public boolean isEnum(byte[] bytes) {
			if (bytes != null && bytes.length == 2 && bytes[0] == Value_CM
					&& bytes[1] == Value_PM) {
				return true;
			}
			return false;
		}
	}

	public enum KEYTypeEnum {
		None((byte) 0x00), //
		Owner((byte) 0x01), // 桩主
		Family((byte) 0x02), // 家人
		Order((byte) 0x03), // 一次性（订单）
		Reset((byte) 0x04), // 重置
		Factory((byte) 0x05),// 工厂KEY
		ConfirmHistory((byte)0x06);//历史

		private final byte keytype;

		private KEYTypeEnum(byte value_type) {
			this.keytype = value_type;
		}

		public byte getValue() {
			return keytype;
		}

		public boolean isEnum(byte bytes) {
			if (bytes == keytype) {
				return true;
			}
			return false;
		}
	}

	// public void test() {
	// CMPMEnum cmd = CMPMEnum.Authenticate;
	// byte[] values;
	// switch (cmd) {
	// case Authenticate:
	// //
	// values = cmd.getValues();
	// Log.i("CMDTEST", String.format("%02x,%02x", values[0],values[1]));
	// break;
	// case Charging_Start:
	// //
	// values = cmd.getValues();
	// Log.i("CMDTEST", String.format("%02x,%02x", values[0],values[1]));
	// break;
	// default:
	// break;
	// }
	// }
}
