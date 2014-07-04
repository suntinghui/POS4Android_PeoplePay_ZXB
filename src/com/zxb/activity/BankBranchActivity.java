package com.zxb.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zxb.R;
import com.zxb.model.Bank;

// 支行
public class BankBranchActivity extends BaseActivity implements OnClickListener {
	ListView mListView ;
	ArrayList<Bank> arrayList;
	Adapter adapter;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_branch);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		arrayList = (ArrayList<Bank>) this.getIntent().getSerializableExtra("list");
		mListView = (ListView) findViewById(R.id.listview);
		adapter = new Adapter(this);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				intent.putExtra("bankbranchid", arrayList.get(arg2).getCode()+"");
				intent.putExtra("bankbranchname", arrayList.get(arg2).getName());
				BankBranchActivity.this.setResult(5, intent);
				finish();

			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_back:
			this.finish();
			break;
		default:
			break;
		}

	}

	public final class ViewHolder {
		public TextView tv_content;

	}
	
	public class Adapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public Adapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return arrayList.size();
		}

		public Object getItem(int arg0) {
			return arrayList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.left_right_arrow_listitem, null);

				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_content.setText(((Bank) (arrayList.get(position))).getName());

			return convertView;
		}
	}
	
}
