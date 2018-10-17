package com.guomao.propertyservice.network.core;

import java.io.File;
import java.io.IOException;

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
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;

import android.content.Intent;

/**
 * 同步联网获取数据的方法
 * 业务接口的统一方法
 * @author bin.yang
 *
 */
public class NetworkSyncImpl extends BaseNetWork implements INetworkSync {

	@Override
	public String getData(RequestVo requestVo,
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
			}
		}
		if (!requestVo.getRequestUrl().contains(CommenUrl.baoshiAction)
				&& !requestVo.getRequestUrl().contains(CommenUrl.loginAction)) {
			if (MainApplication.IS_ON_LINE) {
				Intent intent_net_normal = new Intent(
						Const.ACTION_NETWORK_NORMAL);
				intent_net_normal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MainApplication.getInstance().sendBroadcast(intent_net_normal);
			} else {
				Intent intent_net_error = new Intent(Const.ACTION_NETWORK_ERROR);
				intent_net_error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MainApplication.getInstance().sendBroadcast(intent_net_error);
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
				jsonobj.put("D", CommenUtil.showImei(MainApplication
						.getInstance().getApplicationContext()));
				
				//动火申请的请求，用decoration_hp携带数据,提交服务器
				if(url.contains("/exchange/letters")){
					
					jsonobj.put("letters", json);
				
			    //动火流程,通过，拒绝，完成，重新申请等的请求，用hp_process携带数据,提交服务器
				}else if(url.contains("/exchange/vehicle/base")){
					
					jsonobj.put("vehiclemsg", json);
				
				//整改申请的请求
				}else if(url.contains("/exchange/vehicle/pass")){
					
					jsonobj.put("vehiclepassmsg", json);
				
				//二装申请的请求
				}else if(url.contains("/exchange/decoration/infor")){
					
					jsonobj.put("decoration_infor", json);
				
			    //二装流程的请求	
				}else if(url.contains("/exchange/decoration/infor_pro")){
					
					jsonobj.put("worksheet", json);
					
				//整改请求		
				}else if(url.contains("/exchange/decoration/rn")){
					
					jsonobj.put("decoration_rn", json);
				
			    //整改流程的请求	
				}else if(url.contains("/exchange/decoration/rn_pro")){
					
					jsonobj.put("worksheet", json);
					
				//变更请求	
				}else if(url.contains("/exchange/decoration/chmod")){
					
					jsonobj.put("decoration_chmod", json);
				
			    //变更流程的请求	
				}else if(url.contains("/exchange/decoration/chmod_pro")){
					
					jsonobj.put("worksheet", json);
					
				//报事工单，刷新，工单流程等操作的请求		
				}else{
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
		String str = null;
		RequestParams params = requestVo.getRequestParams();
		if(params == null){
			params = new RequestParams();
		}
		
		
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		params.addHeader("site_id", site_id);
		params.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));

	/*	SharedPrefUtil.Log("请求的时间，地址，参数---->"+requestVo.getRequestUrl()+"----------->"
				+ jsonobj.toString());*/
		
		try {
			
			
			useCookie(http);
			ResponseStream responseStream = http.sendSync(
					requestVo.getRequestMethod(), requestVo.getRequestUrl(),
					params);
			String returnstr = responseStream.readString();
			str = returnstr;
			saveCookie(http);
			NetworkBean networkBean = new NetworkBean(returnstr);
			if (networkBean.isSucc()) {
				operateCallBack.onLoadSuccess(returnstr);
			
			} else {
				// throw (new IOException("操作失败"));
				operateCallBack.onLoadFail(networkBean.getMessage());
				
			}
		} catch (Exception e) {
			String msg = null;
			msg = e.getMessage();
			if(msg != null && msg.contains("UnknownHostException")){
				throw new IOException("您的网络不给力～");
			}
			throw e;
		}
		return str;
	}

}
