package com.example.qianziapp.adapter;

import java.util.ArrayList;

import com.example.qianziapp.R;
import com.example.qianziapp.bean.Bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListAdapter extends BaseAdapter {
	ArrayList<Bean> data;
	Context mContext;
	LayoutInflater mInflater;
	
	public MainListAdapter(Context context,ArrayList<Bean> data) {
		super();
		this.data = data;
		this.mContext = context;
		mInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.item_mainlist, null);
			viewHolder=new ViewHolder();
			viewHolder.iv=(ImageView) convertView.findViewById(R.id.iv_icon);
			viewHolder.tv=(TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.iv.setImageResource(data.get(position).getId());
		viewHolder.tv.setText(data.get(position).getContent());
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView iv;
		TextView tv;
	}
}
