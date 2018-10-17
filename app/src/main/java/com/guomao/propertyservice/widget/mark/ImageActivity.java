package com.guomao.propertyservice.widget.mark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.widget.MyProgress;

/**
 * 
 * @author yang.liu
 * 
 *         显示报事图纸的activity，可在其上做双击增加标记操作
 *
 */
public class ImageActivity extends Activity {
	private ZoomImageView imageView;
	private Button btn;
    private File pic_file;
	private static Handler handle;
	private static MyThread myThread;
	public static final int MESSAGE = 0;
	private File path;
	private String areaId;
    private double ppi;
	private int dpi;
	private static float dmDensityDpi = 0.0f;
	private static DisplayMetrics dm;
	private static float scal = 0.0f;
	private String brand;
	private String model;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);
		dpi = getDpi();
		ppi=getPpi();
		brand=android.os.Build.BRAND;
		model=android.os.Build.MODEL;
		Log.e("brand", brand+"--"+model);
		areaId = getIntent().getStringExtra("areaId");
		imageView = (ZoomImageView) findViewById(R.id.zoom_view);
		btn = (Button) findViewById(R.id.btn);
		handle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == ImageActivity.MESSAGE) {
					MyProgress.dismiss();
					Intent intent = new Intent();
					intent.putExtra("imagePath", file.getAbsolutePath());
					ImageActivity.this.setResult(RESULT_OK, intent);
					finish();
				}
			}
		};

		path = new File(DataFolder.getAppDataRoot() + "baoshi_image");
        File image_path = new File(path.getAbsolutePath() + "/" + areaId + ".jpg");
		if (!image_path.exists()) {
			Toast.makeText(this, "不存在该报事图纸", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.e("dpi", dpi + "---"+ppi);
		if(brand.equals("Sony")&&model.contains("L55")){
			imageView.setImageBitmap(compressImageFromFile((path
					.getAbsolutePath() + "/" + areaId + ".png")));
		}
		else if (dpi >320&&ppi<5.4) {
				/*imageView.setImageBitmap(BitmapFactory.decodeFile(path
						.getAbsolutePath() + "/" + areaId + ".jpg"));*/
			imageView.setImageBitmap(compressImageFromFile((path
					.getAbsolutePath() + "/" + areaId + ".jpg")));
			
		} else {
			imageView.setImageBitmap(compressImageFromFile((path
					.getAbsolutePath() + "/" + areaId + ".jpg")));
		}
		/*imageView.setImageBitmap(compressImageFromFile((path.getAbsolutePath()
				+ "/" + areaId + ".png")));*/
		// imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.guomao));
		imageView.isDoubleClick(true);
		// 设置可加标记的图片
		imageView.setMark(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.location));
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!imageView.isLoca()) {
					Toast.makeText(ImageActivity.this, "请先双击添加定位标记后再保存!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				MyProgress.show("正在保存中...", ImageActivity.this);
                // TODO Auto-generated method stub
				Log.e("rate", imageView.getScale() + "--");
				Log.e("rect", imageView.getMatrixRectF().left + "--"
						+ imageView.getMatrixRectF().top);
				myThread = new MyThread();
				myThread.start();

			}
		});
	}

	private File file;

	@SuppressLint("NewApi")
	private double getPpi() {
		 Point point = new Point();  
		    getWindowManager().getDefaultDisplay().getRealSize(point);  
		    DisplayMetrics dm = getResources().getDisplayMetrics();  
		    double x = Math.pow(point.x/ dm.xdpi, 2);  
		    double y = Math.pow(point.y / dm.ydpi, 2);  
		    double screenInches = Math.sqrt(x + y);  
		return screenInches;
	}

	@SuppressWarnings("static-access")
	public Bitmap saveBitMap() {
		Bitmap bmp;
		/*Bitmap bmp=BitmapFactory.decodeFile(
				path.getAbsolutePath() + "/" + areaId + ".png").copy(
				Config.RGB_565, true);*/
		if(brand.equals("Sony")&&model.contains("L55")){
			bmp =compressImageFromFile(path.getAbsolutePath() + "/" + areaId + ".png");
			bmp = bmp.createScaledBitmap(bmp,
					(int) (bmp.getWidth() * imageView.getScale()),
					(int) (bmp.getHeight() * imageView.getScale()), true);
		}
		else if(dpi >320&&ppi<5.4){
			/* bmp=BitmapFactory.decodeFile(
					path.getAbsolutePath() + "/" + areaId + ".jpg").copy(
							Config.RGB_565, true);
			bmp = bmp.createScaledBitmap(bmp,
					(int) (bmp.getWidth() * imageView.getScale()),
					(int) (bmp.getHeight() * imageView.getScale()), true);*/
			bmp =compressImageFromFile(path.getAbsolutePath() + "/" + areaId + ".jpg");
			bmp = bmp.createScaledBitmap(bmp,
					(int) (bmp.getWidth() * imageView.getScale()),
					(int) (bmp.getHeight() * imageView.getScale()), true);
		}else{
			bmp =compressImageFromFile(path.getAbsolutePath() + "/" + areaId + ".jpg");
			bmp = bmp.createScaledBitmap(bmp,
					(int) (bmp.getWidth() * imageView.getScale()),
					(int) (bmp.getHeight() * imageView.getScale()), true);
		}
		
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.location), (imageView.move_x - imageView
				.getMatrixRectF().left), (imageView.move_y - imageView
				.getMatrixRectF().top), null);
		// save all clip
		canvas.save();// 保存
		// store
		canvas.restore();// 存储
		return bmp;
	}
private int getDpi(){
	return getResources().getDisplayMetrics().densityDpi;
}
	class MyThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			// TODO Auto-generated method stub
            Bitmap bitmap = saveBitMap();
			saveMyShot(areaId, bitmap);
			handle.sendEmptyMessage(ImageActivity.MESSAGE);
		}
	}

	public void saveMyShot(String bitName, Bitmap bmp) {

		file = new File(DataFolder.getAppDataRoot() + "/baoshi_save");
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(file + "/" + bitName + ".jpg");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (btn.getVisibility() == View.GONE) {
			btn.setVisibility(View.VISIBLE);
		}
	}

	public float x;
	public float y;
	public float scale;

	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inSampleSize = 2;
		/*
		 * DisplayMetrics displayMetrics =
		 * this.getResources().getDisplayMetrics();
		 * 
		 * newOpts.inDensity = displayMetrics.densityDpi;
		 */
		newOpts.inJustDecodeBounds = false;
		newOpts.inPreferredConfig = Config.RGB_565;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}

	/*public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			Log.e("Sample", inSampleSize + "--");
		}
		return inSampleSize;
	}*/

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handle.removeCallbacksAndMessages(null);
		handle = null;
		myThread = null;
	}
}
