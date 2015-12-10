package com.example.customviewdemo.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class DragViewHelper extends FrameLayout {

	private ViewDragHelper mViewDragHelper;
	private View mainView,menuView;
	private int mWidth;

	public DragViewHelper(Context context){
		super(context);
	}

	public DragViewHelper(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mViewDragHelper = ViewDragHelper.create(this, callback);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mWidth = menuView.getMeasuredWidth();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mViewDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mViewDragHelper.processTouchEvent(event);
		
		return true;
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		
		if(mViewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		
		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			return arg0 == mainView;
		}
		
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			return left;
		};
		
		public int clampViewPositionVertical(View child, int top, int dy) {
			
			return 0;
		};
		
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if(mainView.getLeft() < 500){
				mViewDragHelper.smoothSlideViewTo(mainView, 0, 0);
				ViewCompat.postInvalidateOnAnimation(DragViewHelper.this);
			}else{
				mViewDragHelper.smoothSlideViewTo(mainView, 300, 0);
				ViewCompat.postInvalidateOnAnimation(DragViewHelper.this);
			}
		};
	};
}
