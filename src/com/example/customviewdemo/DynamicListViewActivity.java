package com.example.customviewdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class DynamicListViewActivity extends Activity {

	private ListView mListView;
	private ArrayList<String> mData;
	private Context mContext;
	private MyAdapter mAdapter;
	private int mCurrentItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_list_view);

		mContext = this;

		mListView = (ListView) findViewById(R.id.listview);
		mData = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			mData.add("Item: " + i);
		}
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.setCurrentItem(position);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void setCurrentItem(int position){
			mCurrentItem = position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
			
			if(mCurrentItem == position){
				layout.addView(addFocusView(position));
			}else{
				layout.addView(addNormalView(position));
			}
			return layout;
		}

		private View addFocusView(int i) {
			ImageView iv = new ImageView(mContext);
			iv.setImageResource(R.drawable.ic_launcher);

			return iv;
		}

		private View addNormalView(int i) {
			LinearLayout layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.HORIZONTAL);

			ImageView iv = new ImageView(mContext);
			iv.setImageResource(R.drawable.in_icon);
			layout.addView(iv, new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			TextView tv = new TextView(mContext);
			tv.setText(mData.get(i));
			layout.addView(tv, new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layout.setPadding(10, 10, 10, 10);
			layout.setGravity(Gravity.CENTER_VERTICAL);
			
			return layout;
		}
	}
}
