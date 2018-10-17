package com.guomao.propertyservice.biz.manger;

import java.util.HashMap;
import java.util.Map;

import com.guomao.propertyservice.dao.OfflineOperateDaoImpl;
import com.guomao.propertyservice.dao.UserDaoImpl;
import com.guomao.propertyservice.dao.DB_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库管理者
 * 
 * @author Administrator
 * 
 */
public class DaoManger {

	public enum DaoType {
		// 任务
		TASK_DAO,
		// 任务进度
		TASK_PROCESS_DAO,
		// 用户
		USER_DAO,
		// 离线操作
		OFFLINE_OPERATE_DAO

	}

	Map<String, Object> map = new HashMap<String, Object>();

	private Context context;
//	private DB helper;
	private SQLiteDatabase readDb;
	private SQLiteDatabase writeDb;

	public DaoManger(Context context) {

		this.context = context;
		DB_base.getInstance();
//		readDb = DB.db_open("longfor.db", "", "");
//		writeDb = readDb;
	}

	public void closeDB() {
		readDb.close();
		writeDb.close();
		DB_base.db_close(0);
	}

	/**
	 * 获得相对业务数据库操作的对象
	 * 
	 * @param type
	 * @return obj
	 */
	public Object get(DaoType type) {
		Object obj = this.map.get(type.name());
		if (obj == null) {
			obj = init(type);
			map.put(type.name(), obj);
		}
		return obj;
	}

	/**
	 * 初始化相对业务数据库操作的对象，方便调用不同业务的数据库操作
	 * 
	 * @param type
	 * @return obj
	 */
	private Object init(DaoType type) {

		Object obj = null;
	    if (type == DaoType.USER_DAO) {
			obj = new UserDaoImpl(readDb, writeDb, context);
		}else if (type == DaoType.OFFLINE_OPERATE_DAO) {
			obj = new OfflineOperateDaoImpl(readDb, writeDb, context);
		}
		return obj;
	}
}
