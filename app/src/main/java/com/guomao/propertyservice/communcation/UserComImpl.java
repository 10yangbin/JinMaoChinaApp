package com.guomao.propertyservice.communcation;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.model.user.UserCom;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBean;
import com.guomao.propertyservice.network.core.INetwork;
import com.guomao.propertyservice.network.core.NetworkImpl;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.CommenUtil;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.MD5;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 登录和注销的功能实现
 */

public class UserComImpl extends AbstractCom implements UserCom {

	User u = null;

	public UserComImpl(WebClient webClient) {
		super(webClient);
	}

	@Override
	public User login(final String username, final String password,
			final OperateCallBack operateCallBack) throws Exception {
		INetwork mNetwork = new NetworkImpl();
		RequestVo mRequestVo = new RequestVo();
		RequestParams mParams = new RequestParams();
		mRequestVo.setRequestUrl(CommenUrl.baseUrl+CommenUrl.loginAction);
		mRequestVo.setRequestMethod(HttpMethod.POST);
		JSONObject json = new JSONObject();
		String userId = SharedPrefUtil.getMsg("user_push_tag", "uid");
		MainApplication.getInstance().setUserName(username);
		MainApplication.getInstance().setPassword(password);
		try {
			json.put("user_name", username);
			json.put("user_pwd", MD5.Md5(password.trim()));
			json.put("channelid",
					CommenUtil.showImei(MainApplication.getInstance()));
			json.put("userid", userId);
		} catch (JSONException e) {
			L.printStackTrace(e);
		}
		mRequestVo.setJson(json);
		mRequestVo.setRequestParams(mParams);
		mNetwork.getData(mRequestVo, new OperateCallBack() {

			@Override
			public void onLoadSuccess(Object obj) {
				NetworkBean networkBean = new NetworkBean(String.valueOf(obj));
				if (networkBean.isSucc()) {
					JSONArray arr = networkBean.getData().optJSONObject("user").optJSONArray("tag");
					String tagStrs = null;
					try {
						tagStrs = arr.join(",");
						List<String> tags =Arrays.asList(tagStrs.split(","));
						u = gson.fromJson(
								networkBean.getData().optJSONObject("user")
								.toString(), User.class);
						u.setUser_pwd(password);
						u.setUser_name(username);
						u.setCf_type(u.getCf_type());
						u.setTag(tags);
						u.setSiteList(networkBean.getData().optJSONArray("site_list").toString());
						operateCallBack.onLoadSuccess(u);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					operateCallBack.onLoadFail(networkBean.getMessage());
				}
			}

			@Override
			public void onTokenInvalid() {
			}

			@Override
			public void onLoadFail(String msg) {
				operateCallBack.onLoadFail(msg);
			}
		});
		return u;
	}

	@Override
	public void logout(String userName, final OperateCallBack operateCallBack)
			throws Exception {
		INetwork mNetwork = new NetworkImpl();
		RequestVo mRequestVo = new RequestVo();
		RequestParams mParams = new RequestParams();
		mRequestVo.setRequestUrl(CommenUrl.baseUrl + CommenUrl.logOutAction);
		mRequestVo.setRequestMethod(HttpMethod.POST);
		JSONObject json = new JSONObject();
		json.put("user_name", userName);
		mRequestVo.setJson(json);
		mRequestVo.setRequestParams(mParams);
		mNetwork.getData(mRequestVo, new OperateCallBack() {

			@Override
			public void onLoadSuccess(Object obj) {
				NetworkBean networkBean = new NetworkBean(String.valueOf(obj));
				if (networkBean.isSucc()) {
					operateCallBack.onLoadSuccess(obj);
				}
			}

			@Override
			public void onTokenInvalid() {

			}

			@Override
			public void onLoadFail(String msg) {
				operateCallBack.onLoadFail(msg);
			}
		});
	}
	
	@Override
	public String getDeviceInfo(String url, String qrCode) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("fm_code", qrCode);
		return webClient.doJsonPost(url, obj);
	}


}
