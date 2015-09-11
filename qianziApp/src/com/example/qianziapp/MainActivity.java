package com.example.qianziapp;

import java.util.ArrayList;

import com.example.qianziapp.adapter.MainListAdapter;
import com.example.qianziapp.bean.Bean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener, OnClickListener {
	ListView listview;
	MainListAdapter adapter;
	ArrayList<Bean> mData;
	Button bt_choose;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		initData();
	}


	private void initView() {
		listview=(ListView) findViewById(R.id.listview_main);
		bt_choose=(Button) findViewById(R.id.btn_write);
		listview.setOnItemClickListener(this);
		bt_choose.setOnClickListener(this);
	}


	private void initData() {
		mData=new ArrayList<Bean>();
		for (int i = 0; i < 10; i++) {
			Bean bean=new Bean();
			bean.setId(R.drawable.ic_launcher);
			bean.setContent("²âÊÔ"+i);
			mData.add(bean);
		}
		adapter = new MainListAdapter(this, mData);
		listview.setAdapter(adapter);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(this, "µã»÷£º"+arg2, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onClick(View v) {
		Intent intent=new Intent(MainActivity.this,ActivityWrite.class);
		startActivity(intent);
	}
	
}
