package com.guomao.propertyservice.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.main.MainApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefUtil {
	private static final String USER_ID_KEY = "key4longforlogin";
	private static final String SP_NAME = "longforfm";
	private static final String GONGDAN = "gongdan";
	private static final String USER_SP_NAME = "longforfmuser";
	private static final String DB_VERSION = "dbVersion";
	private static final String SQL_VERSION = "sqlVersion";
	private static final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 设置日期格式

	public static SharedPreferences getSharedPrefe(String spName) {
		return MainApplication.getInstance().getSharedPreferences(
				StringUtil.isNull(spName) ? SP_NAME : spName,
				Context.MODE_PRIVATE);
	}

	public static void putSqlVersion(String key, String version) {
		getSharedPrefe(SQL_VERSION).edit().putString(key, version).apply();
	}

	public static String getSqlVersion(String key) {
		return getSharedPrefe(SQL_VERSION).getString(key, "-1");
	}

	public static void putDbVersion(String key, String version) {
		getSharedPrefe(DB_VERSION).edit().putString(key, version).apply();
	}

	public static String getDbVersion(String key) {
		return getSharedPrefe(DB_VERSION).getString(key, "-1");
	}

	public static void putUserID(String userId) {
		getSharedPrefe(USER_SP_NAME).edit().putString(USER_ID_KEY, userId)
				.apply();
	}

	public static String getUserID() {
		return getSharedPrefe(USER_SP_NAME).getString(USER_ID_KEY, null);
	}

	// 保存执行页的信息
	public static void setMsg(String spName, String key, String msg) {
		Editor e = SharedPrefUtil.getSharedPrefe(spName).edit();
		e.putString(key, msg);
		e.apply();
	}

	// 获取执行页的信息
	public static String getMsg(String spName, String key) {
		String result = "";
		try {
			result = SharedPrefUtil.getSharedPrefe(spName).getString(key, "");
		} catch (Exception e) {
			L.printStackTrace(e);
		}
		return result;

	}

	public static void Log(String LogMsg) {
		if(Const.DEBUG){
			// LogMsg=LogMsg+"\r\n";
			File file = new File(DataFolder.getAppDataRoot()+"中化log.txt");
			
			FileWriter fw = null;
			BufferedWriter writer = null;
			try {
				fw = new FileWriter(file, true);
				writer = new BufferedWriter(fw);
				writer.write("TIME:" + df.format(new Date()));
				writer.write("\t" + LogMsg + "\r\n");
				writer.newLine();// 换行
				writer.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	}
