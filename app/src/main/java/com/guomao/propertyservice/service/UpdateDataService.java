package com.guomao.propertyservice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.dao.AbstractDao;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.file.FileUitl;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.mydao.FileDao;
import com.guomao.propertyservice.mydaoimpl.FileDaoImpl;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBean;
import com.guomao.propertyservice.network.core.INetwork;
import com.guomao.propertyservice.network.core.NetworkImpl;
import com.guomao.propertyservice.network.request.DownLoadFileVo;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.PropertiesUtils;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;
  

/**
 * 基础数据同步和返回数据的插入 
 * @author Administrator
 *
 */
public class UpdateDataService extends Service {
	private String d_type;
	public static final int PAGE_SIZE = 1000;
	private List<String> tableLists;
	private int currentPage = 0;

	private static final String[] UPDATE_TABLE_NAMES = { "EQ", "SITE", "BL",
			"RM","USER_SITE","AFM_USERS","CSI","CUSTOMER","CUSTOMER_QR"};
	private static final String[] TABLE_NAMES_USERS = {"USER_SITE","AFM_USERS"};
	private static final String[] TABLE_NAMES_PROJECT = { "SITE","CUSTOMER","CUSTOMER_QR"};
	private static final String[] TABLE_NAMES_ROOMS = { "BL","RM"};
	private static final String[] TABLE_NAMES_DEVICE = {"EQ","CSI"};
	
	private String[] currentTables = new String[0];
	
	//时间map
	private static HashMap<String, String> TIMES_MAP = null;
    private final String MAX_COUNT = "total";
	String data = "";
	boolean isNeedContiue = true;
	JSONArray arr = null;
	SQLiteDatabase db;
	static String DB_NAME = null;
	String site_id = null;
	static String DB_NAME_BASE = null;
	static String DB_NAME_BUSINESS = null;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (MainApplication.IS_UPDATE_START) {
			stopSelf();
		} else {
			try {
				d_type = intent.getStringExtra("d_type");
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 为什么再次判断是否正在下载基础数据
			if (MainApplication.IS_UPDATE_START) {
				stopSelf();
			}
			
			MainApplication.IS_UPDATE_START = true;
			Intent start = new Intent(Const.ACTION_DATA_DOWNLOAD_START);
			sendBroadcast(start);
			if (!StringUtil.isNull(d_type)) {
				currentTables = getTableNames(d_type);
			} else {
				currentTables = UPDATE_TABLE_NAMES;
			}

			TIMES_MAP = new HashMap<String, String>();

			arr = new JSONArray();
			
			tableLists = new ArrayList<String>(Arrays.asList(currentTables));

            UserBiz uBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);

            User currentUser = uBiz.getCurrentUser();
			if (currentUser != null) {
				site_id = currentUser.getSite_id();
				DB_NAME_BASE = site_id + "base.db";
				DB_NAME_BUSINESS = site_id + "business.db";
				DB_NAME = DB_NAME_BASE;
				db = DB_base.db_open(DB_NAME, "", "");
				if (isNeedContiue) {
					getBaseData();
				}
			} else {
				stopSelf();
			}
			
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 获取base离线库的数据
	 */
	public void getBaseData() {
		
		arr = new JSONArray();
		prepareReqParams(tableLists);
		try {
			// ????????
			getData(arr, new OperateCallBack() {
				@Override
				public void onTokenInvalid() {
				}

				@Override
				public void onLoadSuccess(Object obj) {
					NetworkBean networkBean = new NetworkBean(String
							.valueOf(obj));
					if (!networkBean.isSucc()) {
						Toast.makeText(getBaseContext(),
								networkBean.getMessage(), Toast.LENGTH_SHORT)
								.show();
					} else if (networkBean.isSucc()) {
						JSONObject retdata = networkBean.getData();
						for (String table : currentTables) {
							JSONArray ret = null;
							try {
								ret = retdata.getJSONArray(table);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (ret == null || ret.length() <= 0) {
								continue;
							}
							UpdateDataService.saveData2DB(db, 1, table, ret);
						}

						int count = currentPage * UpdateDataService.PAGE_SIZE;
						try {
							if (count >= retdata.getInt(MAX_COUNT)) {
								MainApplication.IS_UPDATE_START = false;
								if (!MainApplication.IS_SENDOFFLINE_START) {
									TIMES_MAP.clear();
									Intent end = new Intent(
											Const.ACTION_DATA_DOWNLOAD_SUCCESS);
									sendBroadcast(end);
									DB_base.db_close(0);
									stopSelf();
								}
								isNeedContiue = true;
							} else {
								getBaseData();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							isNeedContiue = true;
							MainApplication.IS_UPDATE_START = false;
							if (!MainApplication.IS_SENDOFFLINE_START) {
								Intent end = new Intent(
										Const.ACTION_DATA_DOWNLOAD_SUCCESS);
								sendBroadcast(end);
								DB_base.db_close(0);
								
							}
						}
						stopSelf();
					}

					if (isNeedContiue) {
						arr = new JSONArray();
					}

				}

				@Override
				public void onLoadFail(String msg) {
					isNeedContiue = true;
					MainApplication.IS_UPDATE_START = false;
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
					if (!MainApplication.IS_SENDOFFLINE_START) {
						Intent end = new Intent(
								Const.ACTION_DATA_DOWNLOAD_SUCCESS);
						sendBroadcast(end);
						DB_base.db_close(0);
						
					}
					stopSelf();
				}
			});

		} catch (Exception e) {
			L.printStackTrace(e);
			isNeedContiue = true;
		}
	}



	private void prepareReqParams(List<String> tableLists) {
		currentPage++;
		String tableName = null;
		JSONObject obj = null;
		for (int i = 0; i < tableLists.size(); i++) {
			tableName = tableLists.get(i);
			if (StringUtil.isNull(tableLists.get(i))) {
				continue;
			}
			obj = new JSONObject();
			try {
				obj.put("tablename", tableName);
				obj.put("lastupdatetime",
						getLastUpdateTimeStr(tableName, false));
				obj.put("page", currentPage);
				obj.put("pagesize", PAGE_SIZE);
				arr.put(obj);
			} catch (JSONException e) {
				L.printStackTrace(e);
			}
		}
	}

    private String getLastUpdateTimeStr(String tableName, boolean isNeedCloseDB) {
		String time = null;
		if (TIMES_MAP.containsKey(tableName)) {
			return TIMES_MAP.get(tableName);
		} else {
			time = DB_base.getLastUpdateTime(tableName, isNeedCloseDB);
			if (StringUtil.isNull(time)) {
				TIMES_MAP.put(tableName, "2015-04-06 08:11:22");
			} else {
				TIMES_MAP.put(tableName, time);
			}
		}
		return StringUtil.isNull(time) ? "2015-04-06 08:11:22" : time;
	}

	private String getData(JSONArray p, final OperateCallBack operateCallBack) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();

		pairs.add(new BasicNameValuePair("A", "MateData_Download"));
		pairs.add(new BasicNameValuePair("P", p.toString()));
		try {
			// ------------6.11 9:40---------------->
			RequestParams mParams = new RequestParams();
			RequestVo mRequestVo = RequestVo.getInstance();
			mRequestVo
					.setRequestUrl(CommenUrl.getActionUrl(CommenUrl.baseData));
			mRequestVo.setRequestMethod(HttpMethod.POST);
			JSONObject json = new JSONObject();
			json.put("P", p);
			mRequestVo.setJson(json);
			mRequestVo.setRequestParams(mParams);
			INetwork mNetwork = new NetworkImpl();
			mNetwork.getData(mRequestVo, new OperateCallBack() {

				@Override
				public void onLoadSuccess(Object obj) {
					NetworkBean networkBean = new NetworkBean(String
							.valueOf(obj));
					// try {
					// FileUitl.writeReturnData(currentPage,
					// String.valueOf(obj));
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					if (networkBean.isSucc()) {
						operateCallBack.onLoadSuccess(obj);
					}
				}

				@Override
				public void onLoadFail(String msg) {
					operateCallBack.onLoadFail(msg);
				}

				@Override
				public void onTokenInvalid() {

				}
			});
			// String result = WebClient.getInstance().doPost(
			// ExecuteBiz.DOWNLOAD_URL, pairs);
			// JSONObject retObject = new JSONObject(result);
			// if ("True".equalsIgnoreCase(retObject.optString("Ret"))) {
			// data = retObject.optString("Res");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private boolean parseResult(SQLiteDatabase db, int dbType, JSONArray result) {
		if (result == null || result.length() <= 0) {
			return false;
		}
		boolean isNeedContinue = false;
		int length = result.length();
		String tableName = null;
		JSONObject obj = null;
		for (int i = 0; i < length; i++) {
			obj = result.optJSONObject(i);
			tableName = obj.optString("Tab");

			if (StringUtil.isNull(tableName)) {
				continue;
			}

			if (obj.optInt("PageCount") > currentPage) {
				isNeedContinue = true;
			} else {
				if (tableLists.contains(tableName)) {
					tableLists.remove(tableName);
				}
			}

		
			JSONArray data = obj.optJSONArray("Data");
			saveData2DB(db, dbType, tableName, data);
		}
		return isNeedContinue;
	}

	/**
	 * 
	 * 保存业务和基础数据到离线db文件
	 * @param db
	 * @param tab
	 * @param data
	 */

	public static synchronized void saveData2DB(SQLiteDatabase db, int dbType,
			String tab, JSONArray data) {

		//如果返回的表，没有数据，不做插入，直接返回。
		if (StringUtil.isNull(tab) || data == null || data.length() <= 0) {
			return;
		}
		try {
			String tableName = tab.toUpperCase();
			String pKey = PropertiesUtils.getValue(
					MainApplication.getInstance(), tableName);
			String[] keys = null;
			if (!StringUtil.isNull(pKey) && pKey.contains(",")) {
				keys = pKey.split(",");
			} else {
				keys = new String[] { pKey };
			}

			saveData(db, dbType, data, tableName, Arrays.asList(keys));
		} catch (Exception e) {
			L.printStackTrace(e);
		}
	}


	
	private static void saveData(SQLiteDatabase db, int dbType, JSONArray data,
			String tableName, List<String> keys) {
		UserBiz uBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String siteId = uBiz.getCurrentUser().getSite_id();
		String baseDbName = siteId + "base.db";
		String businessDbName = siteId + "business.db";
		if (dbType == 1) {
			DB_NAME = baseDbName;
			db = DB_base.db_open(DB_NAME, "", "");
		} else if (dbType == 2) {
			DB_NAME = businessDbName;
			db = DB_business.db_open(DB_NAME, "", "");
		}

		if (StringUtil.isNull(tableName) || data == null) {
			return;
		}

		ArrayList<Object> pValue;
		JSONObject obj = null;
		SharedPrefUtil.Log("开始插入时间-表名"+tableName+"-数据长度"+ String.valueOf(data.length()));
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
						if (dbType == 1) {
							if (DB_base.isExist(tableName,
									AbstractDao.GetWhereCause(myKeys, args))) {
								String argsStr = Arrays.toString(args);
								String[] strs = argsStr.substring(1,
										argsStr.length() - 1).split(", ");
								/*if (dbType == 1) {
									DB_NAME = baseDbName;
									db = DB_base.db_open(DB_NAME, "", "");
								} else if (dbType == 2) {
									DB_NAME = businessDbName;
									db = DB_business.db_open(DB_NAME, "", "");
								}*/
								try {
									db.update(tableName, values,
											DB_base.GetWhereSql(myKeys),
											strs);
									/*if (dbType == 1) {
										db.update(tableName, values,
												DB_base.GetWhereSql(myKeys),
												strs);
									} else if (dbType == 2) {
										db.update(
												tableName,
												values,
												DB_business.GetWhereSql(myKeys),
												strs);
									}*/
								} catch (Exception e) {
									e.printStackTrace();
								}
								/*SharedPrefUtil.Log("update success---->"
										+ obj.toString());*/
							} else {
								/*if (dbType == 1) {
									DB_NAME = baseDbName;
									db = DB_base.db_open(DB_NAME, "", "");
								} else if (dbType == 2) {
									DB_NAME = businessDbName;
									db = DB_business.db_open(DB_NAME, "", "");
								}*/
								try {
									db.insert(tableName, null, values);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/*SharedPrefUtil.Log("insert success---->"
										+ obj.toString());*/
							}
						} else if (dbType == 2) {
							if (DB_business.isExist(tableName,
									AbstractDao.GetWhereCause(myKeys, args))) {
								String argsStr = Arrays.toString(args);
								String[] strs = argsStr.substring(1,
										argsStr.length() - 1).split(", ");
								/*if (dbType == 1) {
									DB_NAME = baseDbName;
									db = DB_base.db_open(DB_NAME, "", "");
								} else if (dbType == 2) {
									DB_NAME = businessDbName;
									db = DB_business.db_open(DB_NAME, "", "");
								}*/
								try {
									db.update(tableName, values,
											DB_base.GetWhereSql(myKeys),
											strs);
									/*if (dbType == 1) {
										db.update(tableName, values,
												DB_base.GetWhereSql(myKeys),
												strs);
									} else if (dbType == 2) {
										db.update(
												tableName,
												values,
												DB_business.GetWhereSql(myKeys),
												strs);
									}*/
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								if (dbType == 1) {
									DB_NAME = baseDbName;
									db = DB_base.db_open(DB_NAME, "", "");
								} else if (dbType == 2) {
									DB_NAME = businessDbName;
									db = DB_business.db_open(DB_NAME, "", "");
								}
								try {
									db.insert(tableName, null, values);
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}
					}
					// if ("TASKIMAGE".equals(tableName)) {
					// String image = obj.optString("IMAGE");
					// if (!StringUtil.isNull(image)) {
					// String[] urls = image.split(",");
					// for (String url : urls) {
					// httpHelper.getFileData(
					// MainApplication.getInstance(), url,
					// DataFolder.getAppDataRoot() + "cache"
					// + File.separator + "img"
					// + File.separator,
					// getFileName(url));
					// }
					// }
					// }
					if ("TASKMESSAGE".equals(tableName)) {
						String url = obj.optString("IMAGE");
						String type = obj.optString("TASK_TYPE");
//						if (type.equals("P")) {
//							System.out.println(type);
//						}
						String dutyMan = obj.optString("DUTY_MAN");
						String filename = obj.optString("TASK_ID") + ".html";
						UserBiz ub = (UserBiz) BizManger.getInstance().get(
								BizType.USER_BIZ);
						if (!StringUtil.isNull(url) && type.equals("P")
								&& dutyMan.equals(ub.getUserId())) {
							FileDao fileDao = new FileDaoImpl();
							DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
							downLoadFileVo.setUrl(MainApplication.finalUrl
									+ File.separator + url);
							downLoadFileVo.setName(filename);
							downLoadFileVo.setFilename(url);
							downLoadFileVo.setTarget(DataFolder
									.getAppDataRoot()
									+ "fm"
									+ File.separator
									+ filename);
							if (!FileUitl
									.file_isExistsByAbsolutePath(DataFolder
											.getAppDataRoot()
											+ "fm"
											+ File.separator + filename)) {
								fileDao.downLoadFile(downLoadFileVo);
							}
						}
					}
				}

			} catch (Exception e) {
				SharedPrefUtil.Log("UpdateDataService :" + e.getMessage());
				SharedPrefUtil.Log("UpdateDataService :"
						+ " insert failer----> " + obj.toString());
				L.printStackTrace(e);
			}
		}

		SharedPrefUtil.Log("工单刷新-插入时间结束");
		
		//添加数据插入完成后，回调流程页面，中化金茂 
		
		Intent endIntent = new Intent(Const.ACTION_EXEC_END);
		endIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainApplication.getInstance().sendBroadcast(endIntent);
	}

	/**
	 * ??JSONObject??????????????????ContentValues
	 * 
	 * @param obj
	 *            JSONObject
	 * @return ContentValues
	 */
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
				contentValues.putAll(json2ContentValues((JSONObject) value));
			} else if (value instanceof JSONArray) {
				contentValues.put(key,value.toString());
			}
		}
		return contentValues;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	protected static String getFileName(String image) {
		if (StringUtil.isNull(image))
			return "";
		String[] args = image.trim().split("/");

		return args[args.length - 1];
	}

	/*private static String getImageUrl(String path) {
		if (StringUtil.isNull(path) || StringUtil.isNull(path)) {

			return "";
		}
		return Const.BASE_URL + path;
	}*/
	
	private String[] getTableNames(String d_type) throws RuntimeException{
		if(d_type.equalsIgnoreCase("user")){
			return TABLE_NAMES_USERS;
		}else if(d_type.equalsIgnoreCase("project")){
			return TABLE_NAMES_PROJECT;
		}else if(d_type.equalsIgnoreCase("rooms")){
			return TABLE_NAMES_ROOMS;
		}else if(d_type.equalsIgnoreCase("device")){
			return TABLE_NAMES_DEVICE;
		}/*else if(d_type.equalsIgnoreCase("charge")){
			return TABLE_NAMES_CHARGE;
		}else if(d_type.equalsIgnoreCase("provider")){
			return TABLE_NAMES_PROVIDER;
		}*/
		return UPDATE_TABLE_NAMES;
		
	}
}
