package com.guomao.propertyservice.javascripinterface;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.file.FileUitl;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.mydao.FileDao;
import com.guomao.propertyservice.mydaoimpl.FileDaoImpl;
import com.guomao.propertyservice.network.request.DownLoadFileVo;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * 1 使用window.csq_io.方法名调用本类中的方法
 * 2 文件相关操作的web使用对象
 * 
 * @author Administrator
 * 
 */
public class IoObject {
    public static String DB_NAME = null;
	private UserBiz ubiz;
	private static final String GONGDAN_WUlIAO = "wuliao";
	private static final String GONGDAN_ZHIXING = "zhixing";

	public IoObject(WebView webView) {
		super();
        Context context = webView.getContext();
        WebView webView1 = webView;
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		getDbName();
	}

	private String getDbName() {
		try {
			String site_id = ubiz.getCurrentUser().getSite_id();
			DB_NAME = site_id + "business.db";
		} catch (Exception e) {
		}
		return DB_NAME;
	}

	@JavascriptInterface
	public void getFileById(String taskId) {
		try {
			if(StringUtil.isNull(DB_NAME))getDbName();
			SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
			String sql = "select IMAGE from TASKIMAGE where TASK_ID = '" + taskId + "'";
			String sql_html = "select TASK_TYPE,IMAGE from TASKMESSAGE where TASK_ID = '" + taskId + "'";
			JSONArray jsonArray = select(db, sql);
			JSONArray jsonArray2 = select(db, sql_html);
			for (int i = 0; i < jsonArray.length(); i++) {
				Log.d("TAG", i + "");
				JSONObject json = jsonArray.getJSONObject(i);
				String imgPath = json.getString("IMAGE");
				FileDao fileDao = new FileDaoImpl();
				DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
				downLoadFileVo.setUrl(CommenUrl.downloadImg);
				downLoadFileVo.setName(getFileName(imgPath));
				downLoadFileVo.setFilename(imgPath);
				downLoadFileVo.setTarget(DataFolder.getAppDataRoot() + "cache/images" + File.separator
						+ imgPath);
				if (!FileUitl.file_isExistsByAbsolutePath(DataFolder.getAppDataRoot() + "cache/images" + File.separator
						+ imgPath)) {
					fileDao.downLoadFile(downLoadFileVo);
				}
			}
			if (jsonArray2 != null && jsonArray2.length() != 0) {
				JSONObject json = jsonArray2.getJSONObject(0);
				String type = json.optString("TASK_TYPE");
				if (("P").equalsIgnoreCase(type)||("C").equalsIgnoreCase(type)||("E").equalsIgnoreCase(type)||("X").equalsIgnoreCase(type)) {
					String url = json.optString("IMAGE");
					String filename = taskId + ".html";
					FileDao fileDao = new FileDaoImpl();
					DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
					downLoadFileVo.setUrl(MainApplication.finalUrl + url);
					downLoadFileVo.setName(filename);
					downLoadFileVo.setFilename(url);
					downLoadFileVo.setTarget(DataFolder.getAppDataRoot() + "fm" + File.separator + filename);
					if (!FileUitl.file_isExistsByAbsolutePath(
							DataFolder.getAppDataRoot() + "fm" + filename)) {
						fileDao.downLoadFile(downLoadFileVo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB_base.db_close(0);
		}
	}
	
	@JavascriptInterface
	public void getEr_Zhuang_FileById(String taskId) {
		try {
			if(StringUtil.isNull(DB_NAME))getDbName();
			SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
			String sql = "select IMAGE from TASKIMAGE where TASK_ID = '" + taskId + "'";
			JSONArray jsonArray = select(db, sql);
			for (int i = 0; i < jsonArray.length(); i++) {
				Log.d("TAG", i + "");
				JSONObject json = jsonArray.getJSONObject(i);
				String imgPath = json.getString("IMAGE");
				FileDao fileDao = new FileDaoImpl();
				DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
				downLoadFileVo.setUrl(CommenUrl.downloadImg);
				downLoadFileVo.setName(getFileName(imgPath));
				downLoadFileVo.setFilename(imgPath);
				downLoadFileVo.setTarget(DataFolder.getAppDataRoot() + "cache/images" + File.separator
						+ imgPath);
				if (!FileUitl.file_isExistsByAbsolutePath(DataFolder.getAppDataRoot() + "cache/images" + File.separator
						+ imgPath)) {
					fileDao.downLoadFile(downLoadFileVo);
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB_base.db_close(0);
		}
	}
	
	
	@JavascriptInterface
	public void getBanerrImage_FileBySiteID(String SiteID) {
		
			
				FileDao fileDao = new FileDaoImpl();
				DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
				downLoadFileVo.setUrl(CommenUrl.downloadImages+SiteID);
				downLoadFileVo.setName(getFileName(SiteID));
				downLoadFileVo.setFilename(SiteID);
				downLoadFileVo.setTarget(DataFolder.getAppDataRoot() + "cache/images" + File.separator
						+ SiteID);
				if (!FileUitl.file_isExistsByAbsolutePath(DataFolder.getAppDataRoot() + "cache/images" + File.separator
						+ SiteID)) {
					fileDao.downLoadFile(downLoadFileVo);
				}
			
		
		
	}

	public static JSONArray select(SQLiteDatabase db, String sql) {
		// DB.rawQuery();没有加入逻辑判断
		Cursor cursor = db.rawQuery(sql, null);
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

	@JavascriptInterface
	public String file_read(String path) {
		// Toast.makeText(context, "读取文件了", Toast.LENGTH_LONG).show();
		return FileUitl.file_read(path);
	}

	@JavascriptInterface
	public boolean file_write(String path, String data, boolean append) {
		// Toast.makeText(context, "读取文件了", Toast.LENGTH_LONG).show();
		FileUitl.file_write(path, data, append);
		return false;
	}

	@JavascriptInterface
	public void delfile(String path) {
		// Toast.makeText(context, "删除文件", Toast.LENGTH_LONG).show();
		try {
			if (!StringUtil.isNull(path)) {
				File file = new File(DataFolder.getAppDataRoot().concat(path));
				if (file != null && file.exists())
					FileUitl.delfile(file);
			}
		} catch (Exception e) {
		}
	}

	@JavascriptInterface
	public boolean file_exists(String path) {
		// Toast.makeText(context, "文件存在与否的判断", Toast.LENGTH_LONG).show();
		return FileUitl.file_exists(path);
	}

	@JavascriptInterface
	public boolean file_isExistsByAbsolutePath(String path) {
		return FileUitl.file_isExistsByAbsolutePath(path);
	}

	@JavascriptInterface
	public static void createPath(String path) {
		FileUitl.createPath(path);
	}

	// 保存执行页的信息
	@JavascriptInterface
	public static void setExecMsg(String spName, String msg) {
		Editor e = SharedPrefUtil.getSharedPrefe(spName).edit();
		e.putString(GONGDAN_ZHIXING, msg);
		e.apply();
	}

	// 获取执行页的信息
	@JavascriptInterface
	public String getExecMsg(String spName) {
		String result = "";
		try {
			result = SharedPrefUtil.getSharedPrefe(spName).getString(GONGDAN_ZHIXING, "");
		} catch (Exception e) {
			L.printStackTrace(e);
		}
		return result;
	}

	// 保存物料页的信息
	@JavascriptInterface
	public void setBomMsg(String spName, String msg) {
		Editor e = SharedPrefUtil.getSharedPrefe(spName).edit();
		e.putString(GONGDAN_WUlIAO, msg);
		e.apply();
	}

	// 获取物料页的信息
	@JavascriptInterface
	public String getBomMsg(String spName) {
		String result = "";
		try {
			result = SharedPrefUtil.getSharedPrefe(spName).getString(GONGDAN_WUlIAO, "");
		} catch (Exception e) {
			L.printStackTrace(e);
		}
		return result;
	}

	@JavascriptInterface
	public void deleteSp(String spName) {
		String path = "/data/data/LongforFM/shared_prefs/" + spName + ".xml";
		delfile(path);
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getNativePicPath(String url) {
		if (StringUtil.isNull(url) || url.toLowerCase().startsWith("file:///")) {
			return url;
		}
		return DataFolder.getAppDataRoot() + "cache" + File.separator + "images" + File.separator + url;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getNativePicPathWithDate(String url) {
		if (StringUtil.isNull(url) || url.toLowerCase().startsWith("file:///")) {
			return url;
		}
		return DataFolder.getAppDataRoot() + "cache" + File.separator + "images" + File.separator + getImageDate(url)
				+ File.separator + getFileName(url);
	}

	@JavascriptInterface
	public String getNativeHtmlPath(String filename) {
		return DataFolder.getAppDataRoot() + "fm" + File.separator + filename + ".html";
	}

	private String getImageDate(String image) {
		if (StringUtil.isNull(image))
			return "";
		String[] args = image.trim().split("/");

		return args[args.length - 2];
	}

	private String getFileName(String image) {
		if (StringUtil.isNull(image))
			return "";
		String[] args = image.trim().split("/");

		return args[args.length - 1];
	}

}
