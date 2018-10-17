package com.guomao.propertyservice.page;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.openaccount.ui.widget.PasswordInputBox;
import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.callback.SiteCheckedCallback;
import com.guomao.propertyservice.callback.XGRegisteAction;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.util.CommenUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.guomao.propertyservice.widget.LoginACInputBox;
import com.guomao.propertyservice.widget.MyProgress;
import com.jinmaochina.propertyservice.R;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionGen;

public class LoginActivity2 extends Activity implements
View.OnClickListener {

	private Button btnLogin;
	private TextView btnRegist;
	private LoginACInputBox etUserName;
	private PasswordInputBox etPwd;
	private static final int WHAT_LOGIN_SUCCESS = 0X111;
	private static final int WHAT_LOGIN_FAILED = 0X112;
	private static final int WHAT_LOGOUT_SUCCESS = 0X113;
	private static final int WHAT_LOGOUT_FAILED = 0X114;
	protected static final String TAG = null;
	private Handler handler;
	private String url;
	private String filePath;
	private UserBiz ubiz;
	private BroadcastReceiver receiver;
	String msg = "";
	boolean isLogin = false;
	String db_base_name = null;
	String db_business_name = null;
	String site_id = null;
	int isMonitor = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kinco_ali_sdk_openaccount_login2);
		initData();
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);

		if (ubiz.isLogin()) {
			if (ubiz.getCurrentUser().getSiteList() != null
					&& !ubiz.getCurrentUser().getSiteList().equals("")) {
				handleLogin(true, "");
				return;
			}
		}
		initReceiver();
		initHandler();
		initView();
		addListener();
		getTakePicPermission();
	}

	public void onPermissionSuccess() {

	}

	public void onPermissionFail() {
		//ToastUtils.getInstanc(this).showToast("要给权限哦");
	}
	public void getPermission(String... permissions) {

		if (Build.VERSION.SDK_INT >= 23) {

			if (findDeniedPermissions(this, permissions).size() > 0) {
				PermissionGen.with(this)
						.addRequestCode(100)
						.permissions(
								permissions
						)
						.request();
			} else {
				onPermissionSuccess();
			}


		} else {
			onPermissionSuccess();
		}

	}

	@TargetApi(value = Build.VERSION_CODES.M)
	public static List<String> findDeniedPermissions(Activity activity, String... permission) {
		List<String> denyPermissions = new ArrayList<>();
		for (String value : permission) {
			if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
				denyPermissions.add(value);
			}
		}
		return denyPermissions;
	}

	//申请拍照和文件读写权限
	public void getTakePicPermission() {
		getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.CAMERA,Manifest.permission.PACKAGE_USAGE_STATS,Manifest.permission.READ_PHONE_STATE);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {

		for (int i = 0; i < grantResults.length; i++) {

			if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
				onPermissionFail();
				return;
			}
		}

		onPermissionSuccess();

	}

	private void initData() {
		Intent intent = getIntent();
		url = intent.getStringExtra("URL");
		String returnKeyNotiece = intent.getStringExtra("ReturnKeyNotiece");
		if(returnKeyNotiece !=null){
			Toast.makeText(LoginActivity2.this, returnKeyNotiece, Toast.LENGTH_SHORT);
		}
		filePath = intent.getStringExtra("FILEPATH");
	}

	private void initHandler() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WHAT_LOGIN_SUCCESS:
				case WHAT_LOGIN_FAILED:
					handleLogin(msg.what == WHAT_LOGIN_SUCCESS,
					String.valueOf(msg.obj));
					break;
				case WHAT_LOGOUT_FAILED:
				case WHAT_LOGOUT_SUCCESS:
					handleLogout(msg.what == WHAT_LOGOUT_SUCCESS,
					String.valueOf(msg.obj));

				default:
					break;
				}
			}
		};
	}

	private void initReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String action = arg1.getAction();
				if (Const.ACTION_CONNECT_TO_PUSH_SERVER_FAILED.equals(action)) {
					MyProgress.dismiss();
					// Toast.makeText(LoginActivity.this, "连接推送服务器失败...请重试",
					// Toast.LENGTH_SHORT).show();
				} else if (Const.ACTION_CONNECT_TO_PUSH_SERVER.equals(action)) {
					MyProgress.show("正在连接推送服务器...", LoginActivity2.this);
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_CONNECT_TO_PUSH_SERVER);
		filter.addAction(Const.ACTION_CONNECT_TO_PUSH_SERVER_FAILED);

		registerReceiver(receiver, filter);

	}

	private void addListener() {
		btnLogin.setOnClickListener(this);
		btnRegist.setOnClickListener(this);
	}

	private void initView() {

		etPwd = findViewById(R.id.password);
		etUserName = findViewById(R.id.login_id);
		btnLogin = (Button) findViewById(R.id.login_btn);
		btnRegist = (TextView) findViewById(R.id.button_account_register);
		if(!StringUtil.isNull(MainApplication.getInstance().getUserName())&&!StringUtil.isNull(MainApplication.getInstance().getPassword())){
			etUserName.getEditText().setText(MainApplication.getInstance().getUserName());
			etPwd.getEditText().setText(MainApplication.getInstance().getPassword());
            btnLogin.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			doLogin();
			break;
		case R.id.button_account_register:
			// doLogout();
			android.os.Process.killProcess(android.os.Process.myPid());
			finish();
		default:
			break;
		}

	}

	private void doLogout() {

		boolean isOk = false;
		String msg = null;
		try {

			ubiz.logout(new OperateCallBack() {

				@Override
				public void onTokenInvalid() {

				}

				@Override
				public void onLoadSuccess(Object obj) {
					Message message = Message.obtain(handler);
					if (String.valueOf(obj).equals("true")) {
						message.what = WHAT_LOGOUT_SUCCESS;
					} else {
						message.what = WHAT_LOGOUT_FAILED;
						message.obj = "退出成功";
					}
					handler.sendMessage(message);
				}

				@Override
				public void onLoadFail(String msg) {
					Toast.makeText(MainApplication.getInstance(), "退出失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			if (e instanceof IOException) {
				msg = "您的网络不给力~";
			} else {
				msg = e.getMessage();
			}
		}
	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}

	@SuppressLint("DefaultLocale")
	private void doLogin() {
		if (checkInput()) {
			MyProgress.show("登录中...", this);
			try {
				ubiz.login(etUserName.getEditText().getText().toString().toUpperCase(), etPwd
						.getEditText().getText().toString(), new OperateCallBack() {

					@Override
					public void onTokenInvalid() {

					}

					@Override
					public void onLoadSuccess(Object obj) {
						handleLogin(true, "登录成功");
					}

					@Override
					public void onLoadFail(String msg) {
						handleLogin(false, msg);
					}
				});
			} catch (Exception e) {
				handleLogin(false, "登录失败");
			}
		}

	}

	private boolean checkInput() {

		if (StringUtil.isNull(etUserName.getEditText().getText().toString())) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (StringUtil.isNull(etPwd.getEditText().getText().toString())) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public void handleLogin(boolean isSuccess, String msg) {
		MyProgress.dismiss();
		if (isSuccess) {
			//String tag = ubiz.getCurrentUser().getTag();
			// if (!StringUtil.isNull(tag)) {
			// PushManager.setTags(this, Arrays.asList(ubiz.getCurrentUser()
			// .getTag().split(",")));
			// }
			if (!ubiz.isLogin()) {
				registeXGPush(new XGRegisteAction() {

					@Override
					public void OnRegisteSucc() {
						showDialogdb();
					}

					@Override
					public void OnRegisteFail() {
						Toast.makeText(getApplicationContext(), "信鸽注册失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				registeXGPush(new XGRegisteAction() {

					@Override
					public void OnRegisteSucc() {
						showDialogdb();
					}

					@Override
					public void OnRegisteFail() {
					}
				});
				showDialogdb();
			
			}

		} else {
			Toast.makeText(this, StringUtil.isNull(msg) ? "登录失败,请重试" : msg,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void registeXGPush(final XGRegisteAction xgRegisteAction) {
		UserBiz uBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		XGPushConfig.enableDebug(this, true);
		if (uBiz.getUserId() != null && !"".equals(uBiz.getUserId())) {
			XGPushManager.registerPush(
					getApplicationContext(),
					CommenUtil.showImei(getApplicationContext()) + ","
							+ uBiz.getUserId(), new XGIOperateCallback() {
						@Override
						public void onSuccess(Object arg0, int arg1) {
							xgRegisteAction.OnRegisteSucc();
							List<String> tagList = new ArrayList<String>();
							//User tag = new User();
							tagList = ubiz.getCurrentUser()
									.getTag();
							String tag = null;
							for (int i = 0; i < tagList.size()-1; i++) {
								/*XGPushManager.deleteTag(
											getApplicationContext(),
											 tagList.get(i));*/
								tag = tagList.get(i);
								if(tag.startsWith("\"")){
									tag = tag.substring(1, tag.length());
								}
								if(tag.endsWith("\"")){
									tag = tag.substring(0, tag.length()-1);
								}
								XGPushManager.setTag(
										getApplicationContext(),
										tag);
							}
						}

						@Override
						public void onFail(Object arg0, int arg1, String arg2) {
							xgRegisteAction.OnRegisteFail();
						}
					});
		}
		// initCustomPushNotificationBuilder(getApplicationContext());
	}
	
	private void showDialogdb(){

		site_id = ubiz.getCurrentUser().getSite_id();
//		isMonitor = ubiz.getCurrentUser().getIs_monitor();
		db_base_name = site_id + "base.db";
		db_business_name = site_id + "business.db";
		if (DB_base.isDbExists(db_base_name)
				&& DB_base.isDbExists(db_business_name)) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.putExtra("URL", url);
			intent.putExtra("FILEPATH", filePath);
			startActivity(intent);
			finish();
		} else {
			//如果数据库文件被删除,允许重新下载。清理时间记录。
			if(!DB_base.isDbExists(db_base_name)){
				SharedPrefUtil.putDbVersion(site_id+"baseVer","-1");
			}
			if(!DB_base.isDbExists(db_business_name)){
				SharedPrefUtil.putDbVersion(site_id+"businessVer","-1");
			}
			try {
				if (ubiz.getCurrentUser().getSiteList() != null
						&& !ubiz.getCurrentUser().getSiteList()
						.equals("")) {
					DbDialog.getInstance(true).showdbListDialog(
							LoginActivity2.this,
							new SiteCheckedCallback() {

								@Override
								public void OnChecked(
										String siteId,
										String isMonitor) {
									ubiz.getCurrentUser()
									.setSite_id(
											siteId);
									ubiz.getCurrentUser()
									.setIs_monitor(
											isMonitor);
									Intent intent = new Intent(
											getApplicationContext(),
											MainActivity.class);
									intent.putExtra(
											"URL", url);
									intent.putExtra(
											"FILEPATH",
											filePath);
									startActivity(intent);
									finish();
								}

								@Override
								public void OnCancel() {
									Toast.makeText(
											LoginActivity2.this,
											"离线包尚未下载，请下载",
											Toast.LENGTH_SHORT)
									.show();
									android.os.Process
									.killProcess(android.os.Process
											.myPid());
								}
							});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}

	// private void initCustomPushNotificationBuilder(Context context) {
	// XGCustomPushNotificationBuilder build = new
	// XGCustomPushNotificationBuilder();
	// RemoteViews remoteViews = new RemoteViews(getPackageName(),
	// R.layout.notification);
	// PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
	// new Intent(this, MainActivity.class),
	// PendingIntent.FLAG_CANCEL_CURRENT);
	// build.setContentView(remoteViews);
	// build.setContentIntent(pendingintent);
	// build.setLayoutId(R.layout.notification);
	// build.setLayoutTextId(R.id.content);
	// build.setLayoutTitleId(R.id.title);
	// build.setLayoutIconId(R.id.icon);
	// build.setLayoutIconDrawableId(R.drawable.ic_launcher);
	// build.setIcon(R.drawable.ic_launcher);
	// build.setLayoutTimeId(R.id.time);
	// XGPushManager.setPushNotificationBuilder(getApplicationContext(), 211,
	// build);
	// }

	public void handleLogout(boolean b, String msg) {
		if (b) {
			finish();
		} else {
			Toast.makeText(this, StringUtil.isNull(msg) ? "退出失败,请重试" : msg,
					Toast.LENGTH_SHORT).show();
		}

	}

	public static Bitmap getBitmapFromResources(Activity act, int resId) {
		Resources res = act.getResources();
		return BitmapFactory.decodeResource(res, resId);
	}

	@Override
	protected void onDestroy() {
		if(handler != null)
			handler.removeCallbacksAndMessages(null);
		super.onDestroy();
		if (receiver != null)
			unregisterReceiver(receiver);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
