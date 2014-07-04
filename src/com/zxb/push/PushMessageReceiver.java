package com.zxb.push;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.zxb.activity.BaseActivity;
import com.zxb.activity.CatalogActivity;
import com.zxb.activity.LoginActivity;
import com.zxb.client.ApplicationEnvironment;
import com.zxb.client.Constants;
import com.zxb.client.TransferRequestTag;
import com.zxb.network.LKAsyncHttpResponseHandler;
import com.zxb.network.LKHttpRequest;
import com.zxb.network.LKHttpRequestQueue;
import com.zxb.network.LKHttpRequestQueueDone;
import com.zxb.util.PhoneUtil;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	/** TAG to Log */
	//public static final String TAG = PushMessageReceiver.class.getSimpleName();

	public static final String TAG = "PUSH";
	
	AlertDialog.Builder builder;
	
	/**
	 * 
	 * 
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {

		Log.d(TAG, ">>> Receive intent: \r\n" + intent);

		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			//获取消息内容
			String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

			//消息的用户自定义内容读取方式
			Log.i(TAG, "onMessage: " + message);

			//用户在此自定义处理消息,以下代码为demo界面展示用
			Intent responseIntent = null;
			responseIntent = new Intent(BPushUtil.ACTION_MESSAGE);
			responseIntent.putExtra(BPushUtil.EXTRA_MESSAGE, message);
			responseIntent.setClass(context, LoginActivity.class);
			responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(responseIntent);

		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			
			//处理绑定等方法的返回数据
			//PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到
			
			//获取方法
			//String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			//方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			//绑定失败的原因有多种，如网络原因，或access token过期。
			//请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			//可以通过限制重试次数，或者在其他时机重新调用来解决。
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE,PushConstants.ERROR_SUCCESS);
			//返回内容
			String content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			
			if (errorCode == 0) {
				String appid = "";
				String channelid = "";
				String userid = "";

				try {
					JSONObject jsonContent = new JSONObject(content);
					JSONObject params = jsonContent.getJSONObject("response_params");
					appid = params.getString("appid");
					channelid = params.getString("channel_id");
					userid = params.getString("user_id");
					
					// 保存此值
					Editor editor = ApplicationEnvironment.getInstance().getPreferences().edit();
					editor.putString(Constants.kBPUSH_APPID, appid);
					editor.putString(Constants.kBPUSH_CHANNELID, channelid);
					editor.putString(Constants.kBPUSH_USERID, userid);
					editor.commit();
					
					this.setBPushInfo(userid, channelid);
					
				} catch (JSONException e) {
					Log.e("BPUSH", "Parse bind json infos error: " + e);
				}
				
			} else {
				Log.e("BPUSH", "Bind Fail, Error Code: " + errorCode);
				if (errorCode == 30607) {
					Log.e("Bind Fail", "update channel token-----!");
				}
			}
			
		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			Log.d(TAG, "intent=" + intent.toUri(0));
			
			/*
			Intent aIntent = new Intent();
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			aIntent.setClass(context, LoginActivity.class);
			
			String title = intent.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, title);
			String content = intent.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT, content);

			context.startActivity(aIntent);
			*/
		}
	}
	
	// 设置推送
	protected void setBPushInfo(String userId, String channelId) {
		
	}
	
}
