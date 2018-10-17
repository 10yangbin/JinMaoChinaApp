package com.guomao.propertyservice.page;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.javascripinterface.DbObject;
import com.guomao.propertyservice.javascripinterface.ExeObject;
import com.guomao.propertyservice.javascripinterface.IoObject;
import com.guomao.propertyservice.javascripinterface.LongforObject;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.base.BaseActivity;
import com.guomao.propertyservice.widget.ProgressWebView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;


/**
 * 主要应用于LongforObject和MainActivity中和web交互时
 * 优化程度高
 * @author Administrator
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends BaseActivity{
	private ProgressWebView webView;
	private String url;
	private long firstTime;
	public static final int SCAN_BARCODE_REQUEST = 0X101;
	public static final int START_CAMERA_REQUEST = 0X102;
	public static final int GET_DEVICE_INFO_SUCCESS = 0X103;
	public static final int GET_DEVICE_INFO_FAIL = 0X104;
	private Bitmap bitmap = null;
    private DbObject dbObject;
	private ExeObject exeObject;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		initNaviView();
		initData();
		initWebView();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GET_DEVICE_INFO_SUCCESS:
				case GET_DEVICE_INFO_FAIL:
					webView.loadUrl("javascript:scanBarcodeSuccessCallBack('"
							+ msg.obj + "')");
					break;
				}
			}
		};
	}

	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (webView != null && webView.canGoBack()
				&& keyCoder == KeyEvent.KEYCODE_BACK) {
			webView.goBack();
			return true;
		}
		// if (keyCoder == KeyEvent.KEYCODE_BACK) {
		// long secondTime = System.currentTimeMillis();
		// if (secondTime - firstTime > 800) {
		// Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
		// firstTime = secondTime;// 更新firstTime
		// return true;
		// } else {
		// System.exit(0);// 否则退出程序
		// }
		// }
		return super.onKeyDown(keyCoder, event);
	}

	private void initData() {
		url = getIntent().getStringExtra("URL");
	}

	private void initWebView() {
		webView = (ProgressWebView) findViewById(R.id.webView);
		WebSettings ws = webView.getSettings();
		// ws.setSupportMultipleWindows(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
		ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
		ws.setUseWideViewPort(true);// 可任意比例缩放
		ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
		ws.setSaveFormData(true);// 保存表单数据
		ws.setDomStorageEnabled(true);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);

		initJavaScriptInterfaceObj();

		webView.loadUrl(url);

	}

	private void initJavaScriptInterfaceObj() {
        LongforObject longForObject = new LongforObject(webView);
		IoObject ioObject = new IoObject(webView);
		webView.addJavascriptInterface(longForObject, "csq_hw");
		webView.addJavascriptInterface(ioObject, "csq_io");
		webView.addJavascriptInterface(dbObject, "csq_db");
		webView.addJavascriptInterface(exeObject, "csq_exe");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == START_CAMERA_REQUEST && resultCode == RESULT_OK) {

            String picPath = "file://" + LongforObject.MPHOTOPATH;
			webView.loadUrl("javascript:pictureCallback('" + picPath + "')");
			// }
		} else if (requestCode == SCAN_BARCODE_REQUEST
				&& resultCode == RESULT_OK) {
			if (data != null) {
				// String result = data.getStringExtra("result");
				// webView.loadUrl("javascript:scanBarcodeSuccessCallBack('"
				// + result + "')");
				final String result = data.getStringExtra("result");
				new Thread() {
					public void run() {
						UserBiz biz = (UserBiz) BizManger.getInstance().get(
								BizType.USER_BIZ);
						Message msg = Message.obtain();
						try {
							JSONObject resJson = new JSONObject(
									biz.getDeviceInfo(result));
							resJson.put("id", result);
							msg.obj = resJson.toString();
							msg.what = WebViewActivity.GET_DEVICE_INFO_SUCCESS;
						} catch (Exception e) {
							L.printStackTrace(e);
							JSONObject obj = new JSONObject();
							try {
								obj.put("id", result);
								if (e instanceof JSONException) {
									obj.put("ERROR", "查询结果失败");
								} else {
									obj.put("ERROR", e.getMessage());
								}
								msg.obj = obj.toString();
							} catch (JSONException e1) {
							}
							msg.what = WebViewActivity.GET_DEVICE_INFO_FAIL;
						}
						handler.sendMessage(msg);
					}
                }.start();
			}
		}

	}

	@Override
	public void onLeftButtonClick(View v) {
		if(webView.canGoBack()){
			webView.goBack();
		}else{
			onBackPressed();
		}
	}

	@Override
	public void onRightButtonClick(View v) {

	}
}
