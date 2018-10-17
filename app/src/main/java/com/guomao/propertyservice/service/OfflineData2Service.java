package com.guomao.propertyservice.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.communcation.OfflineOperateComImpl;
import com.guomao.propertyservice.communcation.WebClient;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.dao.OfflineOperateDaoImpl;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.eventbus.OnOfflineUploadSucc;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateCom;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateDao;
import com.guomao.propertyservice.model.offlineoperate.Operation;
import com.guomao.propertyservice.network.bean.NetworkBeanArray;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.exception.HttpException;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

/**
 * 离线报事的服务
 * @author Administrator
 *
 */
public class OfflineData2Service extends Service {

	boolean isShowProgress = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		try {
			isShowProgress = intent.getBooleanExtra("isShowProgress", false);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		new Thread() {

			public void run() {
				try {
					UserBiz ubiz = (UserBiz) BizManger.getInstance().get(
							BizType.USER_BIZ);
					String site_id = ubiz.getCurrentUser().getSite_id();
					String DB_NAME = site_id + "business.db";
					SQLiteDatabase db = null;
					db = DB_business.db_open(DB_NAME, "", "");
					OfflineOperateCom com = new OfflineOperateComImpl(
							WebClient.getInstance());
					OfflineOperateDao dao = new OfflineOperateDaoImpl(db, db,
							getApplicationContext());
					List<Operation> offlineOperates = dao.getOfflineOperates();
					if (isShowProgress) {
						Intent intent = new Intent(Const.ACTION_EXEC_START);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainApplication.getInstance().sendBroadcast(intent);
					}
					if (offlineOperates != null && !offlineOperates.isEmpty()) {
						String result = null;
						for (Operation operate : offlineOperates) {
							try {
								result = com.sendOfflineData2Server(operate);
								NetworkBeanArray networkBeanArray = new NetworkBeanArray(
										result);
								if (networkBeanArray.isSucc()) {
									operate.setOperate_status(1);
									EventBus.getDefault().post(
											new OnOfflineUploadSucc());
								} else {
									operate.setOperate_status(3);
								}
								operate.setOperate_result(result);
								SimpleDateFormat sDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								String date = sDateFormat
										.format(new java.util.Date());
								operate.setSubmit_time(date);
								dao.save(operate);
							} catch (Exception e) {
								L.printStackTrace(e);
								if (e instanceof IOException) {
									operate.setOperate_status(3);// 3
								} else if (e instanceof HttpException) {
									operate.setOperate_status(3);
								} else if (e instanceof SocketTimeoutException) {
									operate.setOperate_status(3);
								} else {
									operate.setOperate_status(2);// 2
									operate.setOperate_result(StringUtil
											.isNull(result) ? e.getMessage()
											: result);
								}
								SimpleDateFormat sDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								String date = sDateFormat
										.format(new java.util.Date());
								operate.setSubmit_time(date);
								dao.save(operate);
								if (!MainApplication.IS_ON_LINE) {
									stopSelf();
									return;
								}
							}
						}
					}
					if (isShowProgress) {
						Intent endIntent = new Intent(Const.ACTION_EXEC_END);
						endIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainApplication.getInstance().sendBroadcast(endIntent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				stopSelf();
			}
        }.start();

		return super.onStartCommand(intent, flags, startId);
	}
}
