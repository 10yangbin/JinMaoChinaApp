package com.guomao.propertyservice.communcation;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.main.BusinessProcessService;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateCom;
import com.guomao.propertyservice.model.offlineoperate.Operation;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBeanArray;
import com.guomao.propertyservice.network.core.INetworkSync;
import com.guomao.propertyservice.network.core.NetworkSyncImpl;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


/**
 * 离线报事的联网的接口实现
 * @author Administrator
 *
 */
public class OfflineOperateComImpl extends AbstractCom implements
		OfflineOperateCom {

	NetworkBeanArray networkBeanArray = null;
	static String DB_NAME = null;
	private static final String[] TABLE_SAVE = { "TASK_PROCESS", "TASKPT",
			"TASKIMAGE", "TASKCOST", "TASKMESSAGE" };

	public OfflineOperateComImpl(WebClient webClient) {
		super(webClient);
	}

	@Override
	public String sendOfflineData2Server(Operation operate) throws Exception {
		if (operate == null) {
			throw new IllegalArgumentException("operate must be not null.");
		}
		List<String> fileList = null;

		if (!StringUtil.isNull(operate.getOperate_files())) {
			fileList = StringUtil.convertString2ImageList(operate
					.getOperate_files());
		}
		RequestParams mParams = new RequestParams();
		JSONObject jsonobj = new JSONObject(operate.getOperate_p().toString());
		// jsonobj.put("create_time", formator.format(new Date()));
		// jsonobj.put("submit_time", formator.format(new Date()));
		RequestVo mRequestVo = RequestVo.getInstance();
		mRequestVo
				.setRequestUrl(CommenUrl.getActionUrl(operate.getOperate_a()));
		mRequestVo.setRequestMethod(HttpMethod.POST);
		mRequestVo.setJson(jsonobj);
		mRequestVo.setFilesPath(fileList);
		mRequestVo.setRequestParams(mParams);
		INetworkSync mINetworkSync = new NetworkSyncImpl();
		String str = null;
		str = mINetworkSync.getData(mRequestVo, new OperateCallBack() {

			@Override
			public void onLoadSuccess(Object obj) {
				networkBeanArray = new NetworkBeanArray(String.valueOf(obj));
				if (networkBeanArray.isSucc()) {
					saveData2DB(networkBeanArray);
				} else {
				}
			}

			@Override
			public void onTokenInvalid() {

			}

			@Override
			public void onLoadFail(String msg) {
				Toast.makeText(
						MainApplication.getInstance().getApplicationContext(),
						msg, Toast.LENGTH_SHORT).show();
			}

		});
		return str;
	}

	public static void saveData2DB(final NetworkBeanArray networkBeanArray) {
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME = site_id + "business.db";
		new Thread() {
			public void run() {
				try {
					SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
					JSONObject retdata = new JSONObject(
							networkBeanArray.getResult());
					for (String table : TABLE_SAVE) {
						JSONArray ret = retdata.getJSONArray(table);
						if (ret == null || ret.length() <= 0) {
							continue;
						}
						BusinessProcessService.saveData2DB(db, table, ret);
					}
				} catch (Exception e) {
					L.printStackTrace(e);
				} finally {
					DB_base.db_close(0);
				}

			}
        }.start();
	}

}
