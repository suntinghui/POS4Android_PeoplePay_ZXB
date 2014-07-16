package com.zxb.client;


public class Constants {

	// 当前系统的版本号
	public static final int VERSION = 1;

	public static final String AESKEY = "dynamicode";

	public static final String APPFILEPATH = "/data/data/"
			+ ApplicationEnvironment.getInstance().getApplication()
					.getPackageName();

	// assets下的文件保存路径
	public static final String ASSETSPATH = APPFILEPATH + "/assets/";
	
	public static final String kUSERNAME = "kUSERNAME";
	public static final String kPASSWORD = "kPASSWORD";

	public static final String DOWNLOADURL = "http://59.49.20.154:8586/zfb/mpos/transProcess.do?operationId=getVersion";
	// public static final String DOWNLOADURL =
	// "http://192.168.1.46:8080/zfb/mpos/transProcess.do?operationId=getVersion";

	public static final int OVERTIME = 20;// 超时时间

	// 上次签到日期 MMdd。一天签到一次
	public static String kPRESIGNDATE = "kPRESIGNDATE";

	public static final String kLOCKKEY = "LockKey";
	public static final String kFIRSTLOGIN = "FirstLogin";
	public static final String kGESTRUECLOSE = "GestureClose";

	public static final String ACTION_ZXB_STARTSWIP = "ACTION_ZXB_STARTSWIP";
	public static final String ACTION_ZXB_SUCCESS = "ACTION_ZXB_SUCCESS";
	public static final String ACTION_ZXB_SWIPFINISHED = "ACTION_ZXB_SWIPFINISHED";

	public static final String ip = "http://211.147.87.20:8092/Vpm/";
	
	public static final String SIGNIMAGESPATH = APPFILEPATH + "/signImages/";
	public static final String kTRACEAUDITNUM = "kTRACEAUDITNUM";

	public static final String kPAGESIZE = "5";
	public static final String NEWAPP = "newApp";

	// BPUSH
	public static final String kBPUSH_APPID = "appid";
	public static final String kBPUSH_USERID = "userid";
	public static final String kBPUSH_CHANNELID = "channelid";

	public static String APPTOKEN = "";
}
