package com.guomao.propertyservice.dao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * 抽象的dao的基类
 * 
 * @author Administrator
 * 
 */
public abstract class AbstractDao {
	private static final String TAG = "AbstractDao";

	public static final String ALL_COUNT_COLUMN = "count(*)";

	private SQLiteDatabase readDb;
	private SQLiteDatabase writeDb;
	protected Context context;
	protected SimpleDateFormat formator = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	String DB_NAME = null;
	String DB_NAME_BASE = null;
	String DB_NAME_BUSINESS = null;

	private static Object writeSyncObj = new Object();

	public AbstractDao(SQLiteDatabase readDb, int dbType,
			SQLiteDatabase writeDb, Context context) {
		this.readDb = readDb;
		this.writeDb = writeDb;
		this.context = context;
		formator.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
	}

	protected SQLiteDatabase getReadDb(int dbType) {
		Log.i(TAG, "getReadDb");
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		DB_NAME_BUSINESS = site_id + "business.db";
		if (dbType == 1) {
			DB_NAME = DB_NAME_BASE;
			readDb = DB_base.db_open(DB_NAME, "", "");
		} else if (dbType == 2) {
			DB_NAME = DB_NAME_BUSINESS;
			readDb = DB_business.db_open(DB_NAME, "", "");
		}
		return readDb;
	}

	protected SQLiteDatabase getWriteDb(int dbType) {
		Log.i(TAG, "getWriteDb");
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		DB_NAME_BUSINESS = site_id + "business.db";
		if (dbType == 1) {
			DB_NAME = DB_NAME_BASE;
			writeDb = DB_base.db_open(DB_NAME, "", "");
		} else if (dbType == 2) {
			DB_NAME = DB_NAME_BUSINESS;
			writeDb = DB_business.db_open(DB_NAME, "", "");
		}
		return writeDb;
	}

	public static String GetWhereSql(String... columns) {
		StringBuilder sbWhere = new StringBuilder();
		int i = 0;
		for (String column : columns) {
			if (i++ > 0) {
				sbWhere.append("and ");
			}
			sbWhere.append(column);
			sbWhere.append("=? ");
		}
		return sbWhere.toString();
	}

	public static String GetWhereCause(String[] columns, Object[] values) {
		StringBuilder sbWhere = new StringBuilder();
		int i = 0;
		for (String column : columns) {
			if (i++ > 0) {
				sbWhere.append("and ");
			}
			sbWhere.append(column);
			if (values[i - 1] instanceof String) {
				sbWhere.append("= '" + values[i - 1] + "' ");
			} else {
				sbWhere.append("=" + values[i - 1] + " ");
			}
		}
		return sbWhere.toString();
	}

	public static String GetWhereCause(List<String> columns, Object[] values) {
		StringBuilder sbWhere = new StringBuilder();
		int i = 0;
		for (String column : columns) {
			if (i++ > 0) {
				sbWhere.append("and ");
			}
			sbWhere.append(column);
			if (values[i - 1] instanceof String) {
				sbWhere.append("= '" + values[i - 1] + "' ");
			} else {
				sbWhere.append("=" + values[i - 1] + " ");
			}
		}
		return sbWhere.toString();
	}

	public static String sqlEscape(String orgSql) {
		return orgSql.replace("'", "''");
	}
}
