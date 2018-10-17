package com.guomao.propertyservice.util;

import com.guomao.propertyservice.config.AppConfig;

import android.util.Log;

public class LogUtil {
	private static final String TAG = "== LogTrace ==";
	private static final String DEBUG_TAG = "debug";

	private static final boolean DEBUG = AppConfig.DEBUG;

	public static void e(String tag, String err) {
		if (DEBUG) {
			Log.e(tag, err);
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			Log.e(DEBUG_TAG, msg);
		}
	}

	public static void d(String tag, String debug) {
		if (DEBUG && debug != null) {
			Log.d(tag, debug);
		}
	}

	public static void d(String tag, String debug, Throwable e) {
		if (DEBUG) {
			Log.d(tag, debug, e);
		}
	}

	public static void d(String msg) {
		if (DEBUG) {
			Log.d(DEBUG_TAG, msg);
		}
	}

	public static void i(String tag, String info) {
		if (DEBUG) {
			Log.i(tag, info);
		}
	}

	public static void i(String msg) {
		if (DEBUG) {
			Log.i(DEBUG_TAG, msg);
		}
	}

	public static void w(String msg) {
		if (DEBUG) {
			Log.w(DEBUG_TAG, msg);
		}
	}

	public static void jw(Object object, Throwable tr) {
		if (DEBUG) {
			Log.w(getPureClassName(object), "", filterThrowable(tr));
		}
	}

	private static Throwable filterThrowable(Throwable tr) {
		StackTraceElement[] ste = tr.getStackTrace();
		tr.setStackTrace(new StackTraceElement[] { ste[0] });
		return tr;
	}

	private static String getPureClassName(Object object) {
		if (object == null) {
			Log.e(TAG, "getPureClassName() : object is null.");
		}
		String name = object.getClass().getName();
		if ("java.lang.String".equals(name)) {
			return object.toString();
		}
		int idx = name.lastIndexOf('.');
		if (idx > 0) {
			return name.substring(idx + 1);
		}
		return name;
	}

	public static void logException(Throwable e) {
		if (AppConfig.DEBUG) {
			e.printStackTrace();
		}
	}
}
