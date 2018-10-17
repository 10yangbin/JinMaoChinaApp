package com.guomao.propertyservice.dao;

import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.model.user.UserDao;
import com.guomao.propertyservice.util.GsonUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * 用户数据信息,dao的实现
 * 
 * 设置，获取当前用户，getUserById,updateUser
 * @author Administrator
 *
 */
public class UserDaoImpl extends AbstractDao implements UserDao {

	public UserDaoImpl(SQLiteDatabase readDb, SQLiteDatabase writeDb,
			Context context) {
		super(readDb, 1, writeDb, context);
	}

	public static final String USER_TABLE = "user";
	public static final String USER_ID_COLUMN = "user_id";
	public static final String USER_NAME_COLUMN = "user_name";
	public static final String USER_ALIAS_COLUMN = "user_alias";
	public static final String MOBILE_COLUMN = "mobile";
	public static final String IS_MONITOR_COLUMN = "is_monitor";
	public static final String SITE_ID_LIST_COLUMN = "site_id_list";
	public static final String EMAIL_COLUMN = "email";
	public static final String USER_PWD_COLUMN = "user_pwd";
	public static final String REASON_MAIN_ID_COLUMN = "reason_main_id";

	private static User u;
	

	@Override
	public synchronized User getCurrentUser() {
		if (u == null) {
			String userId = SharedPrefUtil.getUserID();
			if (StringUtil.isNull(userId)) {
				return null;
			} else {
				u = getUserById(userId);
			}
		}
		return u;
	}

	@Override
	public synchronized void setCurrentUser(User u) {
		UserDaoImpl.u = u;
		SharedPrefUtil.putUserID(u == null ? "" : u.getUser_id());
		updateUser(u);
	}

	public User getUserById(String userId) {
		if (!StringUtil.isNull(userId)) {
			String userInfo = SharedPrefUtil.getMsg("userinfo", userId);
			return GsonUtil.gson().fromJson(userInfo, User.class);
		}
		return null;
	}

	public void updateUser(User u) {
		if (u == null) {
			UserDaoImpl.u = null;
			return;
		}
		SharedPrefUtil.setMsg("userinfo", u.getUser_id(), GsonUtil.gson()
				.toJson(u));
	}
}
