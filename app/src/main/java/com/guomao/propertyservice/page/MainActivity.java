package com.guomao.propertyservice.page;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.adapter.SiteAdapter;
import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.callback.SiteCheckedCallback;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.eventbus.OnChangeSite;
import com.guomao.propertyservice.eventbus.OnLogoutByOtherUser;
import com.guomao.propertyservice.eventbus.OnOfflineUploadSucc;
import com.guomao.propertyservice.eventbus.OnPushOtherSite;
import com.guomao.propertyservice.eventbus.OnXgClickEvent;
import com.guomao.propertyservice.javascripinterface.DbDownLoadObject;
import com.guomao.propertyservice.javascripinterface.DbObject;
import com.guomao.propertyservice.javascripinterface.ExeObject;
import com.guomao.propertyservice.javascripinterface.FileUpload;
import com.guomao.propertyservice.javascripinterface.IoObject;
import com.guomao.propertyservice.javascripinterface.LongforObject;
import com.guomao.propertyservice.widget.listener.ShakeListener;
import com.guomao.propertyservice.widget.listener.ShakeListener.OnShakeListener;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.Site;
import com.guomao.propertyservice.mydao.AppAction;
import com.guomao.propertyservice.mydaoimpl.AppActionImpl;
import com.guomao.propertyservice.widget.tel.TelReceiver;
import com.guomao.propertyservice.util.AnimationUtil;
import com.guomao.propertyservice.util.BitmapUtil;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.DensityUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.guomao.propertyservice.widget.MyProgress;
import com.guomao.propertyservice.widget.ProgressWebView;
import com.jinmaochina.propertyservice.BuildConfig;
import com.jinmaochina.propertyservice.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity {

	private ProgressWebView webView;
	public static MainActivity mainActivity;
    private String url;
	private String returnKeyNotiece;
	private String filePath;
	private DownLoadSuccessReceiver receiver;
	private NetworkReceiver networkReceiver;
    private Dialog netDialog;
	private UserBiz ubiz;
	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	String params = "";
	SiteAdapter siteAdapter = null;
	List<Site> siteList = new ArrayList<Site>();
	private LongforObject obj;
	NotificationManager nm;
	private LinearLayout ll;
	private boolean needCallBackAfterRestart = false;
	private TelReceiver telReceiver = new TelReceiver();
	private IntentFilter telFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
    private String pahts ;

    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mainActivity = this;
		//setContentView(R.layout.webview);
		ll = new LinearLayout(getApplicationContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		webView = new ProgressWebView(this);
		initData();
		initWebView(savedInstanceState);

		EventBus.getDefault().register(this);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WebViewActivity.GET_DEVICE_INFO_SUCCESS:
                    case WebViewActivity.GET_DEVICE_INFO_FAIL:
                        webView.loadUrl("javascript:scanBarcodeSuccessCallBack('"
                                + msg.obj + "')");
                        break;
                }
            }
        };
		//startPush(getApplicationContext());
		registReciver();
		registeNetReceiver();
		//		if(savedInstanceState != null){
		//			String url = savedInstanceState.getString("URL");
		//			webView.loadUrl(url);
		//		}
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		AppAction appAction = new AppActionImpl();
		appAction.checkUpdate(MainActivity.this);
	}

	private void initView() {
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				String currentUrl = webView.getUrl();
				if (currentUrl.indexOf("index.html") > 0
						|| currentUrl.indexOf("gongdan_list.html") > 0
						|| currentUrl.indexOf("offline_business.html") > 0) {
					// startAnim(); //开始 摇一摇手掌动画
					mShakeListener.stop();
					startVibrato(); // 开始 震动
					AnimationUtil
					.anim(MainActivity.this, webView, R.anim.shake);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							DbDialog.getInstance(true).showdbListDialog(
									MainActivity.this,
									new SiteCheckedCallback() {

										@Override
										public void OnChecked(String siteId,
												String isMonitor) {

										}

										@Override
										public void OnCancel() {

										}
									});
							mVibrator.cancel();
							mShakeListener.start();
						}
					}, 1000);
				}
			}
		});
	}

	public void startVibrato() { // 定义震动
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, 2); // 第一个｛｝里面是节奏数组，
		// 第二个参数是重复次数，-1为不重复，非-1俄
		// 日从pattern的指定下标开始重复
	}

	private void registReciver() {
		receiver = new DownLoadSuccessReceiver();
        IntentFilter filter = new IntentFilter(Const.ACTION_DATA_DOWNLOAD_SUCCESS);
		filter.addAction(Const.ACTION_DATA_DOWNLOAD_START);
		filter.addAction(Const.ACTION_EXEC_START);
		filter.addAction(Const.ACTION_EXEC_END);
		filter.addAction(Const.ACTION_START_PUSH_SERVICE);
		filter.addAction(Const.ACTION_NETWORK_NORMAL);
		filter.addAction(Const.ACTION_NETWORK_ERROR);
		filter.addAction(Const.ACTION_USER_LOGOUT_START);
		filter.addAction(Const.ACTION_USER_LOGOUT_END);
		filter.addAction(Const.ACTION_OFFLINE_PER_UPLOAD_START);
		filter.addAction(Const.ACTION_OFFLINE_PER_UPLOAD_SUCC);
		filter.addAction(Const.ACTION_RECODER_END);
		registerReceiver(receiver, filter);
	}

	private void registeNetReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		networkReceiver = new NetworkReceiver();
		this.registerReceiver(networkReceiver, filter);
	}

	private void initData() {
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		Intent intent = getIntent();
		url = intent.getStringExtra("URL");
		returnKeyNotiece = intent.getStringExtra("ReturnKeyNotiece");
		NoticeKeyReturn();
		filePath = intent.getStringExtra("FILEPATH");
	}

	private void NoticeKeyReturn() {
		if(returnKeyNotiece!=null){
			//Toast.makeText(mainActivity, returnKeyNotiece, 10000);
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setMessage(returnKeyNotiece);
			builder.setTitle("归还钥匙");
			builder.setCancelable(true);
			builder.setNegativeButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.create().show();
			System.out.println(returnKeyNotiece);
		}
	}


	private void initWebView(Bundle savedInstanceState) {
		WebSettings ws = webView.getSettings();
		// ws.setSupportMultipleWindows(true);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
		ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
		ws.setUseWideViewPort(true);// 可任意比例缩放
		ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
		ws.setSaveFormData(true);// 保存表单数据
		ws.setDomStorageEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_DEFAULT);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);

		webView.addJavascriptInterface(this, "longformain");
		obj = new LongforObject(webView);
		// 添加js调用android方法的功能和约定
		DbObject dbobj = new DbObject(webView);
		ExeObject execobj = new ExeObject(webView);
		IoObject ioObject = new IoObject(webView);
		FileUpload upobj = new FileUpload(webView);
		DbDownLoadObject dbdownObject = new DbDownLoadObject(webView);
		webView.addJavascriptInterface(ioObject, "csq_io");
		webView.addJavascriptInterface(execobj, "csq_exe");
		webView.addJavascriptInterface(dbobj, "csq_db");
		webView.addJavascriptInterface(obj, "csq_hw");
		webView.addJavascriptInterface(dbdownObject, "csq_data");
		webView.addJavascriptInterface(upobj, "csq_fileup");
		webView.setWebViewClient(new MyWebClient());
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				return false;
			}
		});
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		ll.addView(webView,lp);
		setContentView(ll);
		// 控制是否进主页更名
		if (StringUtil.isNull(url)) {
			// Intent upDateIntent = new Intent(this, UpdateDataService.class);
			// Intent operateIntent = new Intent(this,
			// SendOfflineDataService.class);
			// startService(upDateIntent);
			// startService(operateIntent);
			// Intent OfflineDataIntent = new Intent(this,
			// OfflineData2Service.class);
			// startService(OfflineDataIntent);
		}
		if (StringUtil.isNull(url)) {
			url = "file:///android_asset/Web/index.html";
		}
		//url = "file:///android_asset/Web/beizhu.html";
		if(null != savedInstanceState){
			webView.restoreState(savedInstanceState);
		}else{
			webView.loadUrl(url
					+ "?user_id="
					+ ((UserBiz) BizManger.getInstance().get(BizType.USER_BIZ))
					.getUserId()
					+ (StringUtil.isNull(filePath) ? ""
							: ("&file_path=" + filePath)));
		}

		// webView.loadUrl("file://"+DataFolder.getAppDataRoot()+"fm/test.html");
	}



	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (keyCoder == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if (webView != null && keyCoder == KeyEvent.KEYCODE_BACK) {
			String currentUrl = webView.getUrl();
			if (currentUrl.endsWith(".jpeg") || currentUrl.endsWith(".png")
					|| currentUrl.endsWith(".jpg")) {
				webView.goBack();
			} else if (currentUrl.indexOf("index.html") > 0) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.HOME");
				startActivity(intent);
			} else {
				if(!webView.getUrl().startsWith("file://")){
					webView.goBack();
				}else {
					webView.loadUrl("javascript:backCallBack()");
				}
			}

			return true;
		}

		return super.onKeyDown(keyCoder, event);
	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (requestCode == WebViewActivity.START_CAMERA_REQUEST
				&& resultCode == RESULT_OK) {
			try {
				if(StringUtil.isNull(LongforObject.MPHOTOPATH)){
					LongforObject.MPHOTOPATH = SharedPrefUtil.getMsg("CAMER_PHOTO_PATH", "CAMER_PHOTO_PATH");
				}
				File file = new File(LongforObject.MPHOTOPATH);
				if (file.exists()
						&& !StringUtil.isNull(LongforObject.MPHOTOPATH)) {
					/*try {
						compressImageFromPath(LongforObject.MPHOTOPATH);
					} catch (Exception e) {
						L.printStackTrace(e);
					}
					this.picPath = "file://" + LongforObject.MPHOTOPATH;
					webView.loadUrl("javascript:pictureCallback('" + picPath
							+ "')");*/
					compressImageFromPath(LongforObject.MPHOTOPATH);
                    String picPath = "file://" + LongforObject.MPHOTOPATH;
					bitmap =BitmapFactory.decodeFile(LongforObject.MPHOTOPATH);
					if(bitmap !=null){
						LongforObject.MPHOTOPATH = addWaterMarking(bitmap, LongforObject.MPHOTOPATH);
					}
					webView.loadUrl("javascript:pictureCallback('" + LongforObject.MPHOTOPATH
							+ "')");
				} else {
					Toast.makeText(this, "无法加载文件,请重试", Toast.LENGTH_SHORT)
					.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "拍照失败,请重试", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == WebViewActivity.SCAN_BARCODE_REQUEST
				&& resultCode == RESULT_OK) {
			if (data != null) {
				final String result = data.getStringExtra("result");
				Toast.makeText(this, result, Toast.LENGTH_SHORT).show(); // by
				if (StringUtil.isNull(result)) {
					Toast.makeText(MainActivity.this, "扫码获取结果失败",
							Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					JSONObject obj = new JSONObject();
					obj.put("id", result);
					if (webView != null) {
						webView.loadUrl("javascript:scanBarcodeSuccessCallBack('"
								+ obj.toString() + "')");
					}
				} catch (JSONException e) {
					Toast.makeText(MainActivity.this, "扫码获取结果失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		else if(requestCode==LongforObject.PIC_RESQUEST&&resultCode == RESULT_OK){
			//在报事图片加完标记后的回调
            String imagePath = data.getStringExtra("imagePath");
			webView.loadUrl("javascript:onDrawingBack('" + imagePath + "')");
		}
		
		else if(requestCode==LongforObject.SELECT_SAVE_PIC &&resultCode == RESULT_OK){
			//在报事图片加完标记后的回调

            ArrayList<String> selectPicPaths = data.getStringArrayListExtra("dataList");
			if(selectPicPaths.size()>0){
			   for (int i = 0; i< selectPicPaths.size(); i++){
				   pahts = selectPicPaths.get(i);
				   if (!StringUtil.isNull(pahts)){
						compressImageFromPath(pahts);
					}
			   }
			}
			bitmap =BitmapFactory.decodeFile(pahts);
			if(bitmap !=null){
				pahts = addWaterMarking(bitmap, pahts);
			}
			webView.loadUrl("javascript:pictureCallback('" + pahts + "')");
		}
	}

	public void compressImageFromPath(String path) {
		if (path == null && StringUtil.isNull(path)) {
			throw (new IllegalArgumentException("path may not be null"));
		}
		byte[] bytes = BitmapUtil.compressImage2Byte(path, 200);
		File file = new File(path);
		if (bytes != null) {
			OutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "ISTAKEPHOTO", null);
		SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "CAMER_PHOTO_PATH", null);
	}
	//
	// @Override
	// protected void onStop() {
	// super.onStop();
	// if(webView!=null){
	// CURRENT_URL = webView.getUrl();
	// }
	// }

	@Override
	protected void onStart() {

		// Intent intent = new Intent(MainActivity.this, FloatViewService.class)
		// .putExtra("siteName", ubiz.getCurrentUser().getSite_id());
		// // 启动FloatViewService
		// startService(intent);
		super.onStart();
		//此时说明拍照时被系统杀死
		if("true".equals(SharedPrefUtil.getMsg("CAMER_PHOTO_PATH", "ISTAKEPHOTO"))){
			needCallBackAfterRestart = true;
		}
		if("true".equals(SharedPrefUtil.getMsg("RECODER_PATH", "ISRECODER"))){
			needCallBackAfterRestart = true;
		}
        long startTime = System.currentTimeMillis();
	}

	// @Override
	// protected void onStop() {
	// // // 销毁悬浮窗
	// // Intent intent = new Intent(MainActivity.this,
	// // FloatViewService.class);
	// // // 终止FloatViewService
	// // stopService(intent);
	// // super.onStop();
	// }

	private class DownLoadSuccessReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (webView != null) {
				if (Const.ACTION_DATA_DOWNLOAD_START.equals(action)) {
					MyProgress.show("正在更新数据...", MainActivity.this, true);
				} else if (Const.ACTION_DATA_DOWNLOAD_SUCCESS.equals(action)) {
					MyProgress.dismiss();
					webView.loadUrl("javascript:LoadServerDataSuccess();");
				} else if (Const.ACTION_EXEC_START.equals(action)) {
					MyProgress.show("执行中,请稍候...", MainActivity.this);
				} else if (Const.ACTION_EXEC_END.equals(action)) {
					MyProgress.dismiss();
					webView.loadUrl("javascript:LoadServerDataSuccess();");
				} else if (Const.ACTION_OFFLINE_PER_UPLOAD_START.equals(action)) {
					MyProgress.show("执行中,请稍候...", MainActivity.this);
				} else if (Const.ACTION_OFFLINE_PER_UPLOAD_SUCC.equals(action)) {
					MyProgress.dismiss();
				} else if (Const.ACTION_START_PUSH_SERVICE.equals(action)) {
					MainApplication.IS_NEED_START_PUSH = false;
				} else if (Const.ACTION_NetworkImpl_USER_NULL.equals(action)) {
					dialog(MainActivity.this);
				} else if (Const.ACTION_NETWORK_NORMAL.equals(action)) {
					dismissNetErrorDialog();
				} else if (Const.ACTION_NETWORK_ERROR.equals(action)) {
					showNetErrorDialog(context);
				} else if (Const.ACTION_USER_LOGOUT_START.equals(action)) {
					MyProgress.show("正在注销用户", MainActivity.this, true);
				} else if (Const.ACTION_USER_LOGOUT_END.equals(action)) {
					MyProgress.dismiss();
				}else if(Const.ACTION_RECODER_END.equals(action)){
					if(webView != null){
						String path = "file://" + SharedPrefUtil.getMsg("RECODER_PATH", "RECODER_PATH");
						webView.loadUrl("javascript:TelphoneCallback('"+path+"');");	
						SharedPrefUtil.setMsg("RECODER_PATH", "ISRECODER", null);
						SharedPrefUtil.setMsg("RECODER_PATH", "RECODER_PATH", path);
					}
					unRegisterPhoneStateReceiver();
				}
			}
		}

	}

	private class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent arg1) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				if (netDialog != null && !netDialog.isShowing()) {
					showNetErrorDialog(context);
				}
			} else {
				dismissNetErrorDialog();
			}

		}

	}

	/**
	 * 继承WebChromeClient类 对js弹出框时间进行处理
	 * 
	 */
	final class MyWebChromeClient extends WebChromeClient {
		private ProgressBar progressbar;
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(view instanceof ProgressWebView && progressbar == null){
				progressbar = ((ProgressWebView) view).getProgressBar();
			}
			if(progressbar != null){
				if (newProgress == 100) {
					progressbar.setVisibility(View.GONE);
				} else {
					if (progressbar.getVisibility() == View.GONE)
						progressbar.setVisibility(View.VISIBLE);
					progressbar.setProgress(newProgress);
				}
			}
			super.onProgressChanged(view, newProgress);
		}

		/**
		 * 处理alert弹出框
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			String titleText = null;
			String messageText = message;
			String positiveButtonText = null;
			String negativeButtonText = null;
			if (message != null && message.contains("|")) {
				String[] cr = message.split("|");
				if (cr.length == 4) {
					titleText = cr[0];
					message = cr[1];
					positiveButtonText = cr[2];
					negativeButtonText = cr[3];
				}
			} else {
				titleText = "提示";
				messageText = message;
				positiveButtonText = "确认";
				negativeButtonText = "取消";
			}

			// 对alert的简单封装
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(titleText)
			.setMessage(messageText)
			.setCancelable(false)
			.setNegativeButton(negativeButtonText,
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0,
						int arg1) {
					result.cancel();
				}
			})
			.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0,
						int arg1) {
					result.confirm();
				}
			}).create().show();
			return true;
		}


		/**
		 * 处理confirm弹出框
		 */
		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			String titleText = null;
			String messageText = message;
			String positiveButtonText = null;
			String negativeButtonText = null;
			if (message != null && message.contains("|")) {
				String[] cr = message.split("\\|");
				if (cr.length == 4) {
					titleText = cr[0];
					messageText = cr[1];
					positiveButtonText = cr[2];
					negativeButtonText = cr[3];
				}
			} else {
				titleText = "提示";
				messageText = message;
				positiveButtonText = "确认";
				negativeButtonText = "取消";
			}
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(titleText)
			.setMessage(messageText)
			.setCancelable(false)
			.setNegativeButton(negativeButtonText,
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0,
						int arg1) {
					result.cancel();
				}
			})
			.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0,
						int arg1) {
					result.confirm();
				}
			}).create().show();
			return true;
		}

	}

	final class MyWebClient extends WebViewClient {
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@SuppressLint("ShowToast")
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
	}

	@Override
	protected void onDestroy() {
		mainActivity = null;
		super.onDestroy();
		try {

			if (DbDialog.isDbListDialogShown) {
				DbDialog.isDbListDialogShown = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		if (networkReceiver != null) {
			unregisterReceiver(networkReceiver);
		}
		if(webView != null){
			ll.removeAllViews();
			webView.stopLoading();

			webView.removeAllViews();

			webView.destroy();

			webView = null;

			ll = null;
		}
		releaseAllWebViewCallback();
		super.onDestroy();
	}

	private void showNetErrorDialog(final Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("网络状况发生异常!");
		builder.setTitle("提示");
		builder.setPositiveButton("检查设置", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = null;
				// 判断手机系统的版本 即API大于10 就是3.0或以上版本
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
			}

		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();
			}
		});
		netDialog = builder.create();
		netDialog.show();
	}

	public void dismissNetErrorDialog() {
		if (netDialog != null && netDialog.isShowing()) {
			netDialog.dismiss();
		}
	}

	private void dialog(final Context context) {

		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("用户信息发生异常,请从新登录!");
		builder.setTitle("提示");
		builder.setCancelable(false);
		builder.setPositiveButton("重新登录", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
				ubiz.getDao().setCurrentUser(null);
				MainActivity.this.finish();
			}

		});
		builder.create().show();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// 必须要调用这句
		returnKeyNotiece = intent.getStringExtra("ReturnKeyNotiece");
		NoticeKeyReturn();
	}

	@Subscribe
	public void onEvent(OnXgClickEvent onXgClickEvent) {
		webView.loadUrl(onXgClickEvent.getUrl());
	}

	@Subscribe
	public void onEvent(OnPushOtherSite onPushOtherSite) {
		DbDialog.getInstance(true).showdbListDialog(MainActivity.this,
				new SiteCheckedCallback() {

			@Override
			public void OnChecked(String siteId, String isMonitor) {

			}

			@Override
			public void OnCancel() {

			}
		});
	}

	@Subscribe
	public void onEvent(OnLogoutByOtherUser onLogoutByOtherUser) {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
		MainActivity.this.finish();
	}

	@Subscribe
	public void onEvent(OnChangeSite onChangeSite) {
		ubiz.getCurrentUser().setSite_id(onChangeSite.getSite_id());
		//		ubiz.getCurrentUser().setIs_monitor(onChangeSite.getIsMonitor());
		ubiz.getDao().setCurrentUser(ubiz.getCurrentUser());
		IoObject.DB_NAME = null;
		try {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						webView.reload();
					} catch (Exception e) {
					}
					// String currentUrl = webView.getUrl();
					// if(currentUrl.indexOf("index.html")>0 ||
					// currentUrl.indexOf("gongdan_list.html")>0
					// ||currentUrl.indexOf("offline_business.html")>0)
					// {
					// webView.reload();
					// }else{
					// webView.loadUrl("file:///android_asset/Web/index.html");
					// }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(MainActivity.this, "项目切换成功", Toast.LENGTH_SHORT).show();
	}

	@Subscribe
	public void onEvent(OnOfflineUploadSucc onOfflineUploadSucc) {

		try {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (webView.getUrl().contains(
							"file:///android_asset/Web/gongdan_list.html")
							|| webView
							.getUrl()
							.contains(
									"file:///android_asset/Web/offline_business.html")) {
						webView.loadUrl(webView.getUrl());
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void showDbNotExistsDialog(String message) {
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage(message);
		builder.setTitle("切换项目失败");
		builder.setCancelable(true);
		builder.setPositiveButton("下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}

		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.create().show();
	}

	public boolean isDbExists(String dbName) {
		String path = DataFolder.getAppDataRoot() + "db/" + dbName;
		File file = new File(path);
        return file.exists();
    }
	@Override
	protected void onResume() {
		super.onResume();
		if(obj != null){
			LongforObject.getDbUpdate();
		}
		nm.cancel(2);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if(webView != null)
			webView.saveState(outState);
	}


	public void releaseAllWebViewCallback() {
		if (android.os.Build.VERSION.SDK_INT < 16) {
			try {
				Field field = WebView.class.getDeclaredField("mWebViewCore");
				field = field.getType().getDeclaredField("mBrowserFrame");
				field = field.getType().getDeclaredField("sConfigCallback");
				field.setAccessible(true);
				field.set(null, null);
			} catch (NoSuchFieldException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
				if (sConfigCallback != null) {
					sConfigCallback.setAccessible(true);
					sConfigCallback.set(null, null);
				}
			} catch (NoSuchFieldException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				if (BuildConfig.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}
	@JavascriptInterface
	public void onPageLoaded(){
		if(needCallBackAfterRestart && webView != null){
			webView.post(new Runnable() {

				@Override
				public void run() {
					if("true".equals(SharedPrefUtil.getMsg("CAMER_PHOTO_PATH", "ISTAKEPHOTO"))){
						webView.loadUrl("javascript:pictureCallbackAfterRestart('" + SharedPrefUtil.getMsg("CAMER_PHOTO_PATH", "CAMER_PHOTO_PATH")
						+ "')");
						SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "ISTAKEPHOTO", null);
						SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "CAMER_PHOTO_PATH", null);
					}else if("true".equals(SharedPrefUtil.getMsg("RECODER_PATH", "ISRECODER"))){
						webView.loadUrl("javascript:TelphoneCallback( '"+SharedPrefUtil.getMsg("RECODER_PATH", "RECODER_PATH")+"');");
						SharedPrefUtil.setMsg("RECODER_PATH", "ISRECODER", null);
						SharedPrefUtil.setMsg("RECODER_PATH", "RECODER_PATH", null);
					}

				}
			});

		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public void registerPhoneStateReceiver(){
		registerReceiver(telReceiver, telFilter);
	}

	public void unRegisterPhoneStateReceiver(){
		if(telReceiver != null){
			unregisterReceiver(telReceiver);
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

    public String addWaterMarking(Bitmap bitmap, String path) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String str = sDateFormat.format(new java.util.Date());
		int width = bitmap.getWidth(), hight = bitmap.getHeight();
		Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.RGB_565); // 建立一个空的BItMap
		Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上
		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些
		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());// 创建一个指定的新矩形的坐标
		Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
		canvas.drawBitmap(bitmap, src, dst, photoPaint);// 将photo 缩放或则扩大到
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setTextSize(24.0f);// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(Color.RED);// 采用的颜色
		// textPaint.setShadowLayer(3f, 1,
		// 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
		//int ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		//int ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		canvas.drawText(str, width-DensityUtil.px2dip(getApplicationContext(), 1000) , hight-DensityUtil.px2dip(getApplicationContext(), 200), textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
		canvas.save();
		canvas.restore();
		return saveFile(icon);
	}
	public String saveFile(Bitmap bitmap) {
		//File dirFile = new File(fileName);
		// 检测图片是否存在
	/*	if (dirFile.exists()) {
			dirFile.delete(); // 删除原图片
		}*/
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sDateFormat.format(new java.util.Date());
		File file = new File(DataFolder.getAppDataRoot() + "cache/images");
		if(!file.exists()){
			file.mkdirs();
		}
		File myCaptureFile = new File(file,  System.currentTimeMillis()
				+ ".jpeg");
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			// 100表示不进行压缩，70表示压缩率为30%
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myCaptureFile.getAbsolutePath().toString();
	}
}
