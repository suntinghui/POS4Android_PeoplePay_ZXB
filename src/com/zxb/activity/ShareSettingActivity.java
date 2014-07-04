package com.zxb.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxb.R;
import com.zxb.util.WXUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareSettingActivity extends BaseActivity implements OnClickListener {

	private ListView listView = null;
	private Adapter adapter = null;

	private Integer[] imageIds = { R.drawable.share_sina_weibo, R.drawable.share_weixin, R.drawable.share_weixin_friend };

	private String[] titles = { "新浪微博", "微信好友", "朋友圈" };

	private static final String APP_ID = "wx1e4484ab6b577a3d";
	private IWXAPI api = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_share_setting);

		api = WXAPIFactory.createWXAPI(getApplicationContext(), APP_ID, true);
		api.registerApp(APP_ID);

		listView = (ListView) this.findViewById(R.id.listview);
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		adapter = new Adapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0:
					Toast.makeText(ShareSettingActivity.this, "暂未实现", Toast.LENGTH_SHORT).show();
					break;
					
				case 1: // 微信好友
					sendToWXFriend();
//					Toast.makeText(ShareSettingActivity.this, "暂未实现", Toast.LENGTH_SHORT).show();
					break;
					
				case 2: // 朋友圈
					sharedToCirle();
//					Toast.makeText(ShareSettingActivity.this, "暂未实现", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		});

	}

	private void sendToWXFriend() {
		if (!api.isWXAppInstalled()){
			Toast.makeText(this, "您还没有安装微信", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 初始化一个WXTextObject对象  
        String text = "我在用众付宝进行收款，移动互联网时代的刷卡利器，速来围观！！！"; 
        
        WXWebpageObject webObj = new WXWebpageObject();
        webObj.webpageUrl = "http://www.people2000.net";

        WXMediaMessage msg = new WXMediaMessage(webObj);
        msg.title = "众付宝";
        msg.description = text;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        msg.thumbData = WXUtil.bmpToByteArray(thumb, true);
          
        SendMessageToWX.Req req = new SendMessageToWX.Req();  
        req.transaction = String.valueOf(System.currentTimeMillis());  
        req.message = msg;  
        req.scene = SendMessageToWX.Req.WXSceneSession;
          
        api.sendReq(req);
	}
	
	private void sharedToCirle(){
        if (!api.isWXAppInstalled()){
			Toast.makeText(this, "您还没有安装微信", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 初始化一个WXTextObject对象  
        String text = "众付宝，惊呆了我和我的小伙伴，借记卡和信用卡都能刷，到账周期短，扣率低，真的是太好用了。"; 
        
        WXWebpageObject webObj = new WXWebpageObject();
        webObj.webpageUrl = "http://www.people2000.net";

        WXMediaMessage msg = new WXMediaMessage(webObj);
        msg.title = "众付宝，刷新梦想 刷出未来";
        msg.description = text;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        msg.thumbData = WXUtil.bmpToByteArray(thumb, true);
          
        SendMessageToWX.Req req = new SendMessageToWX.Req();  
        req.transaction = String.valueOf(System.currentTimeMillis());  
        req.message = msg;  
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
          
        api.sendReq(req);
	}

	public final class ViewHolder {
		public RelativeLayout contentLayout;

		public TextView tv_content;
		public ImageView iv_left;
	}

	public class Adapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			return titles.length;
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.item_setting, null);

				holder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.contentLayout);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_left.setBackgroundResource(imageIds[position]);
			holder.tv_content.setText(titles[position]);

			return convertView;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

}