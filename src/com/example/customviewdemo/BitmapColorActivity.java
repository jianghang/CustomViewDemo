package com.example.customviewdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.customviewdemo.R.color;
import com.example.customviewdemo.view.ImageHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class BitmapColorActivity extends Activity implements
		OnSeekBarChangeListener {

	private static final int CROP_PHOTO = 2;
	private static final int MID_VALUE = 127;
	private static final int MAX_VALUE = 255;
	private ImageView colorImage;
	private Uri imageUri;
	private Bitmap mBitmap;
	private SeekBar mHueSeek;
	private SeekBar mSaturationSeek;
	private SeekBar mLumSeek;
	private float mHue;
	private float mSaturation;
	private float mLum;
	private ImageView originalImage;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitmap_color);

		mContext = this;
		mHue = (MID_VALUE - MID_VALUE) * 1.0F / MID_VALUE * 180;
		mSaturation = MID_VALUE * 1.0F / MID_VALUE;
		mLum = MID_VALUE * 1.0F / MID_VALUE;

		colorImage = (ImageView) findViewById(R.id.colorImage);
		originalImage = (ImageView) findViewById(R.id.originalImage);

		mHueSeek = (SeekBar) findViewById(R.id.hueSeek);
		mHueSeek.setMax(MAX_VALUE);
		mHueSeek.setProgress(MID_VALUE);
		mHueSeek.setOnSeekBarChangeListener(this);

		mSaturationSeek = (SeekBar) findViewById(R.id.staurationSeek);
		mSaturationSeek.setMax(MAX_VALUE);
		mSaturationSeek.setProgress(MID_VALUE);
		mSaturationSeek.setOnSeekBarChangeListener(this);

		mLumSeek = (SeekBar) findViewById(R.id.lumSeek);
		mLumSeek.setMax(MAX_VALUE);
		mLumSeek.setProgress(MID_VALUE);
		mLumSeek.setOnSeekBarChangeListener(this);

		colorImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}

	protected void showPopupWindow(View v) {
		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.pop_window, null);
		TextView hueTv = (TextView) contentView.findViewById(R.id.huetv);
		TextView saturationTv = (TextView) contentView
				.findViewById(R.id.saturationtv);
		TextView lumTv = (TextView) contentView.findViewById(R.id.lumtv);

		hueTv.setText("Hue: " + mHue);
		saturationTv.setText("Saturation: " + mSaturation);
		lumTv.setText("Lum: " + mLum);

		PopupWindow popupwindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupwindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shapedemo));
		popupwindow.showAsDropDown(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bitmapcolormenu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_bitmap:
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, CROP_PHOTO);

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case CROP_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				LinearLayout layout = (LinearLayout) colorImage.getParent();
				// mBitmap = BitmapFactory.decodeFile(picturePath);
				mBitmap = ImageHelper.decodeSampledBitmapFromFile(picturePath,
						layout.getWidth(), layout.getHeight());
				mBitmap = ImageHelper.rotateImage(mBitmap, picturePath);
				
				originalImage.setImageBitmap(mBitmap);
				colorImage.setImageBitmap(mBitmap);

				mHueSeek.setProgress(MID_VALUE);
				mSaturationSeek.setProgress(MID_VALUE);
				mLumSeek.setProgress(MID_VALUE);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.hueSeek:
			mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180;

			break;

		case R.id.staurationSeek:
			mSaturation = progress * 1.0F / MID_VALUE;

			break;

		case R.id.lumSeek:
			mLum = progress * 1.0F / MID_VALUE;

			break;

		default:
			break;
		}
		Bitmap bm = getImageViewBitmap(originalImage);
		colorImage.setImageBitmap(ImageHelper.handleImageEffect(bm, mHue,
				mSaturation, mLum));
	}

	private Bitmap getImageViewBitmap(ImageView iv) {
		Bitmap bm = ((BitmapDrawable) iv.getDrawable()).getBitmap();

		return bm;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}
}
