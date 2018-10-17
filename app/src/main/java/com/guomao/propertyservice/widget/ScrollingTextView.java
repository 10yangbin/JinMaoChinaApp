package com.guomao.propertyservice.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

public class ScrollingTextView extends android.support.v7.widget.AppCompatTextView {

	public ScrollingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ScrollingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScrollingTextView(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
		if (focused)
			super.onWindowFocusChanged(focused);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

	// add by laomo
	private void init() {
		setEllipsize(TruncateAt.MARQUEE);// 对应android:ellipsize="marquee"
		setMarqueeRepeatLimit(-1);// 对应android:marqueeRepeatLimit="marquee_forever"
		setSingleLine();// 等价于setSingleLine(true）
	}
}
