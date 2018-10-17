package com.guomao.propertyservice.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.eventbus.OnChangeSite;
import com.guomao.propertyservice.eventbus.OnLogoutByOtherUser;
import com.guomao.propertyservice.eventbus.OnPushOtherSite;
import com.guomao.propertyservice.eventbus.OnXgClickEvent;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.service.SendOfflineDataService;
import com.guomao.propertyservice.model.user.Site;
import com.guomao.propertyservice.util.FunctionUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.page.LoginActivity;
import com.guomao.propertyservice.page.MainActivity;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class XGmessageReceiver extends XGPushBaseReceiver {

	public static boolean LOOP_WHEN_SET_FAIL = true;
	
	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		if (context == null || message == null || ubiz.getCurrentUser() == null) {
			return;
		}
		
		List<Site> siteList = new ArrayList<Site>();
		Site site = new Site();
		siteList = site.getSiteList(ubiz.getCurrentUser().getSiteList());
		if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			String customContent = message.getCustomContent();
			if (customContent != null && customContent.length() != 0) {
				try {
					JSONObject obj = new JSONObject(customContent);
					// if (!obj.isNull("USER_ID")) {
					// if (verifyUser(obj.optString("USER_ID"))) {
					String siteId = obj.optString("site_id");
					if (siteId != null
							&& !siteId.equals(ubiz.getCurrentUser()
									.getSite_id()) && siteList.size() > 1) {
						String site_id = obj.optString("site_id");
						String db_base_name = site_id + "base.db";
						String db_business_name = site_id + "business.db";
						String isMonitor = getIsMonitorBySite(site_id);
						if (DB_base.isDbExists(db_base_name)
								&& DB_base.isDbExists(db_business_name)) {
							EventBus.getDefault().post(
									new OnChangeSite(site_id, isMonitor));
						} else {
							EventBus.getDefault().post(new OnPushOtherSite());
						}
					} else if (!obj.isNull("ACTION")) {
						/*
						  中化金茂的推送

						  ACTION: 0-报事 1-抢单 2-派单 3-退回 4-接收 5-备注 6-完成
						 */
						if (obj.getString("ACTION").equals("0")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("1")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("2")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("3")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("4")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("5")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("6")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));
						} else if (obj.getString("ACTION").equals("7")) {
							updateData(context);
							String task_id = obj.optString("task_id");
							gotoActivity(context,"file:///android_asset/Web/liucheng.html?taskId="+task_id +"&task_type="+'A'+"&isFromNotice="+true);
							EventBus.getDefault().post(new OnXgClickEvent("file:///android_asset/Web/liucheng.html?taskId="+task_id+"&task_type="+'A'+"&isFromNotice="+true));

						}
					}
					// }
					// }
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void gotoActivity(Context context, String value) {
		Intent intent = new Intent();
		if (value != null && (value.contains("file://")||value.contains("http://"))) {
			intent.putExtra("URL", value);
		}else{               
			intent.putExtra("ReturnKeyNotiece", value);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (MainApplication.isAppOnForeground(context)) {
			intent.setClass(context, MainActivity.class);
		} else {
			intent.setClass(context, LoginActivity.class);
		}
		context.startActivity(intent);
	}

	private void dealLogout(Context context) {

		UserBiz biz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		
		// 发通知
		sendNotify(context);
		// 退出登录
		biz.logoutByOtherLogin();
		
	}


	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		if (context == null || message == null) {
			return;
		}
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// if (!obj.isNull("USER_ID")) {
				// if (verifyUser(obj.optString("USER_ID"))) {
				if (!obj.isNull("ACTION")) {
					if (obj.getString("ACTION").equals("LOGOUT")) {
						dealLogout(context);
						EventBus.getDefault().post(new OnLogoutByOtherUser());
					} else if (obj.getString("ACTION").equals("UPDATE")) {
						updateData(context);
					}
				}
				// }
				// }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		if(arg1 >10101 && arg1 <10108){
//			SharedPrefUtil.Log("onDeleteTagResult-再次删除-"+arg2);
			XGPushManager.deleteTag(arg0, arg2);
		}
	}

	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) {
			return;
		}
		if (notifiShowedRlt.getCustomContent().contains("task_id")) {
		}
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1,
			XGPushRegisterResult arg2) {
	
			if(arg1==SUCCESS){
				LOOP_WHEN_SET_FAIL = true;
			}
	}
	
	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		if(arg1 >10101 && arg1 <10108){
			if(LOOP_WHEN_SET_FAIL){
				//SharedPrefUtil.Log("onSetTagResult-再次设置-"+arg2);
				XGPushManager.setTag(arg0, arg2);
			}
		}
	}
	
	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		LOOP_WHEN_SET_FAIL = false;
	}

	public String getIsMonitorBySite(String siteId) {
		List<Site> siteList = new ArrayList<Site>();
		Site site = new Site();
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		siteList = site.getSiteList(ubiz.getCurrentUser().getSiteList());
		HashMap<String, String> siteMap = new HashMap<String, String>();
		for (int i = 0; i < siteList.size(); i++) {
			siteMap.put(siteList.get(i).getSite_id(), siteList.get(i)
					.getIs_monitor() + "");
		}
		String isMonitor = siteMap.get(siteId);
//		int isMonitorRet = 0;
//		try {
//			isMonitorRet = Integer.valueOf(isMonitor);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return isMonitor;
	}

	/**
	 * 判断该消息是不是该用户的
	 * 
	 * @param userId
	 * @return
	 */
	private boolean verifyUser(String userId) {
		if (StringUtil.isNull(userId)) {
			return true;
		}
        return userId.equals(SharedPrefUtil.getUserID());

    }

	private void updateData(Context context) {
		// 跟新业务数据
		if (!FunctionUtil.isServiceWork(context,
				"com.guomao.propertyservice.service.SendOfflineDataService")) {
			Intent offlineIntent = new Intent(context,
					SendOfflineDataService.class);
			offlineIntent.putExtra("refresh_type", "0");
			context.startService(offlineIntent);
			context.startService(offlineIntent);
		}

		// 报事 等业务请求
		// 跟新元数据
		// Intent updateIntent = new Intent(context,UpdateDataService.class);
		// context.startService(updateIntent);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void sendNotify(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		Notification.Builder builder = new Notification.Builder(context);
		builder
				.setSmallIcon(R.drawable.ly)
//				.setLargeIcon(Icon.createWithResource(context, R.drawable.ly))
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_LIGHTS
				| Notification.DEFAULT_SOUND)
				.setTicker( "当前用户在其他设备登录！")
				.setContentTitle("提示")
				.setContentText("当前用户在其他设备登录！")
				.setContentIntent(pi)
				.setWhen(System.currentTimeMillis())
				.setContentInfo("点击重新登录");
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(1, builder.build());
	
	}
}
