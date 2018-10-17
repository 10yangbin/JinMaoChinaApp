package com.guomao.propertyservice.receiver;

import java.util.ArrayList;
import java.util.List;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.model.user.Site;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {

	UserBiz userBiz;

	@Override
	public void onReceive(Context context, Intent arg1) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			// Intent intent_net_error = new Intent(Const.ACTION_NETWORK_ERROR);
			// intent_net_error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.sendBroadcast(intent_net_error);
		} else {
			// Intent intent_net_normal = new
			// Intent(Const.ACTION_NETWORK_NORMAL);
			// intent_net_normal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.sendBroadcast(intent_net_normal);
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
			if (userBiz.getCurrentUser() == null) {
				unRegisteXG(context);
			}
		}
	}

	public void unRegisteXG(final Context context) {
		XGPushManager.unregisterPush(context, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object arg0, int arg1) {
				/*List<Site> siteList = new ArrayList<Site>();
				Site site = new Site();
				siteList = site.getSiteList(userBiz.getCurrentUser()
						.getSiteList());
				for (int i = 0; i < siteList.size(); i++) {
					XGPushManager.deleteTag(context, "notMonitor"
							+ siteList.get(i).getSite_id());
					XGPushManager.deleteTag(context, "isMonitor"
							+ siteList.get(i).getSite_id());
				}*/
				List<String> tagList = new ArrayList<String>();
				tagList = userBiz.getCurrentUser()
						.getTag();
				for (int i = 0; i < tagList.size(); i++) {
					XGPushManager.deleteTag(context,
							tagList.get(i));
					XGPushManager.deleteTag(context, 
							tagList.get(i));
				}
				Intent intent_user_logout_end = new Intent(
						Const.ACTION_USER_LOGOUT_END);
				intent_user_logout_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.sendBroadcast(intent_user_logout_end);
			}

			@Override
			public void onFail(Object arg0, int arg1, String arg2) {
				List<Site> siteList = new ArrayList<Site>();
				Site site = new Site();
				siteList = site.getSiteList(userBiz.getCurrentUser()
						.getSiteList());
				for (int i = 0; i < siteList.size(); i++) {
					XGPushManager.deleteTag(context, "notMonitor"
							+ siteList.get(i).getSite_id());
					XGPushManager.deleteTag(context, "isMonitor"
							+ siteList.get(i).getSite_id());
				}
				Intent intent_user_logout_end = new Intent(
						Const.ACTION_USER_LOGOUT_END);
				intent_user_logout_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.sendBroadcast(intent_user_logout_end);
			}
		});
	}
}
