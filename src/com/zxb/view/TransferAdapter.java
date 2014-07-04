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
import com.zxb.model.TradeModel;
import com.zxb.util.DateUtil;
import com.zxb.util.StringUtil;


public class TransferAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private ArrayList<TradeModel> array = new ArrayList<TradeModel>();
	private Context mContext;
	public TransferAdapter(Context context, int resource, ArrayList<TradeModel> objects) {

		mLayoutInflater = LayoutInflater.from(context);
		array = objects;
		mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();

			convertView = mLayoutInflater.inflate(R.layout.list_item_transfer, null);

			holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);

			holder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			holder.tv_week = (TextView) convertView.findViewById(R.id.tv_week);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_cardnum = (TextView) convertView.findViewById(R.id.tv_cardnum);
			holder.tv_revoke = (TextView) convertView.findViewById(R.id.tv_revoke);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		TradeModel model = array.get(position);
		String cardNum = model.getCardNo();
		if (cardNum != null) {
			holder.tv_cardnum.setText(StringUtil.formatCardId(StringUtil.formatAccountNo(cardNum)));
		}
		String amount = model.getTxnamt();
		if (amount != null) {
			holder.tv_amount.setText(StringUtil.String2SymbolAmount(amount));
		}

		holder.tv_date.setText(DateUtil.getDayWeekTime(model.getSysDate()));
		holder.tv_revoke.setText(model.getStatus());

		return convertView;
	}

	public void setData(ArrayList<TradeModel> data) {
		this.array = data;
	}

	public ArrayList<TradeModel> getData() {
		return array;
	}

	private class ViewHolder {
		public LinearLayout contentLayout;
		public TextView tv_date;
		public TextView tv_week;
		public TextView tv_amount;
		public TextView tv_cardnum;
		public TextView tv_revoke;
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


}
