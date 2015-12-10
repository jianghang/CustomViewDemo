package com.example.customviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PhotoViewActivity extends Activity {
	public static final String TAG = "PhotoViewActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_view);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra("uri");
		Log.i(TAG, "url: " + url);
	}
}
