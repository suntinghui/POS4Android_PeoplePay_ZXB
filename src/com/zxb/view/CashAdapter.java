package com.zxb.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxb.R;
import com.zxb.activity.TransferListActivity;
import com.zxb.model.CashModel;
import com.zxb.util.DateUtil;

public class CashAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater mLayoutInflater;
	private ArrayList<CashModel> array = new ArrayList<CashModel>();
	private Context mContext;

	public CashAdapter(Context context, int resource, ArrayList<CashModel> objects) {

		mLayoutInflater = LayoutInflater.from(context);
		array = objects;
		mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();

			convertView = mLayoutInflater.inflate(R.layout.list_item_cash, null);

			holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);

			holder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			holder.tv_week = (TextView) convertView.findViewById(R.id.tv_week);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.btn_delete.setTag(position+10000);
		holder.btn_delete.setOnClickListener(this);
		CashModel model = array.get(position);
		String amount = model.getAmount();
		if (amount != null) {
			holder.tv_amount.setText("￥" + amount);
		}
		holder.tv_date.setText(DateUtil.getDayWeekTime(model.getDate().replaceAll("-", "") + model.getTime().replaceAll(":", "")));
		return convertView;
	}

	public void setData(ArrayList<CashModel> data) {
		this.array = data;
	}

	public ArrayList<CashModel> getData() {
		return array;
	}

	private class ViewHolder {
		public LinearLayout contentLayout;
		public TextView tv_date;
		public TextView tv_week;
		public TextView tv_amount;
		public Button btn_delete;
	}

	@Override
	public int getCount() {
		return array.size();
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public void onClick(View arg0) {
		
		TransferListActivity activity = (TransferListActivity) mContext;
		Integer tag = (Integer) arg0.getTag()-10000;
		activity.deleteCashItem(tag + "");

	}

}
