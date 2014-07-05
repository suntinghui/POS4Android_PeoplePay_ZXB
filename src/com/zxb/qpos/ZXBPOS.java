package com.zxb.qpos;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fncat.xswipe.controller.POSManage;
import com.fncat.xswipe.controller.StatusListener;
import com.zxb.activity.BaseActivity;
import com.zxb.client.AppDataCenter;
import com.zxb.client.AppInit;
import com.zxb.util.ByteUtil;

public class ZXBPOS {

	private static POSManage manage = null;
	
	public static POSManage getPOSManage() {
		if (null == manage) {
			manage = POSManage.getInstance();
			manage.setDebugMode(false);
			manage.setListener(AppInit.context, mStatusListener);
		}

		return manage;
	}
	
	public static void handData(Handler mHandler, Object obj, int returnCode) {
		if (mHandler != null){
			Message message = mHandler.obtainMessage();
			message.obj = obj;
			message.what = returnCode;
			mHandler.sendMessage(message);
		}
	}
	
	public static void handData(Handler mHandler, int returnCode) {
		if (mHandler != null){
			Message message = mHandler.obtainMessage();
			message.obj = ErrorMsg.getErrorMsg().get(returnCode);
			message.what = returnCode;
			mHandler.sendMessage(message);
		}
	}
	
	
	public static void broadcastUpdate(final String action) {
		try{
			final Intent intent = new Intent(action);
			AppInit.context.sendBroadcast(intent);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static StatusListener mStatusListener = new StatusListener() {

		@Override
		public void onCardData(byte[] encTrack) {
			AppDataCenter.encTrack = encTrack;
			
			Log.e("***", "取得encTrack..."+ByteUtil.byte2hex(encTrack));
			
			// TODO
		}

		@Override
		public void onCardInfo(String arg0, byte[] arg1) {
			
		}

		@Override
		public void onCardNum(String cardNo) {
			AppDataCenter.cardNo = cardNo;
			
			Log.e("***", "取得card no..."+cardNo);
		}

		@Override
		public void onError(int arg0) {
			
		}

		@Override
		public void onPlugin() {
			Toast.makeText(BaseActivity.getTopActivity(), "设备已插入", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onPlugout() {
			Toast.makeText(BaseActivity.getTopActivity(), "设备已拔出", Toast.LENGTH_SHORT).show();
		}
		
	};
	
	public static void checkBattery(){
		if (AppDataCenter.Adc[0] < 3) {
			String prompt = "刷卡器电量过低！！！";
			if (AppDataCenter.DevTypes[0].contains("ZFT-ZXB-I")) { // I型
				prompt = "刷卡器电量过低，请及时更换电池！";
			} else if (AppDataCenter.DevTypes[0].contains("ZFT-ZXB-S")) { // // S型
				prompt = "刷卡器电量过低，请及时充电！";
			}
				
			Toast.makeText(BaseActivity.getTopActivity(), prompt, Toast.LENGTH_SHORT).show();
		}
	}

}
