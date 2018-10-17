package com.guomao.propertyservice.mydaoimpl;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.eventbus.OnCancelUpdate;
import com.guomao.propertyservice.page.DbDialog;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.mydao.AppAction;
import com.guomao.propertyservice.network.NetworkCallBack;
import com.guomao.propertyservice.network.core.BaseNetWork;
import com.guomao.propertyservice.network.request.DownLoadFileVo;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.FunctionUtil;
import com.guomao.propertyservice.util.MyNotification;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.guomao.propertyservice.util.UpdateManage;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;

/**
 * APP版本升级的下载 
 * db的检查和下载
 * @author Administrator
 *
 */
public class AppActionImpl extends BaseNetWork implements AppAction {
	MyNotification myNotification;
	UpdateManage updateManage;

	@Override
	public void downLoadFile(final Activity context,
			final DownLoadFileVo downLoadFileVo,
			final NetworkCallBack networkCallBack) {
		final HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		RequestVo mRequestVo = new RequestVo();
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
		mRequestVo.setRequestParams(params);
		myNotification = new MyNotification();
		useCookie(http);
		http.download(downLoadFileVo.getUrl(), downLoadFileVo.getTarget(),mRequestVo.getRequestParams(),
				true, true, new RequestCallBack<File>() {
					@Override
					public void onStart() {
						myNotification.setUpNotification(context,
								downLoadFileVo.getName(), 0);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (total != -1) {
							MathContext mc = new MathContext(2,
									RoundingMode.HALF_DOWN);
							BigDecimal c = new BigDecimal(current);
							BigDecimal t = new BigDecimal(total);
							BigDecimal bigDecimal = new BigDecimal(100);
							int progress = c.divide(t, mc).multiply(bigDecimal)
									.intValue();
							myNotification.builder.setProgress(100, progress,
									false);
							myNotification.builder.setContentText(progress
									+ "%");
							myNotification.manager.notify(0,
									myNotification.builder.build());
							if (progress == 100) {
								myNotification.manager.cancel(0);
								networkCallBack.onLoadSuccess(null);
							}
						} else {
							myNotification.builder.setContentText("下载中:  "
									+ current + "byte");
							myNotification.manager.notify(0,
									myNotification.builder.build());
						}
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						networkCallBack.onLoadSuccess(null);
						myNotification.builder.setContentText("下载完成");
						myNotification.manager.notify(0,
								myNotification.builder.build());
						myNotification.manager.cancel(0);
						 saveCookie(http);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						myNotification.manager.cancel(0);
						networkCallBack.onLoadFail();
						 saveCookie(http);
					}
				});
		
		  
	}

	/**
	 * apk升级检查更新
	 */
	@Override
	public void checkUpdate(final Activity context) {
		final HttpUtils http = new HttpUtils();
		RequestVo mRequestVo = new RequestVo();
		mRequestVo.setRequestUrl(CommenUrl.getActionUrl(CommenUrl.checkUpdate));
		mRequestVo.setRequestMethod(HttpMethod.POST);
		
		http.configSoTimeout(10 * 1000);
		RequestParams params = new RequestParams();
		JSONObject obj = new JSONObject();
		try {
			obj.put("platform", "android");
		} catch (JSONException e1) {
			throw new AssertionError(e1);
		}
		params.addBodyParameter(new BasicNameValuePair("reqjson",obj.toString() ));
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
		
		mRequestVo.setRequestParams(params);
		useCookie(http);
		http.send(mRequestVo.getRequestMethod(), mRequestVo.getRequestUrl(),
				mRequestVo.getRequestParams(), new RequestCallBack<String>() {
					@Override
					public void onStart() {
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!responseInfo.result.equals("")) {
							updateManage = new UpdateManage(context);
							String appVersion = "0";
							int resVersion = 0;
							String type = null;
							String isForceUpdate = null;
							String updateContent = null;
							String appPath = null;
							String resPath = null;
							int sqlexecute = -1;
							int sqlexectype = -1;
							String sqlcontent = null;
							try {
								JSONObject json = new JSONObject(
										responseInfo.result);
								json = json.optJSONObject("retdata");
								appVersion = json.getString("appnumber");
								resVersion = json.getInt("filenumber");
								type = json.getString("type");
								isForceUpdate = json.getString("isforced");
								updateContent = json.getString("content");
								appPath = json.getString("apppath");
								resPath = json.getString("filepath");
								sqlexecute = json.getInt("sqlexecute");
								sqlexectype = json.getInt("sqlexectype");
								sqlcontent = json.getString("sqlcontent");
								updateManage.AutoUpdate(isForceUpdate, type,
										updateContent, appVersion, resVersion,
										appPath, resPath, sqlexecute,
										sqlexectype, sqlcontent);
							} catch (JSONException e) {
								e.printStackTrace();
								EventBus.getDefault()
										.post(new OnCancelUpdate());
								
								/*Toast.makeText(context, "解析异常",
										Toast.LENGTH_SHORT).show();*/
							}
						}
						 saveCookie(http);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						error.printStackTrace();
						EventBus.getDefault().post(new OnCancelUpdate());
						saveCookie(http);
					}
				});
	}

	/**
	 * DB检查更新
	 */
	@Override
	public void checkDbUpdate(final Activity context, String siteIdList) {
	
		if(StringUtil.isNull(siteIdList))return;
		List<String> siteIds =Arrays.asList(siteIdList.split(","));
		//if(siteIds == null || siteIds.trim().length()==0) return;
		if(siteIds == null || siteIds.size()<=0) return;

		final HttpUtils http = new HttpUtils();
		RequestVo mRequestVo = new RequestVo();
		mRequestVo.setRequestUrl(CommenUrl.checkDbUpdate);
		mRequestVo.setRequestMethod(HttpMethod.POST);
		RequestParams params = new RequestParams();
		//params.addBodyParameter("site_id", siteIds);

		JSONArray arr = new JSONArray(siteIds);
		JSONObject obj = new JSONObject();
		try {
			obj.put("site_ids", arr);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		params.addBodyParameter(new BasicNameValuePair("reqjson",obj.toString()));
		mRequestVo.setRequestParams(params);
		http.configSoTimeout(10 * 1000);

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
        mRequestVo.setRequestParams(params);
        useCookie(http);
		http.send(mRequestVo.getRequestMethod(), mRequestVo.getRequestUrl(),
				mRequestVo.getRequestParams(), new RequestCallBack<String>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!responseInfo.result.equals("")) {
							DbDialog.notifyListViewDataByServer(context,
									responseInfo.result);
							saveCookie(http);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						error.printStackTrace();
						saveCookie(http);
					}
				});
	}
}
