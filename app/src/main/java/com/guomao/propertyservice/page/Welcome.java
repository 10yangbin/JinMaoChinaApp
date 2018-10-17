package com.guomao.propertyservice.page;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.eventbus.OnApkDownLoadFail;
import com.guomao.propertyservice.eventbus.OnApkDownLoadStart;
import com.guomao.propertyservice.eventbus.OnApkDownLoadSucc;
import com.guomao.propertyservice.eventbus.OnCancelUpdate;
import com.guomao.propertyservice.eventbus.OnCurrentVersionNew;
import com.guomao.propertyservice.eventbus.OnResDownLoadFail;
import com.guomao.propertyservice.eventbus.OnResDownLoadStart;
import com.guomao.propertyservice.eventbus.OnResDownLoadSucc;
import com.guomao.propertyservice.eventbus.OnResInitStart;
import com.guomao.propertyservice.eventbus.OnResInitSucc;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.mydao.AppAction;
import com.guomao.propertyservice.mydaoimpl.AppActionImpl;
import com.guomao.propertyservice.util.AnimationUtil;
import com.guomao.propertyservice.util.FunctionUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.jinmaochina.propertyservice.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class Welcome extends Activity {
	@ViewInject(R.id.logo)
	ImageView iv_logo;
	@ViewInject(R.id.tv_version)
	TextView tv_version;
	@ViewInject(R.id.ll_init)
	LinearLayout ll_init;
	@ViewInject(R.id.tv_initContent)
	TextView tv_initContent;
	String TAG = "welcome";
	String filePath = "";
	private static int INSTALL_CODE = 0x123;
	private Handler handler;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		//ONEAPM测试帐号
		//BlueWare.withApplicationToken("357428CE0BC042CB60616F01E297C07794").start(this.getApplication());
		//ONEAPM生产帐号
		//BlueWare.withApplicationToken("C6C632E1F49300F4B1726EC3146BAC1420").start(this.getApplication());
		setContentView(R.layout.activity_welcome);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);
		tv_version.setText("Version:"
				+ FunctionUtil.getVersionName(getApplicationContext()));
		showAnimation();
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					startActivity(new Intent(Welcome.this,
							LoginActivity2.class));
					Welcome.this.finish();
				}
			}
        };

        UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		
		if (ubiz.getCurrentUser()==null) {
			startActivity(new Intent(Welcome.this,LoginActivity2.class));
			Welcome.this.finish();
		}
		
		if (MainApplication.IS_ON_LINE) {
			checkUpdate();
		} else {
			handler.sendEmptyMessageDelayed(1, 1000);
			
		}
	}

	public void showAnimation() {
		AnimationUtil.anim(Welcome.this, iv_logo, R.anim.logo_anim);
	}

	public void checkUpdate() {
		AppAction appAction = new AppActionImpl();
		appAction.checkUpdate(Welcome.this);
	}

	@Subscribe
	public void onEvent(OnCurrentVersionNew onCurrentVersionNew) {
		if(handler!=null){
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	@Subscribe
	public void onEvent(OnCancelUpdate onCancelUpdate) {
		if(handler!=null){
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	@Subscribe
	public void onEvent(OnResDownLoadStart onResDownLoadStart) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("资源下载中，请稍候");
	}

	@Subscribe
	public void onEvent(OnResDownLoadSucc onResDownLoadSucc) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("资源下载完成");
	}

	@Subscribe
	public void onEvent(OnApkDownLoadStart onApkDownLoadStart) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("正在下载安装包，请稍候");
	}

	@Subscribe
	public void onEvent(OnApkDownLoadSucc onApkDownLoadSucc) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("安装包下载完成");
	}

	@Subscribe
	public void onEvent(OnApkDownLoadFail onApkDownLoadFail) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("安装包下载失败");
		if(handler!=null){
			handler.sendEmptyMessageDelayed(1, 1000);
		}
	}

	@Subscribe
	public void onEvent(OnResDownLoadFail onResDownLoadFail) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("资源下载失败");
		if(handler!=null){
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	@Subscribe
	public void onEvent(OnResInitStart onreResInitStart) {
		ll_init.setVisibility(View.VISIBLE);
		tv_initContent.setText("资源文件初始化，请稍候");
	}

	@Subscribe
	public void onEvent(OnResInitSucc onResInitSucc) {
		ll_init.setVisibility(View.GONE);
		ll_init.setVisibility(View.VISIBLE);
		if(handler!=null){
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x123 && resultCode != RESULT_OK) {
			Welcome.this.finish();
		}
	}
	@Override
	protected void onDestroy() {
		if(handler != null){
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
		super.onDestroy();
	}
}
