package com.guomao.propertyservice.service;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.util.FunctionUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;


/**
 * 程序重新启动运行,触发离线报事
 * @author Administrator
 *
 */
public class LongForService extends Service {
	private static final String tag = "GuangGuService";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, this.getClass());
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime();
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
				60 * 10 * 1000, pendingIntent);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(tag, "onStartCommand called");
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		if (ubiz.getCurrentUser() != null) {
			String site_id = ubiz.getCurrentUser().getSite_id();
			String db_base_name = site_id + "base.db";
			String db_business_name = site_id + "business.db";
			if (ubiz.getCurrentUser() != null && site_id != null) {
				if (DB_base.isDbExists(db_base_name)
						&& DB_base.isDbExists(db_business_name)) {
					if (MainApplication.IS_ON_LINE) {
						if (!FunctionUtil
								.isServiceWork(getApplicationContext(),
										"com.guomao.propertyservice.service.OfflineData2Service")) {
							if (!MainApplication.isManualSubmit) {
								Intent offineDataIntent = new Intent(
										getApplicationContext(),
										OfflineData2Service.class).putExtra(
										"isShowProgress", false);// 离线报事
								getApplicationContext().startService(
										offineDataIntent);
							}
						}
					}
				}
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.e(tag, "onDestroy callded");
		super.onDestroy();
	}

}
