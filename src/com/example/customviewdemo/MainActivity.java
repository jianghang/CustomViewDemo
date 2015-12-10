package com.example.customviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends Activity {
	
    private Button listViewSimpleBtn;
	private Button chatListViewBtn;
	private Button dynamicListViewBtn;
	private Button dragViewBtn;
	private Button dragViewHelperBtn;
	private Button bitmapColorBtn;
	private Button porterDuffBtn;
	private Button photoWallBtn;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listViewSimpleBtn = (Button)findViewById(R.id.listViewSimpleActiviry);
        listViewSimpleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ListViewSimpleActivity.class);
				startActivity(intent);
			}
		});
        
        chatListViewBtn = (Button)findViewById(R.id.chatListViewActivity);
        chatListViewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ChatListViewActivity.class);
				startActivity(intent);
			}
		});
        
        dynamicListViewBtn = (Button)findViewById(R.id.dynamicListViewActivity);
        dynamicListViewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DynamicListViewActivity.class);
				startActivity(intent);
			}
		});
        
        dragViewBtn = (Button)findViewById(R.id.dragViewActivity);
        dragViewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DragViewActivity.class);
				startActivity(intent);
			}
		});
        
        dragViewHelperBtn = (Button)findViewById(R.id.dragViewHelperActivity);
        dragViewHelperBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DragViewHelperActivity.class);
				startActivity(intent);
			}
		});
        
        bitmapColorBtn = (Button)findViewById(R.id.bitmapColorActivity);
        bitmapColorBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, BitmapColorActivity.class);
				startActivity(intent);
			}
		});
        
        porterDuffBtn = (Button)findViewById(R.id.PorterDuffActivity);
        porterDuffBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,PorterDuffXfermodeActivity.class);
				startActivity(intent);
			}
		});
        
        photoWallBtn = (Button)findViewById(R.id.PhotoWallActivity);
        photoWallBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, PhotoWallActivity.class);
				
				startActivity(intent);
			}
		});
    }
}
