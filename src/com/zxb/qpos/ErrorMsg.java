package com.zxb.qpos;

import java.util.Map;
import java.util.TreeMap;

import com.fncat.xswipe.controller.ErrorCode;

public class ErrorMsg {
	
	public static Map<Integer, String> errorMap = new TreeMap<Integer, String>();

	public static Map<Integer, String> getErrorMsg() {
		if (errorMap.size() == 0) {
			errorMap.put(ErrorCode.SUCCESS, "执行成功！");
			errorMap.put(ErrorCode.COMMAND_NOT_IMPLEMENTED, "命令未执行！");
			errorMap.put(ErrorCode.SECRET_KEY_IS, "密钥已经存在！");
			errorMap.put(ErrorCode.PARAMETER_IS_ERROR, "输入参数错误！");
			errorMap.put(ErrorCode.SECRET_KEY_NO, "密钥不存在！");
			errorMap.put(ErrorCode.ENCRYPT_FAIL, "加密失败！");
			errorMap.put(ErrorCode.NOT_SWIPE_CARD, "未刷卡！");
			errorMap.put(ErrorCode.SWIPE_CARD_FAIL, "刷卡失败！");
			errorMap.put(ErrorCode.DEVICE_IS_OPEN, "刷卡器已打开！");
			errorMap.put(ErrorCode.RECV_DATA_NOT_ENOUGH, "接受数据长度不够！");
			errorMap.put(ErrorCode.RECV_DATA_ERROR, "将接受数据错误，获取为null！");
			errorMap.put(ErrorCode.ERR_DEVICE_UNKOWN, "设备没初始化！");
			errorMap.put(ErrorCode.NOT_SWIPER_LISTENER, "未启动刷卡监听！");
			errorMap.put(ErrorCode.INPUT_PARAMS, "参数长度不够！");
			errorMap.put(ErrorCode.UNKNOW_ERROR, "未知错误！");
			errorMap.put(ErrorCode.SYSTEM_BUSY, "硬件正在工作中！");
			errorMap.put(ErrorCode.GET_DEVICE_INFO_FAIL, "获取设备信息失败！");
			errorMap.put(ErrorCode.CRC_ERROR, "CRC错误！");
			errorMap.put(ErrorCode.NOT_OPEN_DEVICE, "没有调用打开刷卡接口！");

			errorMap.put(ErrorCode.AudioManager_NULL, "音频初始化失败！");
			errorMap.put(ErrorCode.brand_NULL, "手机型号获取为空！");
			errorMap.put(ErrorCode.brand_nothing, "该手机型号没有适配！");
			errorMap.put(ErrorCode.powerDevice_ERROR, "设备上电/关闭异常！");
			errorMap.put(ErrorCode.Device_encrypt_NULL, "获取卡磁为null！");
			errorMap.put(ErrorCode.Device_encrypt_1, "轮询超过指定次数获取卡磁！");
			errorMap.put(ErrorCode.Device_Card_NULL, "获取卡号为null！");
			errorMap.put(ErrorCode.Common_Interrupt, "操作被中断！");
			errorMap.put(ErrorCode.Common_TIMEOUT, "操作超时！");

			errorMap.put(ErrorCode.DATA_INCOMPLETE, "数据接受结束后，接受的数据不全而导致错误");
			errorMap.put(ErrorCode.DATA_LOST, "在解析音频数据的时候，发现有数据无法解析而导致数据丢失");
			errorMap.put(ErrorCode.CAPTRUE_HEAD_FAILED, "一直捕获不到前导 尝试超过次数");
			errorMap.put(ErrorCode.READ_WAV_TIMEOUT, "READ_WAV_TIMEOUT");
			errorMap.put(ErrorCode.AUDIO_RECORD_NOT_INIT, "record没有被初始化");
			errorMap.put(ErrorCode.PARSE_TIMEOUT, "PARSE_TIMEOUT");
			errorMap.put(ErrorCode.AUDIO_RECORD_IS_STOP, "AUDIO_RECORD_IS_STOP");
			errorMap.put(ErrorCode.AUDIO_TRACK_NOT_INIT, "AUDIO_TRACK_NOT_INIT");
			errorMap.put(ErrorCode.INPUT_ERROR, "INPUT_ERROR");
			errorMap.put(ErrorCode.WRITE_EXCEPTION, "WRITE_EXCEPTION");
			errorMap.put(ErrorCode.CANNOT_PLAY_TRACK, "CANNOT_PLAY_TRACK");
			errorMap.put(ErrorCode.CRC_ERR, "CRC一直校验错误");
			errorMap.put(ErrorCode.DEVICE_RECV_ERR, "终端一直要求重发数据");
			errorMap.put(ErrorCode.MUST_NOT_CMD_NORMAL_REQUEST, "终端一直要求重发数据");
			errorMap.put(ErrorCode.Device_Verify_length_ERR, "终端返回校验长度不正确");
		}

		return errorMap;
	}
}
