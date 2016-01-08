package com.example.wavepullpushscrolllayout;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private List<Bean> mBeanList;

	private Activity mActivity;

	public MyAdapter(Activity activity, List<Bean> beanList) {
		mBeanList = beanList;
		mActivity = activity;
	}

	@Override
	public int getCount() {
		return mBeanList.size();
	}

	@Override
	public Bean getItem(int position) {
		return mBeanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_list, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Bean bean = getItem(position);
		holder.iv.setImageResource(bean.image);
		holder.tv.setText(bean.name);
		return convertView;
	}

	static class ViewHolder {
		ImageView iv;
		TextView tv;
	}

}
