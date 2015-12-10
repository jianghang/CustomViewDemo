package com.example.customviewdemo.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpClientConnection;

import com.example.customviewdemo.R;

import libcore.io.DiskLruCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMuxer.OutputFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	
	private static final String TAG = "ImageLoader";

	private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
	private static final int CPU_COUNT = Runtime.getRuntime()
			.availableProcessors();
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private static final long KEEP_ALIVE = 10L;
	private static final int TAG_KEY_URI = R.id.imageloader_uri;
	private static final int DISK_CACHE_INDEX = 0;
	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private static final int MESSAGE_POST_RESULT = 1;
	
	private Context mContext;
	private LruCache<String, Bitmap> mMemoryCache;
	private DiskLruCache mDiskLruCache;
	private boolean mIsDiskLruCacheCreated = false;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
		}
	};

	private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(), sThreadFactory);

	
	private Handler mMainHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			LoaderResult result = (LoaderResult) msg.obj;
			ImageView imageView = result.imageView;
			imageView.setImageBitmap(result.bitmap);
			String uri = (String) imageView.getTag(TAG_KEY_URI);
			if(uri.equals(result.uri)){
				imageView.setImageBitmap(result.bitmap);
			}else{
				Log.w(TAG,"set image bitmap,but url has changed,ignored!");
			}
		};
	};
	
	public void bindBitmap(final String uri,final ImageView imageView){
		bindBitmap(uri, imageView, 0, 0);
	}
	
	/**   
	* @Title: bindBitmap   
	* @Description: 异步加载函数
	* @param uri
	* @param imageView
	* @param reqWidth
	* @param reqHeight      
	* @return: void      
	* @throws   
	*/  
	public void bindBitmap(final String uri,final ImageView imageView,final int reqWidth,final int reqHeight){
		imageView.setTag(TAG_KEY_URI, uri);
		Bitmap bitmap = loadBitmapFromMemCache(uri);
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
			return;
		}
		
		Runnable loadBitmapTask = new Runnable() {
			
			@Override
			public void run() {
				Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
				if(bitmap != null){
					LoaderResult result = new LoaderResult(imageView, uri, bitmap);
					mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
				}
			}
		};
		THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
	}

	private ImageLoader(Context context) {
		mContext = context.getApplicationContext();
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight() / 1024;
			}
		};

		File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
		if (!diskCacheDir.exists()) {
			diskCacheDir.mkdirs();
		}
		if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
			try {
				mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1,
						DISK_CACHE_SIZE);
				mIsDiskLruCacheCreated = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**   
	* @Title: build   
	* @Description: 构建对象的静态函数
	* @param context
	* @return: ImageLoader      
	* @throws   
	*/  
	public static ImageLoader build(Context context) {
		return new ImageLoader(context);
	}
	
	/**   
	* @Title: loadBitmap   
	* @Description: 加载图片函数，优先从MemCache加载，其次从DiskCache加载，最后从网络上下载
	* @param uri
	* @param reqWidth
	* @param reqHeight
	* @return: Bitmap      
	* @throws   
	*/  
	private Bitmap loadBitmap(String uri,int reqWidth,int reqHeight){
		Bitmap bitmap = loadBitmapFromMemCache(uri);
		if(bitmap != null){
			Log.d(TAG, "loadBitmapFromMemCache,url:" + uri);
			return bitmap;
		}
		try {
			bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
			if(bitmap != null){
				Log.d(TAG, "loadBitmapFromDisk,url:" + uri);
				return bitmap;
			}
			bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
			Log.d(TAG,"loadBitmapFromHttp,url:" + uri);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(bitmap == null && !mIsDiskLruCacheCreated){
			Log.w(TAG,"encounter error,DiskLruCache is not created.");
			bitmap = downloadBitmapFromUrl(uri);
		}
		
		return bitmap;
	}
	
	/**   
	* @Title: downloadBitmapFromUrl   
	* @Description: 此函数是在MemCache中没有找到，同时由于手机存储空间不足没有创建DiskCache时调用
	* @param uri
	* @return: Bitmap      
	* @throws   
	*/  
	private Bitmap downloadBitmapFromUrl(String uri) {
		Bitmap bitmap = null;
		HttpURLConnection urlConnection = null;
		BufferedInputStream in = null;
		
		try {
			final URL url = new URL(uri);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
			bitmap = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e(TAG, "Error in downloadBitmap: " + e);
			e.printStackTrace();
		} finally{
			if(urlConnection != null){
				urlConnection.disconnect();
			}
			MyUtils.close(in);
		}
		
		return bitmap;
	}

	/**   
	* @Title: loadBitmapFromHttp   
	* @Description: 从网络下载图片同时存储到DiskLruCache里面
	* @param url
	* @param reqWidth
	* @param reqHeight
	* @return
	* @throws IOException      
	* @return: Bitmap      
	* @throws   
	*/  
	private Bitmap loadBitmapFromHttp(String url,int reqWidth,int reqHeight) throws IOException{
		if(Looper.myLooper() == Looper.getMainLooper()){
			throw new RuntimeException("can not visit network form UI Thread.");
		}
		if(mDiskLruCache == null){
			return null;
		}
		
		String key = hashKeyFromUrl(url);
		DiskLruCache.Editor editor = mDiskLruCache.edit(key);
		if(editor != null){
			OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
			if(downloadUrlToStream(url, outputStream)){
				editor.commit();
			}else{
				editor.abort();
			}
			mDiskLruCache.flush();
		}
		
		return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
	}
	
	private boolean downloadUrlToStream(String urlString,OutputStream outputStream){
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
			out = new BufferedOutputStream(outputStream,IO_BUFFER_SIZE);
			
			int b;
			while((b = in.read()) != -1){
				out.write(b);
			}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(urlConnection != null){
				urlConnection.disconnect();
			}
			MyUtils.close(out);
			MyUtils.close(in);
		}
		
		return false;
	}
	
	/**   
	* @Title: loadBitmapFromDiskCache   
	* @Description: 从DiskCache中加载，同时把加载到MemCache中
	* @param url
	* @param reqWidth
	* @param reqHeight
	* @return
	* @throws IOException      
	* @return: Bitmap      
	*/  
	private Bitmap loadBitmapFromDiskCache(String url,int reqWidth,int reqHeight) throws IOException{
		if(Looper.myLooper() == Looper.getMainLooper()){
			Log.w(TAG, "load bitmap from UI Thread,it's not recommended!");
		}
		if(mDiskLruCache == null){
			return null;
		}
		
		Bitmap bitmap = null;
		String key = hashKeyFromUrl(url);
		DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
		if(snapShot != null){
			FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
			FileDescriptor fileDescriptor = fileInputStream.getFD();
			bitmap = ImageHelper.decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
			if(bitmap != null){
				addBitmapToMemoryCache(key, bitmap);
			}
		}
		
		return bitmap;
	}
	
	private Bitmap loadBitmapFromMemCache(String url){
		final String key = hashKeyFromUrl(url);
		Bitmap bitmap = getBitmapFromMemCache(key);
		return bitmap;
	}

	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	
	private void addBitmapToMemoryCache(String key,Bitmap bitmap){
		if(getBitmapFromMemCache(key) == null){
			mMemoryCache.put(key, bitmap);
		}
	}

	private File getDiskCacheDir(Context context, String uniqueName) {
		boolean externalStorageAvailable = Environment
				.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		final String cachePath;
		if (externalStorageAvailable) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}

		return new File(cachePath + File.separator + uniqueName);
	}

	private long getUsableSpace(File path) {
		if (Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			return path.getUsableSpace();
		}

		final StatFs stats = new StatFs(path.getPath());
		return stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**   
	* @Title: hashKeyFromUrl   
	* @Description: 字符串转换成hash值
	* @param url
	* @return      
	* @return: String      
	* @throws   
	*/  
	private String hashKeyFromUrl(String url) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("md5");
			mDigest.update(url.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(url.hashCode());
		}

		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xff & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}

		return sb.toString();
	}
	
	private static class LoaderResult{
		public ImageView imageView;
		public String uri;
		public Bitmap bitmap;
		
		public LoaderResult(ImageView imageView,String uri,Bitmap bitmap){
			this.imageView = imageView;
			this.uri = uri;
			this.bitmap = bitmap;
		}
	}
}