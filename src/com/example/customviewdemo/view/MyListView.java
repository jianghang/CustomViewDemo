package com.example.customviewdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

public class MyListView extends ListView {
	
	private Context mContext;
	private int mMaxOverDistance = 50;

	public MyListView(Context context){
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		initView();
	}
	
	public void initView(){
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		float density = metrics.density;
		Log.i("MyListView", "density: " + density);
		mMaxOverDistance = (int)(density * mMaxOverDistance );
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, mMaxOverDistance, isTouchEvent);
	}
}
