package com.guomao.propertyservice.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBean;
import com.guomao.propertyservice.network.core.INetwork;
import com.guomao.propertyservice.network.core.NetworkImpl;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 功能:
 * 1 业务数据的刷新的请求基础数据
 * 2 返回业务的插入
 * 3 返回基础数据的插入
 * 
 * @author Administrator
 * 
 */

public class SendOfflineDataService extends Service {
	
	/**
	 * 工单刷新业务数据发送的数据库表
	 */
	private static final String[] TABLE_NAMES = {
			"TASKMESSAGE","BORROW"};
	
	private static final String[] TABLE_NOTICES = {"APP_NOTICE" };
	
	private static final String[] TABLE_APP_HOMEPAGE = {"APP_HOMEPAGE" };
	
	
	private static final String[] TABLE_WARINGS = {"APP_WARNINGMSG"};
	

	/**
	 * 工单刷新和二装刷新，数据返回需要保存的数据库表
	 */
	private static final String[] TABLE_SAVE = { "TASK_PROCESS","TASKIMAGE","TASKMESSAGE",
            "APP_NOTICE","APP_WARNINGMSG","BORROW",
            "APP_HOMEPAGE"
	};
	
	private static final String LONGFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private DateFormat myFormat = new SimpleDateFormat(LONGFORMAT);
	
	private HashMap<String, String> TIMES_MAP = new HashMap<String, String>();
	private int current = 0;
	private boolean needContiue = true;
	private final String MAX_COUNT = "total";
	boolean isHistory = false;
	boolean isErZhuang = false;
	private String daynums ;
	
	private String refresh_type ;
	SQLiteDatabase db;
	UserBiz ubiz = null;
	String DB_NAME = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@SuppressLint("WrongConstant")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null == intent){
			stopSelf();
			return 0;
		}
		isHistory = intent.getBooleanExtra("isHistory", false);
		
		daynums =  intent.getStringExtra("daynums");
		
		isErZhuang = intent.getBooleanExtra("isErZhuang", false);
		
		refresh_type = intent.getStringExtra("refresh_type");
		
		//如果真正请求业务的数据
		if (MainApplication.IS_SENDOFFLINE_START) {
			stopSelf();
			
			/*Toast.makeText(getApplicationContext(), "数据同步中，请稍候",
					Toast.LENGTH_SHORT).show();*/
		} else {
			// if (MainApplication.IS_SENDOFFLINE_START) {
			// Toast.makeText(getBaseContext(), "数据更新中请稍候", Toast.LENGTH_SHORT)
			// .show();
			// stopSelf();
			// }
			MainApplication.IS_SENDOFFLINE_START = true;
			current = 0;
			if (needContiue) {
				ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
				if (ubiz.getCurrentUser() != null) {
					String site_id = ubiz.getCurrentUser().getSite_id();
					DB_NAME = site_id + "business.db";
					db = DB_business.db_open(DB_NAME, "", "");
					Intent broadcast = new Intent(
							Const.ACTION_DATA_DOWNLOAD_START);
					sendBroadcast(broadcast);
					sendData();
				} else {
					stopSelf();
				}

			}

		}

		return super.onStartCommand(intent, flags, startId);
	}

	public void sendData() {
		try {
			JSONArray pArr = new JSONArray();
			
			JSONObject reqjson = new JSONObject();
			
			if(refresh_type.equalsIgnoreCase("notice")){
				prepareReqParams(TABLE_NOTICES, pArr);
				reqjson.accumulate(Const.REQACTION, "Notices");
				
			} else if(refresh_type.equalsIgnoreCase("homepage")){
				prepareReqParams(TABLE_APP_HOMEPAGE, pArr);
				reqjson.accumulate(Const.REQACTION, "Homepage");
				
			}else if(refresh_type.equalsIgnoreCase("warning")){
				prepareReqParams(TABLE_WARINGS, pArr);
				reqjson.accumulate(Const.REQACTION, "Warning");
				
			}else {
				prepareReqParams(TABLE_NAMES, pArr);
				reqjson.accumulate(Const.REQACTION, "New_JobData");
			}
		
			reqjson.accumulate(Const.REQPARAM, pArr);
			RequestParams mParams = new RequestParams();
			RequestVo mRequestVo = RequestVo.getInstance();
			if (isErZhuang) {
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.erzhuangbusinessData));
			
			} else if(refresh_type.equalsIgnoreCase("notice")){
			
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.noticeData));
				
			}  else if(refresh_type.equalsIgnoreCase("homepage")){
			
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.homepageData));
				
			}else if(refresh_type.equalsIgnoreCase("letters")){
			
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.leteersData));
				
			} else if(refresh_type.equalsIgnoreCase("vehicle")){
			
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.vehicleData));
				
			} else if(refresh_type.equalsIgnoreCase("customer")){
			
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.customerData));
				
			} else if(refresh_type.equalsIgnoreCase("warning")){
			
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.waringsData));
			}else {
				mRequestVo.setRequestUrl(CommenUrl
						.getActionUrl(CommenUrl.businessData));
			}
			mRequestVo.setRequestMethod(HttpMethod.POST);
			mRequestVo.setJson(reqjson);
			mRequestVo.setRequestParams(mParams);
			INetwork mNetwork = new NetworkImpl();
			mNetwork.getData(mRequestVo, new OperateCallBack() {

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
						startWriteDate(retdata);
					}

				}

				public void startWriteDate(JSONObject jsonObject) {
					try {
						writeDbTask task = new writeDbTask();
						task.execute(jsonObject);
					} catch (Exception e) {
						e.printStackTrace();
						needContiue = true;
						if (!MainApplication.IS_UPDATE_START) {
							Intent end = new Intent(
									Const.ACTION_DATA_DOWNLOAD_SUCCESS);
							sendBroadcast(end);
							DB_base.db_close(0);
						}
					}
				}

				@Override
				public void onLoadFail(String msg) {
					needContiue = true;
					try {
						Intent intent = new Intent(getApplicationContext(),
								SendOfflineDataService.class);
						stopService(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
					current = 0;
					if (!MainApplication.IS_UPDATE_START) {
						Intent end = new Intent(
								Const.ACTION_DATA_DOWNLOAD_SUCCESS);
						sendBroadcast(end);
						DB_base.db_close(0);
					}
				}

				@Override
				public void onTokenInvalid() {

				}

			});
		} catch (Exception e) {
			L.printStackTrace(e);
		} finally {
			MainApplication.IS_SENDOFFLINE_START = false;
			if (!MainApplication.IS_UPDATE_START) {
				// Intent intent = new
				// Intent(Const.ACTION_DATA_DOWNLOAD_SUCCESS);
				// sendBroadcast(intent);
				DB_base.db_close(0);
			}
		}
	}

	private void prepareReqParams(String[] tableArray, JSONArray arr) {
		current++;
		JSONObject obj = null;
		String tableName = null;
		for (int i = 0; i < tableArray.length; i++) {
			tableName = tableArray[i];
			if (StringUtil.isNull(tableArray[i])) {
				continue;
			}
			obj = new JSONObject();
			try {
				obj.put("tablename", tableName);
				if (isHistory) {
					obj.put("lastupdatetime", daynums);
				} else {
					obj.put("lastupdatetime",
							getLastUpdateTimeStr(tableName, false));
				}
				obj.put("page", current);
				obj.put("pagesize", UpdateDataService.PAGE_SIZE);
//				if (tableName.equalsIgnoreCase("taskmessage")) {
//					UserBiz ubiz = (UserBiz) BizManger.getInstance().get(
//							BizType.USER_BIZ);
//					if (ubiz.getCurrentUser().getIs_monitor() == 0) {
//						if (tableName.equalsIgnoreCase("taskmessage")) {
//							JSONArray ids = queryTaskID();
//							obj.put("ids", ids);
//							ubiz = (UserBiz) BizManger.getInstance().get(
//									BizType.USER_BIZ);
//							String site_id = ubiz.getCurrentUser().getSite_id();
//							DB_NAME = site_id + "business.db";
//							db = DB_business.db_open(DB_NAME, "", "");
//							// String where =
//							// "select task_id from taskmessage where task_status != 'Finished' and site_id = '"
//							// + site_id + "'";
//							String timeById = null;
//							timeById = DB_business.getLastUpdateTimeInIds(
//									tableName, false);
//							timeById = StringUtil.isNull(timeById) ? getInitTime()
//									: timeById;
//							if (!timeById.equals("")) {
//								obj.put("lastUpdateTimeById", timeById);
//							}
//						}
//					} else {
						//obj.put("ids", new JSONArray());
//					}
//				}
				arr.put(obj);
			} catch (JSONException e) {
				L.printStackTrace(e);
			}
		}
	}

    private String getLastUpdateTimeStr(String tableName, boolean isNeedCloseDB) {
		String time = null;
		/*if (!tableName.equalsIgnoreCase("TASKPT")) {
			tableName = "TASKMESSAGE";
		}*/
		if (TIMES_MAP.containsKey(tableName)) {
			return TIMES_MAP.get(tableName);
		} else {
			time = DB_business.getLastUpdateTime(tableName, isNeedCloseDB);
			time = StringUtil.isNull(time) ? getInitTime() : time;
			TIMES_MAP.put(tableName, time);
		}
		return time;
	}

	/** 取两日前 */
	@SuppressWarnings("deprecation")
	private String getInitTime() {
		// Date dt = new Date();
		// dt.setDate(dt.getDate() - 2);
		// dt.setHours(0);
		// dt.setMinutes(0);
		// dt.setSeconds(0);
		// String str_date = myFormat.format(dt);
		return "";
	}

	private JSONArray queryTaskID() {
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME = site_id + "business.db";
		db = DB_business.db_open(DB_NAME, "", "");
		JSONArray array = new JSONArray();
		String table = "taskmessage";
		String[] columns = new String[] { "task_id" };
		String selection = "task_status != 'Finished' and site_id = '"
				+ site_id + "'";
		// String selection = "task_status = 'Processed'";
		Cursor c = db.query(table, columns, selection, null, null, null, null);
		while (c.moveToNext()) {
			String task_id = c.getString(c.getColumnIndex("TASK_ID"));
			array.put(task_id);
		}
		return array;
	}

	private class writeDbTask extends AsyncTask<JSONObject, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(JSONObject... arg0) {
			JSONObject json = arg0[0];
			String total = "0";
			for (String table : TABLE_SAVE) {
				JSONArray ret = null;
				try {
					total = json.optString("total");
					ret = json.optJSONArray(table);
					UpdateDataService.saveData2DB(db, 2, table, ret);
				} catch (Exception e) {
					needContiue = true;
				}
				if (ret == null || ret.length() <= 0) {
					continue;
				}
			}
			return total;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				super.onPostExecute(result);
				ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
				String site_id = ubiz.getCurrentUser().getSite_id();
				DB_NAME = site_id + "business.db";
				db = DB_business.db_open(DB_NAME, "", "");
				int count = current * UpdateDataService.PAGE_SIZE;
				
				if (count >= Integer.valueOf(result)) {
					MainApplication.IS_SENDOFFLINE_START = false;
					if (!MainApplication.IS_UPDATE_START) {
						stopSelf();
						/*Toast.makeText(getApplicationContext(), "更新数据完成",
								Toast.LENGTH_SHORT).show();*/
						TIMES_MAP.clear();
						Intent end = new Intent(
								Const.ACTION_DATA_DOWNLOAD_SUCCESS);
						sendBroadcast(end);
						DB_base.db_close(0);
					}
					needContiue = true;
				} else {
					sendData();
				}
			} catch (Exception e) {
				e.printStackTrace();
				needContiue = true;
				if (!MainApplication.IS_UPDATE_START) {
					Intent end = new Intent(Const.ACTION_DATA_DOWNLOAD_SUCCESS);
					sendBroadcast(end);
					DB_base.db_close(0);
				}
			}
		}
	}

}
