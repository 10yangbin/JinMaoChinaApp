package com.guomao.propertyservice.javascripinterface;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.communcation.ExecuteBiz;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.core.INetworkSync;
import com.guomao.propertyservice.network.core.NetworkSyncImpl;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * 使用window.csq_exe.方法名调用本类中的方法
 * 
 * 业务执行方法调用的web对象
 * 
 * @author Administrator
 * 
 */
public class ExeObject {
	private Context context;
	public ExeObject(WebView webView) {
		super();
		this.context = webView.getContext();
	}

	@JavascriptInterface
	public String doexec(String action, String param) {
		String result = null;
		try {
			RequestVo mRequestVo = RequestVo.getInstance();
			RequestParams mParams = new RequestParams();
			JSONObject jsonobj = null;
			if(param == null || param.equals("")){
				jsonobj = new JSONObject();
			}else{
				jsonobj = new JSONObject(param);
			}
			
			mRequestVo.setRequestUrl(CommenUrl.getActionUrl(action));
			mRequestVo.setRequestMethod(HttpMethod.POST);
			mRequestVo.setJson(jsonobj);
			mRequestVo.setRequestParams(mParams);
			INetworkSync networkSync = new NetworkSyncImpl();
			result = networkSync.getData(mRequestVo, new OperateCallBack() {

				@Override
				public void onLoadSuccess(Object obj) {
					Intent endIntent = new Intent(Const.ACTION_EXEC_END);
					endIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					MainApplication.getInstance().sendBroadcast(
							endIntent);
					
				}

				@Override
				public void onTokenInvalid() {

				}

				@Override
				public void onLoadFail(String msg) {
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}

			});
		} catch (Exception e) {
			handleException(e, false);
		}
		return result;
	}

	// 业务执行方法
	@JavascriptInterface
	public boolean exec(String action, String param) {
		try {
			ExecuteBiz.exec(action, param, null, false, context);
			return true;
		} catch (Exception e) {
			handleException(e, false);
			return false;
		}
	}

	@JavascriptInterface
	public boolean exec(String action, String param, boolean needOnline) {
		try {
			ExecuteBiz.exec(action, param, null, needOnline, context);
			return true;
		} catch (Exception e) {
			handleException(e, false);
			return false;
		}
	}

	@JavascriptInterface
	public boolean exec(String action, String param, String files) {
		try {
			ExecuteBiz.exec(action, param, files, false, context);
			return true;
		} catch (Exception e) {
			handleException(e, false);
			return false;
		}
	}

	@JavascriptInterface
	public boolean exec(String action, String param, String files,
			boolean needOnline) {
		try {
			ExecuteBiz.exec(action, param, files, needOnline, context);
			return true;
		} catch (Exception e) {
			SharedPrefUtil.Log("执行失败原因 ---->"
					+ e.toString());
			handleException(e, false);
			return false;
		}
	}

	private void handleException(Exception e, boolean needOnline) {

		String text = e.getMessage();
		if (needOnline) {
			if (e instanceof IOException) {
				text = "您的网络不给力~";
			}
		}
		if (e instanceof JSONException) {
			text = "收到服务器返回信息:请求失败，请稍候重试...";
		}
		if (text.equals("Internal Server Error")) {
			text = "收到服务器返回信息:请求失败，请稍候重试...";
		}
		if (text.equals("Not Found")) {
			text = "收到服务器返回信息:请求失败，请稍候重试...";
		}
		if (StringUtil.isNull(text)) {
			return;
		}
		/*if (text.length() > 0) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}*/
	}
}
