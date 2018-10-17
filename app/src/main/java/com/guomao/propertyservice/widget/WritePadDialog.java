package com.guomao.propertyservice.widget;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.widget.sign.DialogListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/*
 * 手写签名板
 */
public class WritePadDialog extends Dialog {
	Context context;
	LayoutParams p;
	DialogListener dialogListener;
	float price;
	int selection;
	int editable;

	public WritePadDialog(Context context, float price, int selection,
			int editable, DialogListener dialogListener) {
		super(context);
		this.context = context;
		this.dialogListener = dialogListener;
		this.price = price;
		this.selection = selection;
		this.editable = editable;
	}

	static final int BACKGROUND_COLOR = Color.WHITE;
	static final int BRUSH_COLOR = Color.BLACK;
	PaintView mView;
	TextView priceView;
	RadioGroup group;
	RadioButton c_0, c_1, c_2;
	int select = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_PROGRESS);
			setContentView(R.layout.write_pad);
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			p = getWindow().getAttributes();
			p.height = display.getHeight();
			p.width = display.getWidth();
			getWindow().setAttributes(p);

			mView = new PaintView(context);
			priceView = (TextView) findViewById(R.id.textView_price);
		/*	group = (RadioGroup) findViewById(R.id.radioGroup);
			c_0 = (RadioButton) findViewById(R.id.c_0);
			c_1 = (RadioButton) findViewById(R.id.c_1);
			c_2 = (RadioButton) findViewById(R.id.c_2);*/
			select = selection;
			switch (selection) {
				case 0:
					c_0.setChecked(true);
					break;
				case 1:
					c_1.setChecked(true);
					break;
				case 2:
					c_2.setChecked(true);
					break;
			}
			if (editable == 0) {
				c_0.setEnabled(false);
				c_1.setEnabled(false);
				c_2.setEnabled(false);
			}
			if (price != 0) {
				priceView.setText("收费金额:" + price + "元");
			} else {
				priceView.setVisibility(View.INVISIBLE);
			}
			FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
			frameLayout.addView(mView);
			mView.requestFocus();

		/*	group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
						Bitmap photo = mView.getCachebBitmap();
						WritePadDialog.this.dismiss();
						dialogListener.refreshActivity(photo, select);
					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			});
			Button btnCancel = (Button) findViewById(R.id.tablet_cancel);
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Bitmap cachebBitmap;
		private Path path;

		public Bitmap getCachebBitmap() {
			return cachebBitmap;
		}

		public PaintView(Context context) {
			super(context);
			init();
		}

		private void init() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			path = new Path();
			cachebBitmap = Bitmap.createBitmap(p.width, (int) (p.height * 0.8),
					Config.ARGB_8888);
			cacheCanvas = new Canvas(cachebBitmap);
			cacheCanvas.drawColor(Color.WHITE);
		}

		public void clear() {
			if (cacheCanvas != null) {
				paint.setColor(BACKGROUND_COLOR);
				cacheCanvas.drawPaint(paint);
				paint.setColor(Color.BLACK);
				cacheCanvas.drawColor(Color.WHITE);
				invalidate();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(cachebBitmap, 0, 0, null);
			canvas.drawPath(path, paint);
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
					Bitmap.Config.ARGB_8888);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (cachebBitmap != null) {
				newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
			}
			cachebBitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private float cur_x, cur_y;

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
}