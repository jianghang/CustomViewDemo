package com.example.customviewdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class PorterDuffXfermodeActivity extends Activity {

	private Bitmap mBitmap;
	private Bitmap mOut;
	private Paint mPaint;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_porter_duff_xfermode);

		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
		mOut = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(mOut);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		canvas.drawCircle(
				mBitmap.getWidth() / 2,
				mBitmap.getHeight() / 2,
				mBitmap.getHeight() / 2 > mBitmap.getWidth() / 2 ? mBitmap.getWidth() / 2 : mBitmap.getHeight() / 2,
						mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);

		mImageView = (ImageView) findViewById(R.id.imageTest);
		mImageView.setImageBitmap(mOut);
	}
}
