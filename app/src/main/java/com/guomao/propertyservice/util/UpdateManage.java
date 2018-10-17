package com.guomao.propertyservice.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.eventbus.OnApkDownLoadFail;
import com.guomao.propertyservice.eventbus.OnApkDownLoadStart;
import com.guomao.propertyservice.eventbus.OnApkDownLoadSucc;
import com.guomao.propertyservice.eventbus.OnCancelUpdate;
import com.guomao.propertyservice.eventbus.OnCurrentVersionNew;
import com.guomao.propertyservice.eventbus.OnResDownLoadFail;
import com.guomao.propertyservice.eventbus.OnResDownLoadStart;
import com.guomao.propertyservice.eventbus.OnResDownLoadSucc;
import com.guomao.propertyservice.eventbus.OnResInitStart;
import com.guomao.propertyservice.eventbus.OnResInitSucc;
import com.guomao.propertyservice.mydao.AppAction;
import com.guomao.propertyservice.mydaoimpl.AppActionImpl;
import com.guomao.propertyservice.network.NetworkCallBack;
import com.guomao.propertyservice.network.request.DownLoadFileVo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;


/**
 * 
 * 
 * @author wangwentao
 * 
 */

public class UpdateManage {
	private static Activity context;
	private String APK_PATH = DataFolder.getAppDataRoot() + "/fm/file/";

    public UpdateManage(Activity context) {
		UpdateManage.context = context;
	}

	public void AutoUpdate(String isforseUpdate, String updateType,
			String content, String appVersion, int resVersion, String apkPath,
			String resPath, int sqlexecute, int sqlexectype, String sqlcontent) {
		if (updateType.equals("0")) {
			int rv = -1;
			try {
				rv = getResVersion();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resVersion > rv) {
				try {
					executeSqlByAll(sqlexecute, sqlexectype, sqlcontent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				showDownloadDialog(isforseUpdate, content, resVersion, resPath);
			} else {
				EventBus.getDefault().post(new OnCurrentVersionNew());
			}
		} else if (updateType.equals("1")) {
			try {
				if (appVersion.compareToIgnoreCase(FunctionUtil.getVersionName(context))>0){
					try {
						executeSqlByAll(sqlexecute, sqlexectype, sqlcontent);
					} catch (Exception e) {
						e.printStackTrace();
					}
					showUpdateDialog(isforseUpdate, appVersion, content,
							apkPath);
				} else {
					// Toast.makeText(context, "当前为最新版本", Toast.LENGTH_SHORT)
					// .show();
					EventBus.getDefault().post(new OnCurrentVersionNew());
				}
			} catch (Exception e) {
				// Toast.makeText(context, "当前为最新版本",
				// Toast.LENGTH_SHORT).show();
				EventBus.getDefault().post(new OnCurrentVersionNew());
			}
		}
	}

	public int getResVersion() {
		int version = 0;
		try {
			String ver = PropertiesUtils.getValue(context, "RES_VERSION");
			version = Integer.valueOf(ver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	@SuppressLint("NewApi")
	public void showDownloadDialog(String isForceUpdate, String content,
			final int resVersion, final String url) {

		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(content);
		builder.setTitle("有新的资源文件需更新");
		builder.setCancelable(false);
		builder.setPositiveButton("更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				AppAction appAction = new AppActionImpl();
				DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
				downLoadFileVo.setUrl(url);
				downLoadFileVo.setName(url.substring(url.lastIndexOf("/") + 1));
				downLoadFileVo.setTarget(APK_PATH
						+ url.substring(url.lastIndexOf("/") + 1));
				File file = new File(APK_PATH
						+ url.substring(url.lastIndexOf("/") + 1));
				if (file.exists()) {
					file.delete();
				}
				EventBus.getDefault().post(new OnResDownLoadStart());
				appAction.downLoadFile(context, downLoadFileVo,
						new NetworkCallBack() {
							@Override
							public void onLoading(int progress) {
							}

							@Override
							public void onLoadSuccess(Object obj) {
								EventBus.getDefault().post(
										new OnResDownLoadSucc());
								initRes(resVersion,
										url.substring(url.lastIndexOf("/") + 1));
							}

							@Override
							public void onLoadFail() {
								EventBus.getDefault().post(
										new OnResDownLoadFail());
							}
						});
			}

		});
		if (isForceUpdate.equals("0")) {
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					EventBus.getDefault().post(new OnCancelUpdate());
				}

			});
		}
		builder.create().show();
	}

	@SuppressLint("NewApi")
	public void showUpdateDialog(final String isForceUpdate, String appVersion,
			String content, final String url) {

		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(String.valueOf(content));
		builder.setTitle("发现新版本(" + appVersion + ")");
		builder.setCancelable(false);
		builder.setPositiveButton("更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// if (isForceUpdate) {
				//
				// }
				AppAction appAction = new AppActionImpl();
				DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
				downLoadFileVo.setUrl(url);
				downLoadFileVo.setName(url.substring(url.lastIndexOf("/") + 1));
				downLoadFileVo.setTarget(APK_PATH
						+ url.substring(url.lastIndexOf("/") + 1));
				File file = new File(APK_PATH
						+ url.substring(url.lastIndexOf("/") + 1));
				if (file.exists()) {
					file.delete();
				}
				EventBus.getDefault().post(new OnApkDownLoadStart());
				appAction.downLoadFile(context, downLoadFileVo,
						new NetworkCallBack() {
							@Override
							public void onLoading(int progress) {
							}

							@Override
							public void onLoadSuccess(Object obj) {
								File file = new File(APK_PATH, url
										.substring(url.lastIndexOf("/") + 1));
								EventBus.getDefault().post(
										new OnApkDownLoadSucc());
								installApk(file);
							}

							@Override
							public void onLoadFail() {
								EventBus.getDefault().post(
										new OnApkDownLoadFail());
							}
						});
			}

		});
		if (isForceUpdate.equals("0")) {
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					EventBus.getDefault().post(new OnCancelUpdate());
				}

			});
		}
		builder.create().show();
	}

	private void initRes(int appVersion, String fileName) {
		EventBus.getDefault().post(new OnResInitStart());
		String filePath = DataFolder.getAppDataRoot() + "fm/file/" + fileName;
		File file = new File(filePath);
		if (file.exists()) {
			try {
				FileUtil.upZipFile(file, "", DataFolder.getAppDataRoot()
						+ "fm/file/");
				copyUpdateFile();
				PropertiesUtils.writeToValue(context, "RES_VERSION", appVersion
						+ "");
				EventBus.getDefault().post(new OnResInitSucc());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void executeSqlByUser(int isExec, int dbType, String sqls) {
		if (isExec != 1) {
			return;
		}
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		if (ubiz.getCurrentUser() == null) {
			return;
		}
		String DB_NAME = null;
		String site_id = ubiz.getCurrentUser().getSite_id();
		SQLiteDatabase db = null;
		if (dbType == 0) {
			DB_NAME = site_id + "base.db";
			db = DB_base.db_open(DB_NAME, "", "");
		} else {
			DB_NAME = site_id + "business.db";
			db = DB_business.db_open(DB_NAME, "", "");
		}
		String[] sql = sqls.split(",");
		for (int i = 0; i < sql.length; i++) {
			db.execSQL(sql[i]);
		}

	}

	public void executeSqlByAll(int isExec, int dbType, String sqls) {
		if (isExec != 1) {
			return;
		}
		if (SharedPrefUtil.getSqlVersion(sqls).equals("1")) {
			return;
		}
		SQLiteDatabase db = null;
		String path = DataFolder.getAppDataRoot() + "db/";
		File rootPath = new File(path);
		File[] files = rootPath.listFiles();
		for (int k = 0; k < files.length; k++) {
			File file = files[k];
			if (dbType == 0) {
				if (file.getName().endsWith("base.db")) {
					db = DB_base.db_open(file.getAbsolutePath(), "", "");
					String[] sql = sqls.split(",");
					for (int i = 0; i < sql.length; i++) {
						db.execSQL(sql[i]);
					}
				}
			} else if (dbType == 1) {
				if (file.getName().endsWith("business.db")) {
					db = DB_business.db_open(file.getName(), "", "");
					String[] sql = sqls.split(",");
					for (int i = 0; i < sql.length; i++) {
						db.execSQL(sql[i]);
					}
				}
			}
		}
		SharedPrefUtil.putSqlVersion(sqls, "1");
	}

	private void installApk(File apkfile) {
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
        int INSTALL_CODE = 0x123;
        context.startActivityForResult(i, INSTALL_CODE);
	}

	private void copyUpdateFile() {
		String jsPath = DataFolder.getAppDataRoot()
				+ "js/jquery.mobile-1.4.5.min.js";
		String morderPath = DataFolder.getAppDataRoot() + "js/newpmorder.js";
		String jssPath = DataFolder.getAppDataRoot() + "js/jquery.js";
		String cssPath = DataFolder.getAppDataRoot()
				+ "css/jquery.mobile-1.4.5.min.css";
		String pmorderPath = DataFolder.getAppDataRoot() + "css/pmorder.css";
		String htmlPath = DataFolder.getAppDataRoot()
				+ "fm/fm5220_1429753899862.html";
		String imagePath = DataFolder.getAppDataRoot() + "css/images";
		if (!new File(jsPath).exists()) {
			copy(context, "jquery.mobile-1.4.5.min.js",
					"js/jquery.mobile-1.4.5.min.js");
		}
		// if(! new File(morderPath).exists()){
		copy(context, "newpmorder.js", "js/newpmorder.js",true);
		// }

		if (!new File(jssPath).exists()) {
			copy(context, "jquery.js", "js/jquery.js");
		}

		if (!new File(cssPath).exists()) {
			copy(context, "jquery.mobile-1.4.5.min.css",
					"css/jquery.mobile-1.4.5.min.css");
		}
		// if(! new File(pmorderPath).exists()){
		copy(context, "pmorder.css", "css/pmorder.css");
		// }

		if (!new File(htmlPath).exists()) {
			copy(context, "fm5220_1429753899862.html", "fm/test.html");
		}
		copy(context, "sqlite.js", "js/sqlite.js",true);
		imageFile("css/images");
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
//	public static void copy(Context myContext, String copyName, String saveName) {
//		String filename = DataFolder.getAppDataRoot() + saveName;
//		String copyFilePath = DataFolder.getAppDataRoot() + "fm/file/assets/"
//				+ copyName;
//		File file = new File(filename);
//		if (!file.getParentFile().exists()) {
//			file.getParentFile().mkdirs();
//		}
//		InputStream is = null;
//		FileOutputStream fos = null;
//		try {
//			if (!file.exists()) {
//				is = new FileInputStream(copyFilePath);
//				fos = new FileOutputStream(filename);
//				byte[] buffer = new byte[7168];
//				int count = 0;
//				while ((count = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, count);
//				}
//				fos.close();
//				is.close();
//			}
//		} catch (Exception e) {
//			L.printStackTrace(e);
//		} finally {
//			try {
//				if (fos != null)
//					fos.close();
//			} catch (IOException e) {
//			}
//			try {
//				if (is != null)
//					is.close();
//			} catch (IOException e) {
//			}
//		}
//	}

	public static void imageFile(String strPath) {
		try {
			String path = DataFolder.getAppDataRoot() + "fm/file/assets/"
					+ strPath;
			File file = new File(path);
			file.list();
			String[] files = file.list();
			for (int i = 0; i < files.length; i++) {
				String filename = files[i];
				copy(context, "css/images/" + filename, "css/images/"
						+ filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
