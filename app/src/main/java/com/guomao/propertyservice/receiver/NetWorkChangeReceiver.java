package com.guomao.propertyservice.receiver;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.service.OfflineData2Service;
import com.guomao.propertyservice.util.FunctionUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 监听网络广播,没网情况下的报事功能
 * @author Administrator
 *
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

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
		}

		MainApplication.judgeNetWork();

		if (MainApplication.IS_ON_LINE) {
			if (MainApplication.IS_NEED_START_PUSH) {
				Intent startI = new Intent(Const.ACTION_START_PUSH_SERVICE);
				startI.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.sendBroadcast(startI);
			}
			// Intent offlineIntent = new Intent(context,
			// SendOfflineDataService.class);
			// context.startService(offlineIntent);
			// Intent updateIntent = new
			// Intent(context,UpdateDataService.class);
			// context.startService(updateIntent);
			UserBiz ubiz = (UserBiz) BizManger.getInstance().get(
					BizType.USER_BIZ);

			if (!FunctionUtil.isServiceWork(context,
					"com.guomao.propertyservice.service.OfflineData2Service")
					&& ubiz.getCurrentUser() != null) {
				if (!MainApplication.isManualSubmit) {
					Intent offineDataIntent = new Intent(context,
							OfflineData2Service.class).putExtra(
							"isShowProgress", true);// 离线报事
					context.startService(offineDataIntent);
				}
			}
		}

	}
}
