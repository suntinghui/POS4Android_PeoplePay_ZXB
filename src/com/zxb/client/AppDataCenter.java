package com.zxb.client;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppDataCenter {

	/* 掌芯宝 */

	// 设备信息
	public static int[] Adc = new int[2];
	public static String[] DevTypes = new String[1];
	public static byte[] deviceId = new byte[8];
	public static byte[] version = new byte[9];
	// 相关值
	public static String[] PIN_Block = new String[1]; // 密码
	public static String[] MAC = new String[1]; // mac
	public static byte[] encryptFactor = new byte[8]; // 存储服务器下发的分散因子 ？？？
	public static byte[] encTrack = new byte[] {}; // 磁道密文
	public static String cardNo = "";

	// 取系统追踪号，6个字节数字定长域
	public static String getTraceAuditNum() {
		SharedPreferences preferences = ApplicationEnvironment.getInstance().getPreferences();

		int number = preferences.getInt(Constants.kTRACEAUDITNUM, 1);

		Editor editor = preferences.edit();
		editor.putInt(Constants.kTRACEAUDITNUM, (number + 1) > 999999 ? 1 : (number + 1));
		editor.commit();

		String no = String.format("%06d", number);

		return no;
	}

}
