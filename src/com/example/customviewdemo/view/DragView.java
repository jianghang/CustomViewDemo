package com.example.customviewdemo.view;

import com.example.customviewdemo.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Scroller;

public class DragView extends View {
	
	private static final float HOUR_LENGTH = 3/10F;
	private static final float MINUTE_LENGTH = 2/10F;

	private int lastX;
	private int lastY;
	private Scroller mScroller;
	private int mViewWidth;
	private int mViewHeight;
	private int mRadius;

	public DragView(Context context) {
		super(context);
	}

	public DragView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paintCircle = new Paint();
		paintCircle.setAntiAlias(true);
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setStrokeWidth(5);
		canvas.drawCircle(mViewWidth / 2, mViewHeight / 2, mRadius, paintCircle);

		Paint paintDegree = new Paint();
		paintDegree.setStrokeWidth(3);

		canvas.save();
		for (int i = 0; i < 12; i++) {
			if (i == 3 | i == 6 | i == 9 | i == 0) {
				paintDegree.setStrokeWidth(5);
				canvas.drawLine(mViewWidth / 2, mViewHeight / 2 - mRadius,
						mViewWidth / 2, mViewHeight / 2 - mRadius + 40,
						paintDegree);
			} else {
				paintDegree.setStrokeWidth(3);
				paintDegree.setTextSize(15);
				canvas.drawLine(mViewWidth / 2, mViewHeight / 2 - mRadius,
						mViewWidth / 2, mViewHeight / 2 - mRadius + 20,
						paintDegree);
				String str = String.valueOf(i);
				canvas.drawText(str,
						mViewWidth / 2 - paintDegree.measureText(str) / 2,
						mViewHeight / 2 - mRadius + 40, paintDegree);
			}
			canvas.rotate(30, mViewWidth / 2, mViewHeight / 2);
		}
		canvas.restore();

		paintDegree.setTextSize(30);
		canvas.drawText("12", mViewWidth / 2 - paintDegree.measureText("12")
				/ 2, mViewHeight / 2 - mRadius + 70, paintDegree);

		canvas.drawText("6", mViewWidth / 2 - paintDegree.measureText("6") / 2,
				mViewHeight / 2 + mRadius - 50, paintDegree);

		canvas.drawText(
				"3",
				mViewWidth / 2 + mRadius - 70,
				mViewHeight
						/ 2
						+ ((Math.abs(paintDegree.ascent()) + Math
								.abs(paintDegree.descent())) / 2 - paintDegree
								.descent()), paintDegree);

		canvas.drawText(
				"9",
				mViewWidth / 2 - mRadius + 50,
				mViewHeight
						/ 2
						+ ((Math.abs(paintDegree.ascent()) + Math
								.abs(paintDegree.descent())) / 2 - paintDegree
								.descent()), paintDegree);
		
		Paint paintHour = new Paint();
		paintHour.setStrokeWidth(10);
		paintHour.setStrokeCap(Paint.Cap.ROUND);
		Paint paintMinute = new Paint();
		paintMinute.setStrokeWidth(5);
		paintMinute.setStrokeCap(Paint.Cap.ROUND);
		
		canvas.save();
		canvas.translate(mViewWidth/2, mViewHeight/2);
		canvas.drawLine(0, 0, mRadius * HOUR_LENGTH, 100, paintHour);
		canvas.drawLine(0, 0, mRadius * MINUTE_LENGTH, 200, paintMinute);
		paintHour.setStrokeWidth(15);
		canvas.drawPoint(0, 0, paintHour);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mViewWidth = getMeasuredWidth();
		mViewHeight = getMeasuredHeight();
		mRadius = mViewWidth / 2 - 10;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = x;
			lastY = y;

			break;

		case MotionEvent.ACTION_MOVE:
			int offsetX = x - lastX;
			int offsetY = y - lastY;

			// System.out.println("lastX: " + lastX + " lastY: " + lastY +
			// " x: " + x + " y: " + y);

			// layout(getLeft() + offsetX, getTop() + offsetY, getRight()
			// + offsetX, getBottom() + offsetY);
			// offsetLeftAndRight(offsetX);
			// offsetTopAndBottom(offsetY);
			// ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams)
			// getLayoutParams();
			// layoutParams.leftMargin = getLeft() + offsetX;
			// layoutParams.topMargin = getTop() + offsetY;
			// setLayoutParams(layoutParams);

			((View) getParent()).scrollBy(-offsetX, -offsetY);

			break;

		case MotionEvent.ACTION_UP:
			View viewGroup = (View) getParent();
			mScroller.startScroll(viewGroup.getScrollX(),
					viewGroup.getScrollY(), -viewGroup.getScrollX(),
					-viewGroup.getScrollY(), 2000);
			invalidate();

			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		if (mScroller.computeScrollOffset()) {
			((View) getParent()).scrollTo(mScroller.getCurrX(),
					mScroller.getCurrY());
		}
		invalidate();
	}
}
