package com.guomao.propertyservice.biz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.eventbus.OnPushOtherSite;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.model.user.UserCom;
import com.guomao.propertyservice.model.user.UserDao;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;


/**
 * 用户的信息管理,登录，退出，踢人等的维护
 * 
 * @author Administrator
 * 
 */
public class UserBiz {
	private static final String TAG = null;
	// private Activity context;
	private UserDao dao;
	private UserCom com;

	public UserBiz(UserDao dao, UserCom com) {
		this.dao = dao;
		this.com = com;
	}

	public UserDao getDao() {
		return dao;
	}


	/**
	 * 用户登录
	 * @param username
	 * @param pwd
	 * @param operateCallBack
	 * @return
	 * @throws Exception
	 */
	public boolean login(String username, String pwd,
			final OperateCallBack operateCallBack) throws Exception {
		try {
			com.login(username, pwd, new OperateCallBack() {

				@Override
				public void onTokenInvalid() {

				}

				@Override
				public void onLoadSuccess(Object obj) {
					User user = null;
					try {
						user = (User) obj;
						dao.setCurrentUser(user);
						EventBus.getDefault().post(new OnPushOtherSite());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (user != null) {
						operateCallBack.onLoadSuccess(user);
					}
				}

				@Override
				public void onLoadFail(String msg) {
					operateCallBack.onLoadFail(msg);
				}
			});
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw new Exception("您的网络不给力");
			} else {
				throw e;
			}
		}
		return false;
	}

	/**
	 * 判断当前账号是否登录
	 * @return
	 */
	public boolean isLogin() {
		return dao.getCurrentUser() != null;
	}

	/**
	 * 获取当前用户信息
	 * @return
	 */
	public User getCurrentUser() {
		return dao.getCurrentUser();
	}


	/**
	 * 获取用户id
	 * @return
	 */
	public String getUserId() {
		User u = getCurrentUser();
		return u == null ? null : u.getUser_id();
	}



	/**
	 * 用户登出账号,发请求，清空用户信息
	 * @param operateCallBack
	 * @throws Exception
	 */
	public void logout(final OperateCallBack operateCallBack) throws Exception {
		User u = getCurrentUser();
		if(u!=null){
			SharedPrefUtil.setMsg("LogOutSiteId", "SiteId", u.getSite_id());
		}
		if (StringUtil.isNull(u.getUser_id())) {
			List<String> tag = getCurrentUser().getTag();
			dao.setCurrentUser(null);
			String isLogoutSucc = "true";
			operateCallBack.onLoadSuccess(isLogoutSucc);
		} else if (MainApplication.IS_ON_LINE) {
			// isOk = com.logout(u.getUser_name());
			com.logout(u.getUser_name(), new OperateCallBack() {

				@Override
				public void onTokenInvalid() {

				}

				@Override
				public void onLoadSuccess(Object obj) {
					List<String> tag = getCurrentUser().getTag();
					//					List<Site> siteList = new ArrayList<Site>();
					//					Site site = new Site();
					//					siteList = site.getSiteList(getCurrentUser().getSiteList());
					unRegisteXG(tag);
					dao.setCurrentUser(null);
					String isLogoutSucc = "true";
					operateCallBack.onLoadSuccess(isLogoutSucc);
					//百度推送代码
					//					if (!StringUtil.isNull(tag))
					//						PushManager.delTags(MainApplication.getInstance(),
					//								Arrays.asList(tag.split(",")));
					//					PushManager.stopWork(MainApplication.getInstance());
				}

				@Override
				public void onLoadFail(String msg) {
					dao.setCurrentUser(null);
					String isLogoutSucc = "true";
					operateCallBack.onLoadSuccess(isLogoutSucc);
				}
			});
		} else {
			List<String> tag = getCurrentUser().getTag();
			dao.setCurrentUser(null);
			String isLogoutSucc = "true";
			operateCallBack.onLoadSuccess(isLogoutSucc);
		}

		// if (isOk) {
		// String tag = u.getTag();
		// dao.setCurrentUser(null);
		// if (!StringUtil.isNull(tag))
		// PushManager.delTags(MainApplication.getInstance(),
		// Arrays.asList(tag.split(",")));
		// PushManager.stopWork(MainApplication.getInstance());
		// }

	}

	/**
	 * 退出app,或者被其他人顶掉,删除推送tag,同时后台也要做删除tag的处理
	 * @param tag
	 */
	public void unRegisteXG(final List<String> tag) {
		final Context context = MainApplication.getInstance()
				.getApplicationContext();
		XGPushManager.unregisterPush(context, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object arg0, int arg1) {
				/*for (int i = 0; i < siteList.size(); i++) {
					XGPushManager.deleteTag(context, siteList.get(i));
				}*/
				List<String> tagList = new ArrayList<String>();
				tagList = tag;
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
				/*for (int i = 0; i < siteList.size(); i++) {
					XGPushManager.deleteTag(context, siteList.get(i));
				}*/

				List<String> tagList = new ArrayList<String>();
				tagList = tag;
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
		});
	}

	/**
	 * 同一账号,不能同时两个手机登录
	 */
	public void logoutByOtherLogin() {
		User u = getCurrentUser();
		if (u != null) {
			List<String> tag = u.getTag();
			//			List<Site> siteList = new ArrayList<Site>();
			//			Site site = new Site();
			//			siteList = site.getSiteList(getCurrentUser().getSiteList());
			unRegisteXG(tag);
			dao.setCurrentUser(null);
			//百度推送代码
			/*PushManager.delTags(MainApplication.getInstance(),
					Arrays.asList(tag.split(","))); // 删除tag调用的方法
			PushManager.stopWork(MainApplication.getInstance());
			 */
		}

	}

	/**
	 * 二维码扫码功能
	 * @param qrCode
	 * @return
	 * @throws Exception
	 */
	public String getDeviceInfo(String qrCode) throws Exception {
		String url = MainApplication.finalUrl + "GeneralQRInforms.do";
		return com.getDeviceInfo(url, qrCode);
	}

	public boolean hasPermission(String code){
		User u = getCurrentUser();
		if(u == null){
			return false;
		}
		return u.hasPermission(code);
	}
}
