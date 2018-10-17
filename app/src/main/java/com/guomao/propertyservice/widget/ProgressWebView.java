package com.guomao.propertyservice.widget;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ZoomButtonsController;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

	private ProgressBar progressbar;

    public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	private void init(Context context) {
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				5, 0, 0));
		addView(progressbar);
		setWebViewClient(new WebViewClient() {
			@SuppressLint("ShowToast")
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
			      if(Build.VERSION.SDK_INT<26) {
			         view.loadUrl(url);
			        return true;
			      }
			     return false;
			}
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				//super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}
		
			@Override
			public void onLoadResource(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onLoadResource(view, url);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}
			
		});
		setWebChromeClient(new WebChromeClient());
		disableControls();
	}
	public ProgressWebView(Context context) {
		super(context);
		init(context);
	}

	public ProgressBar getProgressBar(){
		return progressbar;
	}

	public  class WebChromeClient extends android.webkit.WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(GONE);
			} else {
				if (progressbar.getVisibility() == GONE)
					progressbar.setVisibility(VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
		// public WebView newWebView = null;
		//
		// @Override
		// public boolean onCreateWindow(WebView view, boolean dialog, boolean
		// userGesture, Message resultMsg) {
		// newWebView = new WebView(view.getContext());
		// view.addView(newWebView);
		// WebSettings settings = newWebView.getSettings();
		// settings.setJavaScriptEnabled(true);
		// //���setWebViewClientҪ���ϣ�����window.open����������򿪡�
		// newWebView.setWebViewClient(new WebViewClient());
		// newWebView.setWebChromeClient(this);
		//
		// WebView.WebViewTransport transport = (WebView.WebViewTransport)
		// resultMsg.obj;
		// transport.setWebView(newWebView);
		// resultMsg.sendToTarget();
		//
		// return true;
		// }
		// @Override
		// public void onCloseWindow(WebView view) {
		// if (newWebView != null) {
		// newWebView.setVisibility(View.GONE);
		// //view.removeView(newWebView);
		// }
		// }

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * Disable the controls
	 */
	private void disableControls() {
		WebSettings settings = this.getSettings();
		// 基本的设置
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);// support zoom
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		if (Build.VERSION.SDK_INT >= 8) {
			settings.setPluginState(WebSettings.PluginState.ON);
		}
		this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		// 去掉滚动条
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);

		// 去掉缩放按钮
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			// Use the API 11+ calls to disable the controls
			this.getSettings().setBuiltInZoomControls(true);
			this.getSettings().setDisplayZoomControls(false);
		} else {
			// Use the reflection magic to make it work on earlier APIs
			getControlls();
		}
	}

	/**
	 * This is where the magic happens :D
	 */
	private void getControlls() {
		try {
			Class webview = Class.forName("android.webkit.WebView");
			Method method = webview.getMethod("getZoomButtonsController");
            ZoomButtonsController zoom_controll = (ZoomButtonsController) method.invoke(this, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
	}
	
	

}