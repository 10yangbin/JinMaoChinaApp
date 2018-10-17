package com.guomao.propertyservice.dao;

import java.io.File;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_base extends SQLiteOpenHelper {

	private static SQLiteDatabase DB;
	private static final int DATABASE_VERSION = 1;
	private static DB_base instance;
	private static String err_msg;
	// EQ设备表
	public static final String EQ = "EQ";
	// SITE项目表
	public static final String SITE = "SITE";
	// BL 项目组团信息表
	public static final String BL = "BL";
	// VN供应商表
	public static final String VN = "VN";
	// RM空间表/设施表
	public static final String RM = "RM";
	// CSM_BUILDING客服系统楼栋信息表
	public static final String CSM_BUILDING = "CSM_BUILDING";
	// CSM_RM客服系统房间表
	public static final String CSM_RM = "CSM_RM";
	// WAREHOUSE仓库表
	public static final String WAREHOUSE = "WAREHOUSE";
	// PT备品备件表
	public static final String PT = "PT";
	// AFM_USERS用户信息表
	public static final String AFM_USERS = "AFM_USERS";
	// USER_SITE用户所属项目信息表
	public static final String USER_SITE = "USER_SITE";
	// T_REASON_MAIN原因大类表(客服系统)
	public static final String T_REASON_MAIN = "T_REASON_MAIN";
	// T_REASON_DETAIL原因细类(客服系统)
	public static final String T_REASON_DETAIL = "T_REASON_DETAIL";
	// TT_REASON_DETAIL_SUB原因子类(客服系统)
	public static final String TT_REASON_DETAIL_SUB = "TT_REASON_DETAIL_SUB";
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
	static String site_id = null;
	static String DB_NAME_BASE = null;
	static UserBiz ubiz = null;

	public DB_base(Context context) {
		super(context, "longfornew.db", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 执行建表sql语句
		initTable(DB);
	}

	private static void initTable(SQLiteDatabase db) {
		createUserTable(db);
		createEQTable(db);
		createSiteTable(db);
		createBLTable(db);
		createVNTable(db);
		createRMTable(db);
		createCSM_BUILDINGTable(db);
		createCSM_RMTable(db);
		createWAREHOUSETable(db);
		createPTTable(db);
		createAFM_USERSTable(db);
		createUSER_SITETable(db);
		createT_REASON_MAINTable(db);
		createT_REASON_DETAILTable(db);
		createTT_REASON_DETAIL_SUBTable(db);
		createOfflineOperateTable(db);
		// createUpdateTimeTable(db);
	}

	/**
	 * 获取DB 类的实例
	 * 
	 * @param context
	 * @return
	 */
	public static DB_base getInstance() {
		if (instance == null) {
			instance = new DB_base(MainApplication.getInstance());
		}
		return instance;
	}

	/**
	 * 打开数据库的方法，未完善
	 * 
	 * @param path
	 * @param user
	 * @param pwd
	 * @return
	 */

	public static SQLiteDatabase db_open(String path, String user, String pwd) {
		// js调用成功db_open，会在logcat中打印此语句。
		SharedPrefUtil.setMsg("DB_OPEN_PATH", "PATH", path);
		SharedPrefUtil.setMsg("DB_OPEN_PATH", "USER", user);
		SharedPrefUtil.setMsg("DB_OPEN_PATH", "PWD", pwd);
		path = DataFolder.getAppDataRoot() + "db/" + path;
		L.d("db_open数据库接口 path=" + path);
		// 创建或者打开数据库，此处需要用绝对路径
		if (DB != null && DB.isOpen() && DB.getPath().equals(path)) {
			return DB;
		}
		DB = SQLiteDatabase.openOrCreateDatabase(path, null);
		return DB;
	}

	public static void reOpenDB(){
		db_open(SharedPrefUtil.getMsg("DB_OPEN_PATH", "PATH"), SharedPrefUtil.getMsg("DB_OPEN_PATH", "USER"), SharedPrefUtil.getMsg("DB_OPEN_PATH", "PWD"));
	}

	/**
	 * 判断db是否存在
	 * 
	 * @param dbName
	 * @return
	 */
	public static boolean isDbExists(String dbName) {
		String path = DataFolder.getAppDataRoot() + "db/" + dbName;
		File file = new File(path);
        return file.exists();
    }

	/**
	 * 关闭数据库的方法
	 * 
	 * @param db_handle
	 */
	public static void db_close(int db_handle) {
		L.d("db_close关闭数据库接口");
		if (instance != null) {
			try {
				if (DB != null) {
					DB.close();
				}
			} catch (Exception e) {
				L.printStackTrace(e);
			}
			instance = null;
		}
	}

	/**
	 * 数据库执行功能 ：执行insert、delete、update型的sql，返回true为成功，false为失败
	 * 
	 * @param db_handle
	 *            打开的数据库句柄，代表要操作哪个数据库
	 * @param sql
	 *            查询语句，支持参数替代符，ex：DELETE FROM user WHERE id = :id AND stat =
	 *            :stat
	 * @param param
	 *            一个json字符串，json的内容为参数的键值对， ex: { ":id":5, ":stat":"act" }
	 */
	public synchronized static Boolean db_exec(int db_handle, String sql,
			String param) {
		L.d("db_exec数据库接口");
		// 需要加入逻辑判断，然后可以通过一下的方法执行nsert、delete、update型的sql
		// Cursor cursor =DB.insert(table, nullColumnHack, values);
		// Cursor cursor =DB.delete(table, whereClause, whereArgs);
		// Cursor cursor =DB.update(table, values, whereClause, whereArgs);
		try {
			if(DB == null){
				reOpenDB();
			}
			DB.execSQL(sql);
			return true;
		} catch (SQLException e) {
			err_msg = e.getMessage();
			return false;
		}

	}

	/**
	 * 数据库查询功能
	 * 
	 * @param db_handle
	 * @param sql
	 *            查询语句，支持参数替代符，ex：SELECT * FROM user WHERE id = :id AND stat =
	 *            :stat
	 * @param param
	 *            一个json字符串，json的内容为参数的键值对， ex: { ":id":5, ":stat":"act" }
	 */
	public static JSONArray db_select(int db_handle, String sql, String param) {
		L.d("db_select打开数据库接口");
		// DB.rawQuery();没有加入逻辑判断
		if(DB == null){
			reOpenDB();
		}
		Cursor cursor = DB.rawQuery(sql, null);
		if (cursor != null && cursor.isBeforeFirst()) {
			JSONArray arr = new JSONArray();
			JSONObject obj = null;
			String[] colums = cursor.getColumnNames();
			int count = cursor.getColumnCount();

			while (cursor.moveToNext()) {
				obj = new JSONObject();
				for (int i = 0; i < count; i++) {
					try {
						obj.put(colums[i], cursor.getString(i));
					} catch (JSONException e) {
						L.printStackTrace(e);
					}
				}
				arr.put(obj);
			}
			cursor.close();
			return arr;
		}
		return null;
	}

	/**
	 * 当一次查询完成后，可通过此方法获取数据库错误信息，如果查询没有出差则返回零长度字符串""，否则返回错误信息字符串
	 * 
	 * @param db_handle
	 *            打开的数据库句柄，代表要操作哪个数据库
	 */
	public static String db_err(int db_handle) {
		// 待开发
		L.d("db_err打开数据库接口");
		if (!StringUtil.isNull(err_msg)) {
			String error = err_msg;
			err_msg = null;
			return error;
		}
		return "";
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private static void createUserTable(SQLiteDatabase db) {
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

	private static void createEQTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + EQ + "(" + " EQ_ID VARCHAR PRIMARY KEY,"
				+ " CSI_ID  VARCHAR NOT NULL," + " SITE_ID VARCHAR NOT NULL,"
				+ " BL_ID VARCHAR NOT NULL," + " FM_CODE VARCHAR NOT NULL,"
				+ " NAME  VARCHAR," + " PARAMETER1  VARCHAR,"
				+ " PARAMETER2  VARCHAR," + " PARAMETER3  VARCHAR,"
				+ " POWER  VARCHAR," + " BRAND  VARCHAR," + " MODEL  VARCHAR,"
				+ " FACTORY  VARCHAR," + " SUBMIT_DATE   DATETIME,"
				+ " GUARANTEE_DATE  VARCHAR," + " BEGIN_DATE   DATETIME,"
				+ " FIRST_SERVICE_ASSESS  VARCHAR,"
				+ " SECOND_SERVICE_ASSESS  VARCHAR,"
				+ " VIRTUAL_SPACE_SITE  VARCHAR,"
				+ " SITE_DESCRIPTION  VARCHAR," + " USE_DEPT  VARCHAR,"
				+ " SERVICE_DEPT  VARCHAR," + " SERVICE_NUM  VARCHAR,"
				+ " WORK_TIME  VARCHAR," + " MAINTENANCE_UNIT_NUM  VARCHAR,"
				+ " SUPERIOR_CODE  VARCHAR," + " SUPERIOR_NUM  VARCHAR,"
				+ " DIRECTION  VARCHAR," + " FAULT_TYPE  VARCHAR,"
				+ " FAULT_DESCRIBE  VARCHAR," + " REMARK VARCHAR,"
				+ " TRANSFER INTEGER," + " KEY_PARTS VARCHAR,"
				+ " POWER_PROPERTY  VARCHAR," + " POWER_TYPE VARCHAR,"
				+ " DUTY_MAN VARCHAR," + " LAST_UPDATE_DATE DATETIME" + ")");
	}

	private static void createSiteTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + SITE + "(" + " SITE_ID VARCHAR PRIMARY KEY,"
				+ " NAME VARCHAR NOT NULL," + " REGION VARCHAR,"
				+ " POSTCODE VARCHAR," + " ADDRESS    VARCHAR,"
				+ " CAMP_TIMEME VARCHAR," + " AREA    NUMBER,"
				+ " TOTAL_AREA NUMBER," + " PLOT_RATIO NUMBER,"
				+ " GREEN_AREA NUMBER," + " CLEAN_AREA NUMBER,"
				+ " CHARGE_AREA NUMBER," + " HOUSE_AREA  NUMBER,"
				+ " HOUSE_NUM   NUMBER," + " OCCUPANCY_NUM NUMBER,"
				+ " OCCUPANCY_RATE NUMBER," + " CARPORT_AREA NUMBER,"
				+ " CARNUM NUMBER," + " SHOP_AREA NUMBER,"
				+ " SHOP_NUM  NUMBER," + " COMPLETED_TIME DATETIME,"
				+ " FORMATS VARCHAR," + " PROJECT_ID VARCHAR,"
				+ " LAST_UPDATE_DATE DATETIME" + ")");
	}

	private static void createBLTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + BL + "(" + " BL_ID VARCHAR PRIMARY KEY,"
				+ " SITE_ID VARCHAR NOT NULL," + " NAME VARCHAR" + ")");
	}

	private static void createVNTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + VN + "(" + " SUPPLIER_CODE  VARCHAR,"
				+ " TYPE VARCHAR," + " NAME VARCHAR," + " ADDRESS   VARCHAR,"
				+ " CONTACTS  VARCHAR," + " CONTACTS_PHONE VARCHAR,"
				+ " GRADE  VARCHAR," + " CERTIFICATE VARCHAR,"
				+ " CERTIFICATE_EXPIRE_TIME  VARCHAR,"
				+ " APPROVE_DATE DATETIME," + " REMARKS  VARCHAR,"
				+ " LAST_UPDATE_DATE  DATETIME" + ")");
	}

	private static void createRMTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + RM + "(" + " RM_ID    VARCHAR PRIMARY KEY,"
				+ " CSI_ID   VARCHAR NOT NULL," + " SITE_ID  VARCHAR NOT NULL,"
				+ " BL_ID    VARCHAR NOT NULL," + " FM_CODE  VARCHAR NOT NULL,"
				+ " NAME  VARCHAR," + " ALIAS VARCHAR," + " CSM_RM_ID VARCHAR,"
				+ " CSM_RM_CODE VARCHAR," + " SITE_DESCRIPTION VARCHAR,"
				+ " REMARK VARCHAR," + " LAST_UPDATE_DATE DATETIME" + ")");
	}

	private static void createCSM_BUILDINGTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + CSM_BUILDING + "("
				+ "BUILDING_ID VARCHAR PRIMARY KEY,"
				+ "PROJECT_ID VARCHAR NOT NULL," + "BUILDING_NAME VARCHAR,"
				+ "ORIGIN_BUILDING_ID VARCHAR," + "IS_BUILDING INTEGER,"
				+ "GROUP_GUID VARCHAR" + ")");
	}

	private static void createCSM_RMTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + CSM_RM + "("
				+ "ROOM_ID  VARCHAR PRIMARY KEY,"
				+ "BUILDING_ID VARCHAR NOT NULL," + "PROJECT_ID  VARCHAR,"
				+ "ROOM_TYPE   VARCHAR," + "ROOM_CODE   VARCHAR,"
				+ "ROOM_NAME   VARCHAR," + "PRODUCT_CODE VARCHAR,"
				+ "PRODUCT_TYPE VARCHAR," + "ROOM_AREA    VARCHAR,"
				+ "CONTRACT_DELIVERY_DATE DATETIME,"
				+ "CONTRACT_SIGN_DATE DATETIME,"
				+ "FACT_DELIVERY_DATE DATETIME," + "DELIVERY_STATUS VARCHAR,"
				+ "ROOM_STATUS VARCHAR," + "PROPERTY_CODE VARCHAR,"
				+ "SOURCE_FLAG INTEGER," + "ROOM_MEMO  VARCHAR,"
				+ "UPDATE_MAN VARCHAR," + "UPDATE_TIME DATETIME,"
				+ "LIVING_STATUS VARCHAR ," + "IS_REFINE INTEGER,"
				+ "CONCENTRATE_DELIVERY_DATE DATETIME," + "BATCH VARCHAR,"
				+ "CONCENTRATE_DELIVERY_BEGIN DATETIME,"
				+ "CONCENTRATE_DELIVERY_END DATETIME" + ")");
	}

	private static void createWAREHOUSETable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + WAREHOUSE + "("
				+ "WAREHOUSE_CODE  VARCHAR PRIMARY KEY," + "NAME VARCHAR,"
				+ "SITE_ID  VARCHAR," + "BUILDING VARCHAR," + "FLOOR  VARCHAR,"
				+ "HOUSE  VARCHAR," + "ADDRESS  VARCHAR" + ")");
	}

	private static void createPTTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + PT + "(" + "PART_ID VARCHAR PRIMARY KEY,"
				+ "CSI_ID  VARCHAR NOT NULL,"
				+ "WAREHOUSE_CODE VARCHAR NOT NULL," + "NAME   VARCHAR ,"
				+ "PARAMETER1  VARCHAR," + "PARAMETER2  VARCHAR,"
				+ "PARAMETER3  VARCHAR," + "BRANDS      VARCHAR,"
				+ "COMPANY_NAME VARCHAR," + "TYPE         VARCHAR,"
				+ "AMOUNT       INTEGER," + "STATUS       VARCHAR,"
				+ "MIN_INVENTORY   INTEGER," + "PRODUCERS_NAME  VARCHAR ,"
				+ "WAREHOUSE_ENTRY_DATE DATETIME," + "CONTRACT_TERM   VARCHAR,"
				+ "DATE_OF_BORROW  DATETIME," + "TAKE_DATE       DATETIME,"
				+ "USE_FOR         VARCHAR ," + "PURCHASE_NUM    VARCHAR ,"
				+ "INSTRUCTIONS    VARCHAR ," + "REMARKS         VARCHAR ,"
				+ "PURCHASE_PHONE  VARCHAR ," + "PURCHASE_NAME   VARCHAR ,"
				+ "NOW_WAREHOUSE_AMOUNT VARCHAR " + ")");
	}

	private static void createAFM_USERSTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + AFM_USERS + "("
				+ "USER_NAME VARCHAR PRIMARY KEY," + "USER_ID VARCHAR,"
				+ "USER_PWD VARCHAR," + "USER_ALIAS VARCHAR,"
				+ "MOBILE VARCHAR" + ")");
	}

	private static void createUSER_SITETable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + USER_SITE + "(" + "ID INTEGER PRIMARY KEY,"
				+ "USER_ID VARCHAR," + "SITE_ID VARCHAR,"
				+ "CSM_PROJECT_ID VARCHAR," + "IS_MONITOR VARCHAR" + ")");
	}

	private static void createT_REASON_MAINTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + T_REASON_MAIN + "("
				+ "REASON_MAIN_ID INTEGER PRIMARY KEY," + "REASON_MAIN VARCHAR"
				+ ")");
	}

	private static void createT_REASON_DETAILTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + T_REASON_DETAIL + "("
				+ "REASON_DETAIL_ID INTEGER PRIMARY KEY,"
				+ "REASON_MAIN_ID INTEGER,"
				+ "PARENT_REASON_DETAIL_ID INTEGER," + "REASON_DETAIL VARCHAR,"
				+ "REASON_COMMENT VARCHAR" + ")");
	}

	private static void createTT_REASON_DETAIL_SUBTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE + TT_REASON_DETAIL_SUB + "("
				+ "REASDEL_SUB_ID INTEGER PRIMARY KEY,"
				+ "REASON_DETAIL_ID INTEGER,"
				+ "PARENT_REASDEL_SUB_ID INTEGER," + "REASDEL_SUB VARCHAR,"
				+ "REASON_COMMENT VARCHAR" + ")");
	}

	private static void createOfflineOperateTable(SQLiteDatabase db) {
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

	// private static void createUpdateTimeTable(SQLiteDatabase db){
	// db.execSQL(CREATE_TABLE +"update_time" +"("
	// + "table_name VARCHAR PRIMARY KEY ,"
	// + "last_update_time DATETIME " +")"
	// );
	// }

	// public static void setUpdateTime(String tableName,Date d,boolean
	// isNeedCloseDB){
	// SimpleDateFormat formator = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// try {
	// String time = formator.format(d);
	// db_open("longfor.db","","");
	// if(StringUtil.isNull(tableName)){
	// DB.execSQL("UPDATE update_time SET last_update_time = '"+time+"' WHERE table_name !='"+TASKMESSAGE+"'");
	// }else{
	//
	// boolean isExist = isExist("update_time", "table_name", tableName);
	// if(isExist){
	// DB.execSQL("UPDATE update_time SET last_update_time = '"+time+"' WHERE table_name ='"+tableName+"'");
	// }else{
	// DB.execSQL("INSERT INTO update_time (last_update_time,table_name) VALUES ('"+time+"'"
	// + ",'"+tableName+"')");
	// }
	//
	// }
	// } catch (Exception e) {
	// L.printStackTrace(e);
	// }finally{
	// if(isNeedCloseDB){
	// db_close(0);
	// }
	// }
	// }
	public static String getLastUpdateTime(String tableName,
			boolean isNeedCloseDB) {
		String time = null;
		Cursor c = null;
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		String sql = "SELECT MAX(DMS_UPDATE_TIME) FROM " + tableName;
		try {
			db_open(DB_NAME_BASE, "", "");
			c = DB.rawQuery(sql, null);

			if (c != null && c.moveToNext()) {
				time = c.getString(0);
				if (time.length() > 19) {
					time = time.substring(0, time.length() - 2);
				}

			}
		} catch (Exception e) {
			L.printStackTrace(e);
		} finally {
			if (c != null) {
				c.close();
			}
			if (isNeedCloseDB) {
				db_close(0);
			}
		}
		return time == null ? null : time.trim();
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

	public static String GetWhereSql(List<String> columns) {
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

	public static boolean isExist(String tableName, String whereColoum,
			String whereArg) {
		boolean result = false;
		if (StringUtil.isNull(tableName)) {
			return result;
		}
		Cursor c = null;
		String sql = null;
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		if (StringUtil.isNull(whereColoum) && StringUtil.isNull(whereArg)) {
			sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "' ";

		} else if ((!StringUtil.isNull(whereColoum))
				&& (!StringUtil.isNull(whereArg))) {
			sql = "select count(*) as c from " + tableName + " where "
					+ whereColoum + " ='" + whereArg + "' ";
		} else {
			return result;
		}
		try {
			c = db_open(DB_NAME_BASE, "", "").rawQuery(sql, null);

			if (c.moveToNext()) {
				int count = c.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	public static boolean isExist(String tableName, String whereCause) {
		boolean result = false;
		if (StringUtil.isNull(tableName)) {
			return result;
		}
		Cursor c = null;
		String sql = null;
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		if (StringUtil.isNull(whereCause)) {
			sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "' ";

		} else if (!StringUtil.isNull(whereCause)) {
			sql = "select count(*) as c from " + tableName + " where "
					+ whereCause;
		} else {
			return result;
		}
		try {
			c = db_open(DB_NAME_BASE, "", "").rawQuery(sql, null);

			if (c.moveToNext()) {
				int count = c.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}
}
