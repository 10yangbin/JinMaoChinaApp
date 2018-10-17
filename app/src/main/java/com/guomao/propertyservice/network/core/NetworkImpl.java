package com.guomao.propertyservice.network.core;

import java.io.File;

import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBean;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.CommenUtil;
import com.guomao.propertyservice.util.FunctionUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Intent;
import android.util.Log;


/**
 * 发送基础和业务数据的请求的统一方法
 */

public class NetworkImpl extends BaseNetWork implements INetwork {


	@Override
	public void getData(RequestVo requestVo,
			final OperateCallBack operateCallBack) throws Exception {

		final HttpUtils http = new HttpUtils();
		http.configTimeout(60 * 10 * 1000);
		http.configSoTimeout(60 * 10 * 1000);
		http.configRequestRetryCount(10);
		JSONObject json = requestVo.getJson();
		JSONObject jsonobj = new JSONObject();
		UserBiz uBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User currentUser = uBiz.getCurrentUser();
		if (!requestVo.getRequestUrl().contains(CommenUrl.loginAction)) {
			if (currentUser == null
					|| StringUtil.isNull(currentUser.getUser_id())) {
				Intent intent = new Intent(Const.ACTION_NetworkImpl_USER_NULL);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MainApplication.getInstance().sendBroadcast(intent);
				return;
			}
		}
		String url = requestVo.getRequestUrl();
		if (url.contains(CommenUrl.loginAction)
				|| url.contains(CommenUrl.baseData)
				|| url.contains(CommenUrl.businessData)
				|| url.contains(CommenUrl.logOutAction)) {
			if (currentUser != null) {
				json.put("U", currentUser.getUser_id());
				json.put("M", currentUser.getIs_monitor());
				json.put("S", currentUser.getSite_id());
				json.put("T", currentUser.getCf_type());
				json.put("channelid",
						CommenUtil.showImei(MainApplication.getInstance().getApplicationContext()) + ","
								+ uBiz.getUserId());
			}
			json.put("platform", "android");
			json.put("D", CommenUtil.showImei(MainApplication.getInstance()
					.getApplicationContext()));
			requestVo.getRequestParams().addBodyParameter("reqjson",
					json.toString());
		} else {
			if (currentUser != null) {
				jsonobj.put("U", currentUser.getUser_id());
				jsonobj.put("M", currentUser.getIs_monitor());
				jsonobj.put("S", currentUser.getSite_id());
				jsonobj.put("T", currentUser.getCf_type());
				jsonobj.put("platform", "android");
				jsonobj.put("channelid",
						CommenUtil.showImei(MainApplication.getInstance().getApplicationContext()) + ","
								+ uBiz.getUserId());
				jsonobj.put("D", CommenUtil.showImei(MainApplication
						.getInstance().getApplicationContext()));

				if(url.contains("/data/business/notice")){
					jsonobj.put("notice", json);
				}else if(url.contains("/data/business/homepage")){
					jsonobj.put("homepage", json);
				}
				else if(url.contains("/data/business/decoration")){
					jsonobj.put("decoration", json);
				}
				else if(url.contains("/data/business/letters")){
					jsonobj.put("letters", json);
				}
				else if(url.contains("/data/business/vehicle")){
					jsonobj.put("vehicle", json);
				}
				else if(url.contains("/data/business/warning")){
					jsonobj.put("warning", json);
				}
				else if(url.contains("/data/business/customer")){
					jsonobj.put("customer", json);
				}
				else{
					jsonobj.put("taskmessage", json);
				}
			}
			requestVo.getRequestParams().addBodyParameter("reqjson",
					jsonobj.toString());
		}
		if (requestVo.getFilesPath() != null
				&& requestVo.getFilesPath().size() != 0) {
			for (int i = 0; i < requestVo.getFilesPath().size(); i++) {
				File file = new File(requestVo.getFilesPath().get(i));
				if(file.exists()){
					requestVo.getRequestParams().addBodyParameter("file" + i, file);
				}
			}

		}
		RequestParams params = requestVo.getRequestParams();
		if(params == null){
			params = new RequestParams();
		}
		String site_id = "0";
		User u = ((UserBiz) BizManger.getInstance().get(BizType.USER_BIZ)).getCurrentUser();
		if(u != null && !StringUtil.isNull(u.getSite_id())){
			site_id = u.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		params.addHeader("site_id", site_id);
		params.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		//设置cookie
		useCookie(http);

		SharedPrefUtil.Log(" Url---->"+requestVo.getRequestUrl()+"--->"
				+ jsonobj.toString());

		http.send(requestVo.getRequestMethod(), requestVo.getRequestUrl(),
				params, new RequestCallBack<String>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						/*Log.d("---NetWorkImpl--->>",
								responseInfo.result);*/

						NetworkBean networkBean = new NetworkBean(
								responseInfo.result);
						/*SharedPrefUtil.Log("返回的数据的长度  ---->"
								+ networkBean.getData().length());*/
						saveCookie(http);
						if (networkBean.isSucc()) {
							operateCallBack.onLoadSuccess(responseInfo.result);
						} else {
							operateCallBack.onLoadFail(networkBean.getMessage());
							
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if(msg != null && msg.contains("UnknownHostException")){
							msg = "您的网络不给力～";
						}
						operateCallBack.onLoadFail(msg);
						//保存cookie
						saveCookie(http);
						error.printStackTrace();
					}
				});

	}
}
