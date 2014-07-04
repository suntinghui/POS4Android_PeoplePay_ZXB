package com.zxb.qpos;

import com.fncat.xswipe.controller.ErrorCode;
import com.zxb.client.AppDataCenter;
import com.zxb.util.ByteUtil;
import com.zxb.util.SecurityUtil;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 密码加密
 * 
 * @author Fncat
 * 
 */
public class ThreadEncPwd extends Thread {

	private static final byte[] PINKEY = {0x11, 0x11, 0x11, 0x11, 0x22, 0x22, 0x22, 0x22};

	private Handler mHandler;
	private Context mContext;
	private String pwd;

	public ThreadEncPwd(Handler mHandler, Context mContext, String pwd) {
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.pwd = pwd;
	}

	@Override
	public void run() {
		Log.e("***", "计算密码...");
		
		try {
			StringBuffer sb = new StringBuffer("");
			sb.append("0600");
			
			for (int i=0; i<pwd.length(); i++){
				sb.append("0").append(pwd.charAt(i));
			}
			
			int tempCount = 16-sb.length()/2;
			for (int i=0; i<tempCount; i++){
				sb.append("0F");
			}
			
			byte[] desResult = SecurityUtil.DESEncry(PINKEY, ByteUtil.hexStringToBytes(sb.toString()));
			
			int r = ZXBPOS.getPOSManage().GetPinBlock(desResult, AppDataCenter.PIN_Block);
			
			if (r == ErrorCode.SUCCESS) {
				Log.e("***", "计算密码成功...");

				Log.e("***", "pwd:" + AppDataCenter.PIN_Block[0]);
			} else {
				Log.e("***", "计算密码失败...");
			}
			ZXBPOS.handData(mHandler, r);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
