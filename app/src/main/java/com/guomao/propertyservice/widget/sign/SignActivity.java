package com.guomao.propertyservice.widget.sign;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 手写签名的主体界面 ,始发于LongforObject,结束于LongforObject,最后回调web方法
 * @author Administrator
 *
 */
public class SignActivity extends Activity {

	static final int BACKGROUND_COLOR = Color.WHITE;
	static final int BRUSH_COLOR = Color.BLACK;
	static DialogListener dialogListener;
	PaintView mView;
	TextView priceView;
	RadioGroup group;
	RadioButton c_0, c_1, c_2;
	private int select = -1;
    private String price;
	private boolean isSign = false;
	private String orderId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_pad);
		initData();
		initView();
	}


	private void initView() {
		mView = new PaintView(this);
		priceView = (TextView) findViewById(R.id.textView_price);
	/*	group = (RadioGroup) findViewById(R.id.radioGroup);
		c_0 = (RadioButton) findViewById(R.id.c_0);
		c_1 = (RadioButton) findViewById(R.id.c_1);
		c_2 = (RadioButton) findViewById(R.id.c_2);
		if (select == 0) {
			c_0.setChecked(true);
		} else if (select == 1) {
			c_1.setChecked(true);
		} else if (select == 2) {
			c_2.setChecked(true);
		}
		if (editable == 0) {
			c_0.setEnabled(false);
			c_1.setEnabled(false);
			c_2.setEnabled(false);
		}*/
		if (price == "0") {
			priceView.setVisibility(View.INVISIBLE);
		} else {
			priceView.setText("收费金额:" + price + "元");
		}
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
		frameLayout.addView(mView);
		mView.requestFocus();

		/*group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.c_0:
					select = 0;
					break;
				case R.id.c_1:
					select = 1;
					break;
				case R.id.c_2:
					select = 2;
					break;
				default:
					break;
				}
			}
		});*/

		Button btnClear = (Button) findViewById(R.id.tablet_clear);
		btnClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mView.clear();
			}
		});
		Button btnOk = (Button) findViewById(R.id.tablet_ok);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if(isSign){
						Bitmap photo = mView.getCacheBitmap();
						if(dialogListener !=null)
							dialogListener.refreshActivity(photo, select);
						finish();
					}else{
						Toast.makeText(SignActivity.this, "您还没有签名", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Button btnCancel = (Button) findViewById(R.id.tablet_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


	}


	private void initData() {

		Intent intent =getIntent();
		select = intent.getIntExtra("selection", 0);
        int editable = intent.getIntExtra("editable", 0);
		price = intent.getStringExtra("price");
		orderId = intent.getStringExtra("orderId");
	}

	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Bitmap cachebBitmap;
		private Path path;
		boolean flag;
		private Paint mTextPaint;
		private int mTextColor = Color.GRAY;
		private int mWaterMarkMarginRight;
		private int mWaterMarkMarginBottom = mWaterMarkMarginRight = 10;
		private float textWidth;
		private int textCenterX;
		private int textBaselineY;

		public Bitmap getCacheBitmap() {
			return cachebBitmap;
		}

		public PaintView(Context context) {
			super(context);
			init();
			getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					PaintView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					Bitmap lastSignBitmap = null;
					if(!StringUtil.isNull(orderId)){
						/*try {
							String path = new StringBuilder(DataFolder.getAppDataRoot()).append("cache/images/sign_").append(orderId).append(".png").toString();
							File lastBitmap = new File(path);
							if(lastBitmap.exists()){
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.outWidth = getWidth();
								options.outHeight = getHeight();
								options.inPreferredConfig =Config.RGB_565;
								lastSignBitmap = BitmapFactory.decodeFile(path,options);
							}
						} catch (Exception e) {
						}*/
					}
					cachebBitmap = Bitmap.createBitmap(getWidth(),getHeight(),
							Config.RGB_565);
					cacheCanvas = new Canvas(cachebBitmap);
					cacheCanvas.drawColor(Color.WHITE);
					if(lastSignBitmap!=null){
						cacheCanvas.drawBitmap(lastSignBitmap, 0, 0, paint);
						isSign = true;
					}else{
						drawWatermark(cacheCanvas);
					}

					if(lastSignBitmap != null && !lastSignBitmap.isRecycled()){
						lastSignBitmap.recycle();
					}
				}
			});
		}

		private void init() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(10);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			path = new Path();
			mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
			mTextPaint.setAntiAlias(true);
			mTextPaint.setColor(mTextColor);
			mTextPaint.setTextSize(20);
			mTextPaint.setDither(true); //获取跟清晰的图像采样  
			mTextPaint.setFilterBitmap(true);//过滤一些 
		}

		public void clear() {
			if (cacheCanvas != null) {
				paint.setColor(BACKGROUND_COLOR);
				cacheCanvas.drawPaint(paint);
				paint.setColor(Color.BLACK);
				cacheCanvas.drawColor(Color.WHITE);
				drawWatermark(cacheCanvas);
				if(isSign){
					isSign = false;
				}
				invalidate();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {

			canvas.drawBitmap(cachebBitmap, 0, 0, null);
			canvas.drawPath(path, paint);
		}

		private void drawWatermark(Canvas canvas) {
			if(!StringUtil.isNull(orderId)){
				String signText = "任务编号：".concat(orderId);
				if(textWidth<=0){
					FontMetrics fm = mTextPaint.getFontMetrics();
					textWidth = mTextPaint.measureText(signText);
					textCenterX = (int) (getWidth() - textWidth/2 - mWaterMarkMarginRight);
					textBaselineY = (int) (getHeight() - fm.bottom - mWaterMarkMarginBottom); 
					mTextPaint.setTextAlign(Align.CENTER);
				}
				canvas.drawText(signText, textCenterX,textBaselineY,mTextPaint);
			}

		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {

			int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
			int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}

			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;

			Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
					Bitmap.Config.RGB_565);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (cachebBitmap != null && !cachebBitmap.isRecycled())  {
				drawWatermark(newCanvas);
				newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
			}
			cachebBitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private float cur_x, cur_y;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event) {

			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				cur_x = x;
				cur_y = y;
				path.moveTo(cur_x, cur_y);
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				path.quadTo(cur_x, cur_y, x, y);
				cur_x = x;
				cur_y = y;
				//if(Math.abs(x - cur_x)>3||Math.abs(y - cur_y)>3)
				isSign = true;
				break;
			}

			case MotionEvent.ACTION_UP: {
				cacheCanvas.drawPath(path, paint);
				path.reset();
				break;
			}
			}

			invalidate();

			return true;
		}
	}

	public static void setDialogListener(DialogListener dialogListener){
		SignActivity.dialogListener = dialogListener;
	}

	@Override
	public void finish() {
		super.finish();
		dialogListener = null;
	}
}
