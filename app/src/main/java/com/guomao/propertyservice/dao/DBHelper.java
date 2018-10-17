package com.guomao.propertyservice.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 实现数据库的建表和更新
 * 
 * @author Administrator
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

	private static final String DATABASE_NAME = "longforfm.db";
	private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Context context1 = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createUserTable(db);
		createOfflineOperateTable(db);

	}

	private void createUserTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + UserDaoImpl.USER_TABLE + "("
				+ UserDaoImpl.USER_ID_COLUMN + " VARCHAR PRIMARY KEY ,"
				+ UserDaoImpl.USER_NAME_COLUMN + " VARCHAR NOT NULL,"
				+ UserDaoImpl.USER_PWD_COLUMN + " VARCHAR,"
				+ UserDaoImpl.USER_ALIAS_COLUMN + " VARCHAR,"
				+ UserDaoImpl.EMAIL_COLUMN + " VARCHAR,"
				+ UserDaoImpl.IS_MONITOR_COLUMN + " INTEGER DEFAULT 0,"
				+ UserDaoImpl.MOBILE_COLUMN + " VARCHAR,"
				+ UserDaoImpl.SITE_ID_LIST_COLUMN + " VARCHAR,"
				+ UserDaoImpl.REASON_MAIN_ID_COLUMN + " VARCHAR  NOT NULL"
				+ ")");

	}


	/**
	 * 离线操作数据库表
	 * 
	 * @param db
	 */
	private void createOfflineOperateTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + OfflineOperateDaoImpl.OFFLINE_OPERATE_TABLE
				+ "(" + OfflineOperateDaoImpl.ID_COLUMN
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ OfflineOperateDaoImpl.CREATE_TIME_COLUMN + " DATETIME,"
				+ OfflineOperateDaoImpl.OPERATE_FILES_COLUMN + " VARCHAR,"
				+ OfflineOperateDaoImpl.OPERATE_A_COLUMN + " VARCHAR,"
				+ OfflineOperateDaoImpl.OPERATE_P_COLUMN + " VARCHAR,"
				+ OfflineOperateDaoImpl.OPERATE_RESULT_COLUMN + " VARCHAR,"
				+ OfflineOperateDaoImpl.OPERATE_STATUS_COLUMN + " INTEGER,"
				+ OfflineOperateDaoImpl.SUBMIT_TIME_COLUMN + " DATETIME" + ")");
	}

	/**
	 * 更新数据库版本
	 */

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public boolean tabIsExist(String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		Cursor cursor = null;
		try {

			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tabName.trim() + "' ";
			cursor = getReadableDatabase().rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor !=null){
				cursor.close();
			}
		}
		return result;
	}

}
