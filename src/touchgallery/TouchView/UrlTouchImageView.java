package touchgallery.TouchView;

/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gesturalpicture.R;

public class UrlTouchImageView extends RelativeLayout {
	protected ProgressBar mProgressBar;
	protected TouchImageView mImageView;
	private PopupWindow popupWindow;

	protected Context mContext;

	public UrlTouchImageView(Context ctx) {
		super(ctx);
		mContext = ctx;
		init();

	}

	public UrlTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	
	@SuppressWarnings("deprecation")
	protected void init() {
		mImageView = new TouchImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mImageView.setLayoutParams(params);
		this.addView(mImageView);
		mImageView.setVisibility(GONE);

		// --------------------------------------------------------------
		
		mImageView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				// 从底部弹出选项框
				View popupWindow_view = LayoutInflater.from(mContext.getApplicationContext())
						.inflate(R.layout.dialog_savepic, null, false);
				Button save = (Button) popupWindow_view.findViewById(R.id.save);
				Button cancel = (Button) popupWindow_view.findViewById(R.id.cancel);

				// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
				popupWindow = new PopupWindow(popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, true);
				// 设置动画效果
				popupWindow.setAnimationStyle(R.style.save_style);
				// 这里是位置显示方式,在屏幕的左侧
				popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

				save.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mImageView.setDrawingCacheEnabled(true);
						Bitmap imageBitmap = mImageView.getDrawingCache();
						if (imageBitmap != null) {
							new SaveImageTask().execute(imageBitmap);
							popupWindow.dismiss();
						}
					}
				});
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}
				});
				return true;
			}
		});
		// --------------------------------------------------------------

		mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
		params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(30, 0, 30, 0);
		mProgressBar.setLayoutParams(params);
		mProgressBar.setIndeterminate(false);
		mProgressBar.setMax(100);
		this.addView(mProgressBar);
	}

	public void setUrl(String imageUrl) {
		new ImageLoadTask().execute(imageUrl);
	}

	public void setScaleType(ScaleType scaleType) {
		mImageView.setScaleType(scaleType);
	}

	public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... strings) {
			String url = strings[0];
			Bitmap bm = null;
			try {
				URL aURL = new URL(url);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				int totalLen = conn.getContentLength();
				InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
				bis.setProgressListener(new InputStreamWrapper.InputStreamProgressListener() {
					@Override
					public void onProgress(float progressValue, long bytesLoaded, long bytesTotal) {
						publishProgress((int) (progressValue * 100));
					}
				});
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inTempStorage = new byte[100 * 1024];
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;

				options.inJustDecodeBounds = true;
				options.inSampleSize = calculateInSampleSize(options, 480, 800);
				options.inJustDecodeBounds = false;
				bm = BitmapFactory.decodeStream(bis, null, options);
				bis.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap == null) {
				mImageView.setScaleType(ScaleType.CENTER);
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				mImageView.setImageBitmap(bitmap);
			} else {
				mImageView.setScaleType(ScaleType.MATRIX);
				mImageView.setImageBitmap(bitmap);
			}
			mImageView.setVisibility(VISIBLE);
			mProgressBar.setVisibility(GONE);
		}

		//计算图片的缩放值
		public int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				final int heightRatio = Math.round((float) height/ (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
			return inSampleSize;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}
	}

	// 图片保存
	private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
		@Override
		protected String doInBackground(Bitmap... params) {
			String result = "保存失败";
			try {
				String sdcard = Environment.getExternalStorageDirectory().toString();

				File file = new File(sdcard + "/Download");
				if (!file.exists()) {
					file.mkdirs();
				}

				File imageFile = new File(file.getAbsolutePath(), new Date().getTime() + ".jpg");
				FileOutputStream outStream = null;
				outStream = new FileOutputStream(imageFile);
				Bitmap image = params[0];
				image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				result = "图片成功保存至"+file.getAbsolutePath()+"目录";
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(mContext.getApplicationContext(), result, Toast.LENGTH_SHORT).show();

			mImageView.setDrawingCacheEnabled(false);
		}
	}
}
