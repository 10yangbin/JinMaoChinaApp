package com.guomao.propertyservice.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.guomao.propertyservice.dao.AbstractDao;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.PropertiesUtils;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class BusinessProcessService {

	/**
	 * 在线业务请求和离线提交返回数据的保存
	 * @param db
	 * @param tab
	 * @param data
	 */
	public static void saveData2DB(SQLiteDatabase db, String tableName,
			JSONArray data) {

		if (StringUtil.isNull(tableName) || data == null || data.length() <= 0) {
			return;
		}
		String pKey = PropertiesUtils.getValue(MainApplication.getInstance(),
				tableName.toUpperCase());
		String[] keys = null;
		if (!StringUtil.isNull(pKey) && pKey.contains(",")) {
			keys = pKey.split(",");
		} else {
			keys = new String[] { pKey };
		}
		saveData(db, data, tableName, Arrays.asList(keys));
	}

	private static void saveData(SQLiteDatabase db, JSONArray data,
			String tableName, List<String> keys) {
		if (StringUtil.isNull(tableName) || data == null) {
			return;
		}

		ArrayList<Object> pValue;
		JSONObject obj = null;
		/*SharedPrefUtil.Log("业务请求----数据开始插入时间:"
				+ " insert start----> ");*/
		for (int i = 0; i < data.length(); i++) {
			try {
				obj = data.optJSONObject(i);
				if (obj != null) {
					ContentValues values = json2ContentValues(obj);
					if (keys == null || keys.size() == 0) {
						db.insert(tableName, null, values);
						/*SharedPrefUtil.Log("insert success ---->"
								+ obj.toString());*/
					} else {
						ArrayList<String> myKeys = new ArrayList<String>(keys);
						pValue = new ArrayList<Object>();
						Object value = null;
						for (int j = 0; j < myKeys.size(); j++) {
							value = obj.opt(myKeys.get(j));
							if (value == null
									|| StringUtil.isNull(value.toString())) {
								myKeys.remove(j);
								j--;
							} else {
								pValue.add(value);
							}
						}
						Object[] args = pValue.toArray();
						if (DB_business.isExist(tableName,
								AbstractDao.GetWhereCause(myKeys, args))) {
							String argsStr = Arrays.toString(args);
							String[] strs = argsStr.substring(1,
									argsStr.length() - 1).split(", ");
							db.update(tableName, values,
									DB_business.GetWhereSql(myKeys), strs);
						} else {
							db.insert(tableName, null, values);
						}
					}

				}

			} catch (Exception e) {
				SharedPrefUtil.Log("BusinessProcessService :" + e.getMessage());
				SharedPrefUtil.Log("BusinessProcessService :"
						+ " insert failer----> " + obj.toString());
				L.printStackTrace(e);
			}
		}
		/*SharedPrefUtil.Log("业务请求-----数据插入完成时间:"
				+ " insert over----> ");*/

	}

	static ContentValues json2ContentValues(JSONObject obj) {
		if (obj == null) {
			return null;
		}

		ContentValues contentValues = new ContentValues();
		Iterator<String> iterator = obj.keys();
		String key = null;
		Object value = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = obj.opt(key);
			if (key.equalsIgnoreCase("DMS_UPDATE_TIME")) {// 业务处理不更新此列【业务数据下载策略的一部分】
				continue;
			}

			if (value instanceof String) {
				contentValues.put(key, (String) value);
			} else if (value instanceof Integer) {
				contentValues.put(key, (Integer) value);
			} else if (value instanceof Boolean) {
				contentValues.put(key, (Boolean) value);
			} else if (value instanceof byte[]) {
				contentValues.put(key, (byte[]) value);
			} else if (value instanceof Byte) {
				contentValues.put(key, (Byte) value);
			} else if (value instanceof Short) {
				contentValues.put(key, (Short) value);
			} else if (value instanceof Long) {
				contentValues.put(key, (Long) value);
			} else if (value instanceof Float) {
				contentValues.put(key, (Float) value);
			} else if (value instanceof Double) {
				contentValues.put(key, (Double) value);
			} else if (value == null) {
				contentValues.putNull(key);
			} else if (value instanceof JSONObject) {
				contentValues.put(key,  value.toString());
				//contentValues.putAll(json2ContentValues((JSONObject) value));
			} else if (value instanceof JSONArray) {
				contentValues.put(key,  value.toString());
			}
		}
		return contentValues;

	}
}
