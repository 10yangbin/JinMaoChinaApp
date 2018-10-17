package com.guomao.propertyservice.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.config.AppConfig;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.util.CrashHandler;
import com.guomao.propertyservice.file.FileUitl;
import com.guomao.propertyservice.service.LongForService;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.PropertiesUtils;
import com.guomao.propertyservice.util.SPCookieStore;
import com.guomao.propertyservice.util.StringUtil;
import com.guomao.propertyservice.util.TelePhoneInfoUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainApplication extends Application {
	public static Context applicationContext ;
	// login user name
	public final String PREF_USERNAME = "username";
	private String userName = null;
	// login password
	private static final String PREF_PWD = "pwd";
	protected static final String TAG = null;
	private String password = null;
	
	/**
	 * 业务数据是否正在下载
	 */
	public static boolean IS_SENDOFFLINE_START = false;
	
	/**
	 * 基础数据是否正在下载
	 */
	public static boolean IS_UPDATE_START = false;
	
	private static MainApplication instance;
	public static boolean IS_WIFI;
	public static boolean IS_ON_LINE;
	public static boolean IS_NEED_START_PUSH;
	public static boolean cancel = true;
	// exec执行是否成功
	public static boolean isExecSucc = false;
	// 是否正在手动提交
	public static boolean isManualSubmit = false;
	// 根URL
	public static String finalUrl = "";
	//计划工单上传地址
	public static String planUpdata = "";
	public static String ImageDownUrl = "";
	public static String PayUrl = "";

	String DB_NAME = null;
	private static SPCookieStore SPCookieStore;
	public static ConcurrentHashMap<String, Boolean> siteUpdate = new ConcurrentHashMap<String, Boolean>();
	public static ConcurrentHashMap<String, String> siteUpdateAddress = new ConcurrentHashMap<String, String>();
	public static String MPHOTOPATH;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// 通过获取进程id ，进一步获取appname
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
		if (processAppName == null || processAppName.equals("")) {
			// workaround for baidu location sdk
			// 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
			// 创建新的进程。
			return;
		}
		Intent intent = new Intent(getApplicationContext(),
				LongForService.class);
		startService(intent);
		judgeNetWork();
		initCookieStore();
		initImageLoader();
		if (!IS_ON_LINE) {
			IS_NEED_START_PUSH = true;
		}
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		initFinalUrl();
		cleanData();
		//LongforObject.getDbUpdate();
		
		initHTML();
		applicationContext =getApplicationContext();
	}
	/**
	 * ImageLoader 的初始化
	 */
	private void initImageLoader() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "caches/image");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.memoryCacheExtraOptions(800, 1200).threadPoolSize(3)
				// 线程池的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// 线程池的属性
				.denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).diskCacheSize(100 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(getApplicationContext())).writeDebugLogs().build();

		ImageLoader.getInstance().init(config);
	}

	private void initCookieStore() {
		SPCookieStore = new SPCookieStore(getApplicationContext());
	}

	private void initFinalUrl() {
		finalUrl = PropertiesUtils.getUrlProperties(getApplicationContext())
				.getProperty("FINAL");
		planUpdata = PropertiesUtils
				.getUrlProperties(getApplicationContext()).getProperty(
						"PLANUPDATA");
		ImageDownUrl = PropertiesUtils
				.getUrlProperties(getApplicationContext()).getProperty(
						"IMAGEDOWNURL");
		PayUrl = PropertiesUtils
				.getUrlProperties(getApplicationContext()).getProperty(
						"PayUrl");
	}

	//初始化计划工单的JS ,CSS ，图片，HTML 数据  
	private void initHTML() {	

		copy(instance, "plan/js/newpmorder.js", "js/newpmorder.js",true);  

		copy(instance, "plan/js/gongdan.js", "js/gongdan.js",true);

		copy(instance, "plan/js/sqlite.js", "js/sqlite.js",true);

		copy(instance, "plan/js/jquery-1.11.2.js", "js/jquery-1.11.2.js",true);

		//copy(instance, "plan/js/jquery.js", "js/jquery.js",true);
		
		//copy(instance, "plan/js/jquery.mobile-1.4.5.min.js", "js/jquery.mobile-1.4.5.min.js",true);


		copy(instance, "plan/css/gongdan_jihua.css", "css/gongdan_jihua.css",true);
		
		copy(instance, "plan/css/wl_bounced.css", "css/wl_bounced.css",true);

		imageFile("plan/css/images");

		copyFolder("plan/images", "images/");
		copyFolder("baoshi_image", "baoshi_image");

	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		if (userName == null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			userName = preferences.getString(PREF_USERNAME, null);
		}
		return userName;
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		if (password == null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			password = preferences.getString(PREF_PWD, null);
		}
		return password;
	}

	/**
	 * 设置用户名
	 * 
	 * @param username
	 */
	public void setUserName(String username) {
		if (username != null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = preferences.edit();
			if (editor.putString(PREF_USERNAME, username).commit()) {
				userName = username;
			}
		}
	}

	/**
	 * 设置密码
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_PWD, pwd).commit()) {
			password = pwd;
		}
	}

	/**
	 * 对网络环境进行判断
	 */
	public static void judgeNetWork() {
		String net = TelePhoneInfoUtil.getNetwork(instance);
		if (StringUtil.isNull(net)) {
			MainApplication.IS_WIFI = false;
			MainApplication.IS_ON_LINE = false;
		} else if ("wifi".equals(net)) {
			MainApplication.IS_WIFI = true;
			MainApplication.IS_ON_LINE = true;
		} else {
			MainApplication.IS_WIFI = false;
			MainApplication.IS_ON_LINE = true;
		}
	}

	/**
	 * 获取应用的实例化对象
	 * 
	 * @return instance
	 */
	public static MainApplication getInstance() {
		return instance;
	}

	public static void copy(Context myContext, String ASSETS_NAME,
			String saveName) {
		copy(myContext, ASSETS_NAME, saveName,false);
	}
	public static void copy(Context myContext, String ASSETS_NAME,
			String saveName,boolean isReWrite) {
		String filename = DataFolder.getAppDataRoot() + saveName;
		File file = new File(filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			if (isReWrite || !file.exists()) {
				is = myContext.getResources().getAssets().open(ASSETS_NAME);
				fos = new FileOutputStream(filename);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			L.printStackTrace(e);
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
	}

	public static void imageFile(String strPath) {
		copyFolder(strPath, "css/images/");
	}

	public static void copyFolder (String folderPath,String dstFolderPath) {

		try {
			if(folderPath .endsWith("/")){
				folderPath = folderPath.substring(0, folderPath.length()-1);
			}
			if(!dstFolderPath.endsWith("/")){
				dstFolderPath = dstFolderPath.concat("/");
			}
			String[] files = instance.getResources().getAssets().list(folderPath);
			for (int i = 0; i < files.length; i++) {
				String filename = files[i];
				copy(instance, folderPath+"/" + filename, dstFolderPath
						+ filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isAppOnForeground(Context context) {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	// 中化金茂去掉  一个月内   Completed  删除时间通过DMS_UPDATE_TIME
	public void cleanData() {
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		if (ubiz.getCurrentUser() == null) {
			return;
		}
		String site_id = ubiz.getCurrentUser().getSite_id();
		String db_base_name = site_id + "base.db";
		String db_business_name = site_id + "business.db";
		if (!DB_base.isDbExists(db_base_name)
				|| !DB_base.isDbExists(db_business_name)) {
			return;
		}
		DB_NAME = site_id + "business.db";
		int month = AppConfig.DEL_DATA_BEFORE_DATE;// 设置清理几个月以前的数据
		String state = "Abandoned"; // 条件废弃
		//String task_status = "Completed"; // 条件已完成   中化金茂去掉
		String tasks_status = "Finished"; // 已完结
		String time = addDay(month, new Date());
		String where = "select TASK_ID from TASKMESSAGE where (task_status = '" + state+ "' or task_status = '" + tasks_status+ "') and DMS_UPDATE_TIME <'" + time + "'";
		final String delete_LOCALIMAGE = "select IMAGE from TASKIMAGE where TASK_ID in ("
				+ where + ")";
		// 删除TASKCOST表里面的数据
		final String delete_TASKCOST = "delete from TASKCOST where TASK_ID in ("
				+ where + ")";
		// 删除TASKIMAGE表里面的数据
		final String delete_TASKIMAGE = "delete from TASKIMAGE where TASK_ID in ("
				+ where + ")";
		// 删除TASKPT表里数据
		final String delete_TASKPT = "delete from TASKPT where TASK_ID in ("
				+ where + ")";
		// 删除TASK_PROCESS表数据
		final String delete_TASK_PROCESS = "delete from TASK_PROCESS where TASK_ID in ("
				+ where + ")";
		// 删除TASKMESSAGE表里数据
		final String delete_TASKMESSAGE = "delete from TASKMESSAGE where (task_status = '"
				+ state
				+ "' or task_status = '"
				+ tasks_status
				+ "') and DMS_UPDATE_TIME <'" + time + "'"; //DMS_UPDATE_TIME  修改更新时间为更新时间
		new Thread() {
			public void run() {
				SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
				try {
					deleteLocalImage(delete_LOCALIMAGE);
					db.execSQL(delete_TASKMESSAGE);
					db.execSQL(delete_TASK_PROCESS);
					db.execSQL(delete_TASKIMAGE);
					//db.execSQL(delete_TASKCOST);
					//db.execSQL(delete_TASKPT);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e(TAG, delete_TASKMESSAGE);
				db.close();
			}
        }.start();
	}

	public void deleteLocalImage(String sql) {
		if (StringUtil.isNull(sql)) {
			return;
		}
		JSONArray imageFile = DB_business.db_select(0, sql, "");
		int length = imageFile.length();
		for (int i = 0; i < length; i++) {
			try {
				JSONObject json = imageFile.getJSONObject(i);
				String image = json.getString("IMAGE");
				String path = DataFolder.getAppDataRoot() + "cache"
						+ File.separator + image;
				File file = new File(path);
				FileUitl.delfile(file);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static String addDay(int month, Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		c.add(Calendar.MONTH, month);
		return df.format(c.getTime());
	}
	/**
	 * 获取CookieStore
	 * @return
	 */
	public  CookieStore getCookieStore(){
		return SPCookieStore;
	}
}