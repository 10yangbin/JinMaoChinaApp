package com.guomao.propertyservice.javascripinterface;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.util.L;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * 使用window.csq_db.方法名调用本类中的方法
 * 
 * 对离线数据库操作的web对象
 * 
 * @author Administrator
 * 
 */
public class DbObject {
    String DB_NAME = null;
	String DB_NAME_BASE = null;
	String DB_NAME_BUSINESS = null;

	public DbObject(WebView webView) {
		super();
        Context context = webView.getContext();
        WebView webView1 = webView;
	}

	// 打开数据库
	@JavascriptInterface
	public void db_open(String path, String user, String pwd) {
		DB_base.db_open(path, user, pwd);
	}

	// 关闭数据库
	@JavascriptInterface
	public boolean db_close(int db_handle) {
		DB_base.db_close(db_handle);
		return false;
	}

	// 执行数据库语句
	@JavascriptInterface
	public void db_exec(int db_handle, String sql, String param, int dbType) {
		L.d("dbExec SQL:", sql);
		
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		DB_NAME_BUSINESS = site_id + "business.db";
		String str = null;
//		if (sql.contains("eq") || sql.contains("rm")) {
//			System.out.println(sql);
//		}
		try {
			if (dbType == 1) {
				DB_NAME = DB_NAME_BASE;
				DB_base.db_open(DB_NAME, "", "");
				DB_base.db_exec(db_handle, sql, param);
			} else if (dbType == 2) {
				DB_NAME = DB_NAME_BUSINESS;
				DB_business.db_open(DB_NAME, "", "");
				DB_business.db_exec(db_handle, sql, param);
			}
		} catch (Exception e) {
			L.printStackTrace(e);
		}
//		return str;
//		
//		try {
//			DB_base.db_exec(db_handle, sql, param);
//			if (dbType == 1) {
//				DB_base.db_exec(db_handle, sql, param);
//			} else if (dbType == 2) {
//				DB_business.db_exec(db_handle, sql, param);
//			}
//		} catch (Exception e) {
//			L.printStackTrace(e);
//		}
	}

	// db_select语句
	@JavascriptInterface
	public String db_select(int db_handle, String sql, String param, int dbType) {
		L.d("db_select SQL:", sql);
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		String site_id = ubiz.getCurrentUser().getSite_id();
		DB_NAME_BASE = site_id + "base.db";
		DB_NAME_BUSINESS = site_id + "business.db";
		String str = null;
//		if (sql.contains("eq") || sql.contains("rm")) {
//			System.out.println(sql);
//		}
		try {
			if (dbType == 1) {
				DB_NAME = DB_NAME_BASE;
				DB_base.db_open(DB_NAME, "", "");
				str = DB_base.db_select(db_handle, sql, param).toString();
			} else if (dbType == 2) {
				DB_NAME = DB_NAME_BUSINESS;
				DB_business.db_open(DB_NAME, "", "");
				str = DB_business.db_select(db_handle, sql, param).toString();
			}
		} catch (Exception e) {
			L.printStackTrace(e);
		}
		return str;
	}

	// db_err语句
	/**
	 * 当一次查询完成后，可通过此方法获取数据库错误信息，如果查询没有出差则返回零长度字符串""，否则返回错误信息字符串
	 * 
	 * @param db_handle
	 *            打开的数据库句柄，代表要操作哪个数据库
	 */
	@JavascriptInterface
	public String db_err(int db_handle) {
		return DB_base.db_err(db_handle);
	}

}
