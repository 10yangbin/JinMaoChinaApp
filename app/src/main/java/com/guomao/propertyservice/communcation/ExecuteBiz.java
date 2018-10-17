package com.guomao.propertyservice.communcation;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.dao.OfflineOperateDaoImpl;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.main.BusinessProcessService;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.service.OfflineData2Service;
import com.guomao.propertyservice.service.UpdateDataService;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateDao;
import com.guomao.propertyservice.model.offlineoperate.Operation;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBeanArray;
import com.guomao.propertyservice.network.core.INetworkSync;
import com.guomao.propertyservice.network.core.NetworkSyncImpl;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.FunctionUtil;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import static com.guomao.propertyservice.config.CommenUrl.getActionUrl;

/**
 * web 页面的网络请求
 */
public class ExecuteBiz {
	public static String DB_NAME = "";
	static String resp = null;
	static String msg = null;
	static int status = 0; // 0-网络原因提交失败，1-提交成功，2-业务原因提交失败
	static NetworkBeanArray networkBeanArray = null;
	
	//业务请求,返回数据所需的插入表
	private static final String[] TABLE_SAVE = { "TASK_PROCESS","TASKIMAGE","TASKMESSAGE",
			"APP_DECORATION_CHMOD", "APP_CHMOD_FILE_DATA", "APP_DECORATION_CHMOD_PRO",//变更的主表，流程，附件表
            "APP_DECORATION_INFOR", "APP_INFOR_FILE_DATA", "APP_DECORATION_INFOR_PRO",//二装的主表，流程，附件表
            "APP_DECORATION_RN",    "APP_RN_FILE_DATA",    "APP_DECORATION_RN_PRO",  //整改的主表，流程，附件表
            "APP_NOTICE","APP_WARNINGMSG",
            "APP_SEND_LETTERS","APP_SEND_LETTERS_PRO","APP_SEND_LETTERS_FILE_DATA",
            "APP_VEHICLE_MSG","APP_VEHICLE_PASS_MSG"
			};

	private static final String LONGFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static DateFormat myFormat = new SimpleDateFormat(LONGFORMAT);
	private static HashMap<String, String> TIMES_MAP = new HashMap<String, String>();
	private static int current = 0;
	private static boolean needContiue;
	private static final String MAX_COUNT = "total";
	// 有效性验证
	static boolean isDeal = false;

	@SuppressLint("SimpleDateFormat")
	public static void exec(String action, String param, String files,
			boolean needOnline, final Context context) throws Exception {
		status = 0;
		isDeal = false;
		resp = null;
		msg = null;
		networkBeanArray = null;
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME = site_id + "business.db";
		if (StringUtil.isNull(action)) {
			throw new IllegalAccessException("action must be not null");
		} else {
			if (!action.contains("/user/login")
					|| !action.contains("/user/logout")) {
				UserBiz uBiz = (UserBiz) BizManger.getInstance().get(
						BizType.USER_BIZ);
				User currentUser = uBiz.getCurrentUser();
				if (currentUser.getUser_id() == ""
						|| currentUser.getSite_id() == "/user/logout") {
					Intent intent = new Intent(
							Const.ACTION_NetworkImpl_USER_NULL);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					MainApplication.getInstance().sendBroadcast(intent);
				}
			}
		}

		Intent intent = new Intent(Const.ACTION_EXEC_START);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainApplication.getInstance().sendBroadcast(intent);
		Intent endIntent = new Intent(Const.ACTION_EXEC_END);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		List<String> fileList = null;

		if (!StringUtil.isNull(files)) {
			fileList = StringUtil.convertString2ImageList(files);
		}
		MainApplication.judgeNetWork();
		RequestParams mParams = new RequestParams();
		JSONObject jsonobj = new JSONObject(param);
		RequestVo mRequestVo = RequestVo.getInstance();
		mRequestVo.setRequestUrl(getActionUrl(action));
		mRequestVo.setRequestMethod(HttpMethod.POST);
		mRequestVo.setJson(jsonobj);
		mRequestVo.setFilesPath(fileList);
		mRequestVo.setRequestParams(mParams);
		INetworkSync mINetworkSync = new NetworkSyncImpl();
		if (MainApplication.IS_ON_LINE) {
			try {
				mINetworkSync.getData(mRequestVo, new OperateCallBack() {

					@Override
					public void onLoadSuccess(Object obj) {
						networkBeanArray = new NetworkBeanArray(String
								.valueOf(obj));
						/*SharedPrefUtil.Log("业务执行返回的数据  ---->"
								+ obj.toString());*/
					/*	SharedPrefUtil.Log("返回的数据的长度  ---->"
								+ networkBeanArray.getResult().length());*/
						if (networkBeanArray.isSucc()) {
							status = 1;// 提交成功
							saveData2DB(context, networkBeanArray);
							msg = networkBeanArray.getMessage();
							Intent endIntent = new Intent(Const.ACTION_EXEC_END);
							endIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							MainApplication.getInstance().sendBroadcast(
									endIntent);
						} else {
							status = 2;// 业务原因提交失败
							msg = networkBeanArray.getMessage();
						}
						isDeal = true;
					}

					@Override
					public void onLoadFail(String message) {
						networkBeanArray = new NetworkBeanArray();
						networkBeanArray.setMessage(message);
						networkBeanArray.setSucc(false);
						networkBeanArray.setResult("");
						status = 0;
						isDeal = true;
						 Toast.makeText(context, message, Toast.LENGTH_SHORT)
						 .show();
					}

					@Override
					public void onTokenInvalid() {

					}
				});
				if (!FunctionUtil.isServiceWork(context,
						"com.guomao.propertyservice.service.OfflineData2Service")
						&& ubiz.getCurrentUser() != null) {
					Intent offineDataIntent = new Intent(context,
							OfflineData2Service.class).putExtra(
							"isShowProgress", false);// 离线报事
					context.startService(offineDataIntent);
				}
				if (networkBeanArray != null) {
					if (!networkBeanArray.isSucc()) {
						throw new Exception(networkBeanArray.getMessage());
					}
				}
			} catch (Exception e) {
				MainApplication.isExecSucc = false;
				if (MainApplication.IS_ON_LINE) {
					MainApplication.getInstance().sendBroadcast(endIntent);
					throw e;
				} else {
					if (!(e instanceof IOException)) {
						MainApplication.getInstance().sendBroadcast(endIntent);
						throw e;
					} else if (needOnline) {
						MainApplication.getInstance().sendBroadcast(endIntent);
						throw e;
					} else {
						status = 0;// 网络原因提交失败
					}
				}
			}
		} else {
			if (needOnline) {
				isDeal = true;
				MainApplication.getInstance().sendBroadcast(endIntent);
				throw new IOException("您的网络不给力~ 请检查网络后重试!");
			}
		}

		if (MainApplication.IS_ON_LINE && status == 1) {
		}

		if (!isDeal && !needOnline) {
			try {
				SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
				Operation operate = new Operation();
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String date = sDateFormat.format(new java.util.Date());
				operate.setCreate_time(date);
				operate.setOperate_a(action);
				operate.setOperate_p(param);
				String taskId = null;
				if (param.contains("task_id")) {
					JSONObject jsonObject = new JSONObject(param);
					taskId = jsonObject.getString("task_id");
				}
				operate.setOperate_taskId(taskId);
				operate.setOperate_files(files);
				operate.setOperate_status(status);
				operate.setOperate_result(msg == null ? resp : msg);
				if (status == 1 || status == 2) {
					operate.setSubmit_time(date);
				}
				OfflineOperateDao dao = new OfflineOperateDaoImpl(db, db,
						context);
				// if (action.equals(CommenAction.ACTION_WO_BS)
				// && action.equals(CommenAction.ACTION_WO_ZXTJ)) {
				// dao.save(operate);
				// } else {
				// String sql = "select operate_p from offlineoperate";
				// JSONArray jsonArray = DB.db_select(0, sql, "");
				// boolean isExeced = false;
				// for (int i = 0; i < jsonArray.length(); i++) {
				// String taskId = null;
				// if (param.contains("task_id")) {
				// JSONObject jsonObject = new JSONObject(param);
				// taskId = jsonObject.getString("task_id");
				// }
				// if (operate.getOperate_p().contains(taskId)) {
				// Toast.makeText(context, "你已执行此动作",
				// Toast.LENGTH_SHORT).show();
				// isExeced = true;
				// break;
				// }
				// }
				// if (!isExeced) {
				// dao.save(operate);
				// }
				// }
				dao.saveOrUpdate(operate);
				DB_business.db_close(0);
			} catch (Exception e) {
				e.printStackTrace();
				MainApplication.getInstance().sendBroadcast(endIntent);
				throw new Exception("数据存储失败!");
			} finally {
				DB_business.db_close(0);
			}
		}
		MainApplication.getInstance().sendBroadcast(endIntent);
		if (!StringUtil.isNull(msg) && status != 1) {
			throw new Exception(msg);
		}
	}

	public static void saveData2DB(final Context context,
			final NetworkBeanArray networkBeanArray) {
		new Thread() {
			public void run() {
				MainApplication.IS_SENDOFFLINE_START = true;
				try {
					UserBiz ubiz = (UserBiz) BizManger.getInstance().get(
							BizType.USER_BIZ);
					String site_id = ubiz.getCurrentUser().getSite_id();
					DB_NAME = site_id + "business.db";
					SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
					JSONObject retdata = new JSONObject(
							networkBeanArray.getResult());
					for (String table : TABLE_SAVE) {
						JSONArray ret = retdata.optJSONArray(table);
						if (ret == null || ret.length() <= 0) {
							continue;
						}
						BusinessProcessService.saveData2DB(db, table, ret);
					}
				} catch (Exception e) {
					L.printStackTrace(e);
				} finally {
					MainApplication.IS_SENDOFFLINE_START = false;
					if (!MainApplication.IS_UPDATE_START) {
						Intent intent = new Intent(
								Const.ACTION_DATA_DOWNLOAD_SUCCESS);
						context.sendBroadcast(intent);
						DB_business.db_close(0);
					}
				}

			}
        }.start();
	}

	private static void prepareReqParams(String[] tableArray, JSONArray arr) {
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
				obj.put("lastupdatetime",
						getLastUpdateTimeStr(tableName, false));
				obj.put("page", current);
				obj.put("pagesize", UpdateDataService.PAGE_SIZE);
				arr.put(obj);
			} catch (JSONException e) {
				L.printStackTrace(e);
			}
		}
	}

    private static String getLastUpdateTimeStr(String tableName,
			boolean isNeedCloseDB) {
		String time = null;
		if (!tableName.equalsIgnoreCase("TASKPT")) {
			tableName = "TASKMESSAGE";
		}
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
	private static String getInitTime() {
		Date dt = new Date();
		dt.setDate(dt.getDate() - 2);
		dt.setHours(0);
		dt.setMinutes(0);
		dt.setSeconds(0);
		String str_date = myFormat.format(dt);
		return str_date;
	}

	private JSONArray queryTaskID(SQLiteDatabase db) {
		JSONArray array = new JSONArray();
		String table = "taskmessage";
		String[] columns = new String[] { "task_id" };
		String selection = "task_status != 'Finished'";
		Cursor c = db.query(table, columns, selection, null, null, null, null);
		while (c.moveToNext()) {
			String task_id = c.getString(c.getColumnIndex("TASK_ID"));
			array.put(task_id);
		}
		return array;
	}
}