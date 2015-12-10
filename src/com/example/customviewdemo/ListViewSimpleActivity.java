package com.example.customviewdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewSimpleActivity extends Activity {

	private ListView mListview;
	private List<String> mList;
	private MyAdapter mDataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view_simple);
		
		mList = new ArrayList<String>();
		
		mListview = (ListView)findViewById(R.id.listview);
		mDataAdapter = new MyAdapter();
		mListview.setAdapter(mDataAdapter);
		
		mListview.setEmptyView(findViewById(R.id.emptyImageview));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			mList.add("Simple Data");
			mDataAdapter.notifyDataSetChanged();
			mListview.smoothScrollToPosition(mList.size() - 1);
			
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = View.inflate(ListViewSimpleActivity.this, R.layout.listview_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.imageView);
				holder.title = (TextView) convertView.findViewById(R.id.textView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.img.setBackgroundResource(R.drawable.ic_launcher);
			holder.title.setText(mList.get(position));
			
			return convertView;
		}
		
		public final class ViewHolder{
			public ImageView img;
			public TextView title;
		}
		
	}
}
