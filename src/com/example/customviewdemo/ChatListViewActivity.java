package com.example.customviewdemo;

import java.util.ArrayList;
import java.util.List;

import com.example.customviewdemo.view.ChatItemListViewBean;
import com.example.customviewdemo.view.ChatItemViewAdapter;
import com.example.customviewdemo.view.ChatLayout;
import com.example.customviewdemo.view.ChatLayout.OnSizeChangedListener;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatListViewActivity extends Activity {

	private ListView mListView;
	private EditText mEdittext;
	private List<ChatItemListViewBean> mData;
	private ChatLayout chatLayout;
	private Button mSendBtn;
	private ChatItemViewAdapter ChatAdapter;
	private boolean mType;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(mData.size() != 0){
				mListView.setSelection(mData.size() - 1);
			}
		};
	};
	private ActionBar mActionbar;
	private View mRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list_view);
		
		mActionbar = getActionBar();
		mRoot = this.findViewById(R.id.chatlayout);
		mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				int offset = mRoot.getRootView().getHeight() - mRoot.getHeight();
				if(offset > 100){
					
				}else{
					
				}
			}
		});

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mListView = (ListView) findViewById(R.id.chatListView);
		mListView.setEmptyView(findViewById(R.id.emptyTextView));

//		ChatItemListViewBean bean1 = new ChatItemListViewBean();
//		bean1.setType(0);
//		bean1.setIcon(BitmapFactory.decodeResource(getResources(),
//				R.drawable.in_icon));
//		bean1.setText("Hello how are you?");
//
//		ChatItemListViewBean bean2 = new ChatItemListViewBean();
//		bean2.setType(1);
//		bean2.setIcon(BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_launcher));
//		bean2.setText("Fine thank you,and you?");
//
//		ChatItemListViewBean bean3 = new ChatItemListViewBean();
//		bean3.setType(0);
//		bean3.setIcon(BitmapFactory.decodeResource(getResources(),
//				R.drawable.in_icon));
//		bean3.setText("I am fine too");
//
//		ChatItemListViewBean bean4 = new ChatItemListViewBean();
//		bean4.setType(1);
//		bean4.setIcon(BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_launcher));
//		bean4.setText("I am fine too");
//
//		ChatItemListViewBean bean5 = new ChatItemListViewBean();
//		bean5.setType(0);
//		bean5.setIcon(BitmapFactory.decodeResource(getResources(),
//				R.drawable.in_icon));
//		bean5.setText("I am fine too");
//
//		ChatItemListViewBean bean6 = new ChatItemListViewBean();
//		bean6.setType(1);
//		bean6.setIcon(BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_launcher));
//		bean6.setText("I am fine too");

		mData = new ArrayList<ChatItemListViewBean>();
//		mData.add(bean1);
//		mData.add(bean2);
//		mData.add(bean3);
//		mData.add(bean4);
//		mData.add(bean5);
//		mData.add(bean6);
		ChatAdapter = new ChatItemViewAdapter(this, mData);
		mListView.setAdapter(ChatAdapter);
		mListView.setSelection(mData.size() - 1);
		
		mEdittext = (EditText)findViewById(R.id.editText);
		mSendBtn = (Button)findViewById(R.id.sendBtn);
		mSendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = mEdittext.getText().toString();
				mEdittext.setText("");
				if(str.equals("")){
					Toast.makeText(ChatListViewActivity.this, "Message is null!", Toast.LENGTH_SHORT).show();
					return;
				}
				ChatItemListViewBean bean = new ChatItemListViewBean();
				bean.setText(str);
				
				if(mType){
					bean.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.message_selected));
					bean.setType(1);
				}else{
					bean.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.message_unselected));
					bean.setType(0);
				}
				
				mData.add(bean);
				ChatAdapter.notifyDataSetChanged();
				
				mListView.smoothScrollToPosition(mData.size() - 1);
			}
		});
		
		chatLayout = (ChatLayout)findViewById(R.id.chatlayout);
		chatLayout.setOnSizeChangedListener(new OnSizeChangedListener() {
			
			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				handler.sendEmptyMessage(0);
				System.out.println("ActionBar: " + mActionbar.isShowing());
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chatmenu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Exchange:
			mType = !mType;
			Toast.makeText(ChatListViewActivity.this, "User Change: " + mType, Toast.LENGTH_SHORT).show();
			
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
