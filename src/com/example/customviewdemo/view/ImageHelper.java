package com.example.customviewdemo.view;

import java.io.FileDescriptor;
import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Log;

public class ImageHelper {
	/**   
	* @Title: handleImageEffect   
	* @Description: 改变图片Rotate(色调)、Saturation(饱和度)、Scale(亮度)
	* @param bm
	* @param hue
	* @param saturation
	* @param lum
	* @return: Bitmap      
	* @throws   
	*/  
	public static Bitmap handleImageEffect(Bitmap bm, float hue,
			float saturation, float lum) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();

		ColorMatrix hueMatrix = new ColorMatrix();
		hueMatrix.setRotate(0, hue);
		hueMatrix.setRotate(1, hue);
		hueMatrix.setRotate(2, hue);

		ColorMatrix saturationMatrix = new ColorMatrix();
		saturationMatrix.setSaturation(saturation);

		ColorMatrix lumMatrix = new ColorMatrix();
		lumMatrix.setScale(lum, lum, lum, 1);

		ColorMatrix imageMatrix = new ColorMatrix();
		imageMatrix.postConcat(hueMatrix);
		imageMatrix.postConcat(saturationMatrix);
		imageMatrix.postConcat(lumMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
		canvas.drawBitmap(bm, 0, 0, paint);

		return bmp;
	}

	/**   
	* @Title: decodeSampledBitmapFromFileDescriptor   
	* @Description: 从FileDescriptor加载图片同时根据你需要的宽高进行缩放
	* @param fd
	* @param reqWidth
	* @param reqHeight
	* @return: Bitmap      
	* @throws   
	*/  
	public static Bitmap decodeSampledBitmapFromFileDescriptor(
			FileDescriptor fd, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFileDescriptor(fd, null, options);
	}

	/**   
	* @Title: decodeSampledBitmapFromResource   
	* @Description: 从资源文件夹加载图同时根据你需要的宽高进行缩放
	* @param res
	* @param resId
	* @param reqWidth
	* @param reqHeight
	* @return: Bitmap      
	* @throws   
	*/  
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**   
	* @Title: decodeSampledBitmapFromFile   
	* @Description: 从文件路径加载图片同时根据你需要的宽高进行缩放
	* @param filepath
	* @param reqWidth
	* @param reqHeight
	* @return: Bitmap      
	* @throws   
	*/  
	public static Bitmap decodeSampledBitmapFromFile(String filepath,
			int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		Log.i("ImageHelper", "imageWdith: " + options.outWidth
				+ " imageHeight: " + options.outHeight + " inSampleSize: "
				+ options.inSampleSize);

		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filepath, options);
	}

	/**   
	* @Title: calculateInSampleSize   
	* @Description: 根据接收的宽高计算图片的缩放比
	* @param options
	* @param reqWidth
	* @param reqHeight
	* @return: int      
	* @throws   
	*/  
	private static int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampledSize = 1;

		if (height > reqHeight || width > reqWidth) {
			int halfHeight = height / 2;
			int halfWidth = width / 2;
			while ((halfHeight / inSampledSize) >= reqHeight
					&& (halfWidth / inSampledSize) >= reqWidth) {
				inSampledSize *= 2;
			}
		}

		return inSampledSize;
	}

	/**   
	* @Title: rotateImage   
	* @Description: 检测加载的图片是否是90度正的，如果不是则旋转为90度正
	* @param bm
	* @param filepath
	* @return: Bitmap      
	* @throws   
	*/  
	public static Bitmap rotateImage(Bitmap bm, String filepath) {
		try {
			ExifInterface exifInterface = new ExifInterface(filepath);
			int result = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			int rotate = 0;
			switch (result) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;

				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;

				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;

				break;

			default:
				break;
			}

			if (rotate > 0) {
				Matrix matrix = new Matrix();
				matrix.setRotate(rotate);
				Bitmap rotateBitmap = Bitmap.createBitmap(bm, 0, 0,
						bm.getWidth(), bm.getHeight(), matrix, true);
				if (rotateBitmap != null) {
					bm.recycle();
					bm = rotateBitmap;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bm;
	}
}
