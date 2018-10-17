package com.guomao.propertyservice.javascripinterface;

import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.service.SendOfflineDataService;
import com.guomao.propertyservice.service.UpdateDataService;
import com.guomao.propertyservice.util.FunctionUtil;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * 基础和业务数据同步调用的web对象
 * @author Administrator
 *
 */
public class DbDownLoadObject{
	private Context context;

    public DbDownLoadObject(WebView webView) {
		super();
		this.context = webView.getContext();
        WebView webView1 = webView;
	}
	
	

	/**
	 * 基础数据同步方法
	 * @param d_type
	 */
	@JavascriptInterface
	public void updateDataService(String d_type) {
		if (MainApplication.IS_ON_LINE) {
			if (!FunctionUtil.isServiceWork(context,
					"com.guomao.propertyservice.service.UpdateDataService")) {
				Intent upDateIntent = new Intent(context,
						UpdateDataService.class);
				upDateIntent.putExtra("d_type", d_type);
					context.startService(upDateIntent);
			}
		}
	}

	/**
	 * 工单业务数据刷新的服务
	 */
	@JavascriptInterface
	public void sendOffLineDataService() {
		if (MainApplication.IS_ON_LINE) {
			if (!FunctionUtil.isServiceWork(context,
					"com.guomao.propertyservice.service.SendOfflineDataService")) {
				Intent operateIntent = new Intent(context,
						SendOfflineDataService.class);
				operateIntent.putExtra("refresh_type", "0");
					context.startService(operateIntent);
			}
		}else{
			Toast.makeText(context, "您的网络不给力",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 二装数据的刷新下载
	 */
	@JavascriptInterface
	public void getErZhuangService() {
		if (MainApplication.IS_ON_LINE) {
			if (!FunctionUtil.isServiceWork(context,
					"com.guomao.propertyservice.service.SendOfflineDataService")) {
				Intent operateIntent = new Intent(context,
						SendOfflineDataService.class);
				
				operateIntent.putExtra("isErZhuang", true);
				operateIntent.putExtra("refresh_type", "0");
					context.startService(operateIntent);
			}
		}else{
			Toast.makeText(context, "您的网络不给力",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 车辆，通知，预警等数据的刷新下载
	 */
	@JavascriptInterface
	public void getErZhuangService(String type) {
		if (MainApplication.IS_ON_LINE) {
			if (!FunctionUtil.isServiceWork(context,
					"com.guomao.propertyservice.service.SendOfflineDataService")) {
				Intent operateIntent = new Intent(context,
						SendOfflineDataService.class);
				
				operateIntent.putExtra("refresh_type", type);
					context.startService(operateIntent);
			}
		}else{
			Toast.makeText(context, "您的网络不给力",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 历史工单的同步服务
	 */
	@JavascriptInterface
	public void sendHistoryOffLineDataService(String daynums) {
		if (MainApplication.IS_ON_LINE) {
			if (!FunctionUtil.isServiceWork(context,
					"com.guomao.propertyservice.service.SendOfflineDataService")) {
				Intent operateIntent = new Intent(context,
						SendOfflineDataService.class);
				operateIntent.putExtra("isHistory", true);
				operateIntent.putExtra("daynums", daynums);
				operateIntent.putExtra("refresh_type", "0");
					context.startService(operateIntent);

			}
		}
	}



}
