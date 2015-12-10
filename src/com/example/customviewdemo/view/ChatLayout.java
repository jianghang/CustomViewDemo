package com.example.customviewdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ChatLayout extends RelativeLayout {
	
	private OnSizeChangedListener mListener;
	
	public ChatLayout(Context context){
		super(context);
	}

	public ChatLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		if(mListener != null){
			mListener.onSizeChanged(w, h, oldw, oldh);
		}
	}
	
	public void setOnSizeChangedListener(OnSizeChangedListener listener){
		this.mListener = listener;
	}
	
	public interface OnSizeChangedListener{
		public void onSizeChanged(int w,int h,int oldw,int oldh);
	}
}
