package com.guomao.propertyservice.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.guomao.propertyservice.config.Const;

import android.content.Context;
import android.util.Log;

//Logcat统一管理类
public class L {

	private L() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	private static final String TAG = "ZhongHua_JM";

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (Const.DEBUG)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (Const.DEBUG)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (Const.DEBUG)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (Const.DEBUG)
			Log.v(TAG, msg);
	}

	// 下面是传入类名作为tag的函数
	public static void i(Class<?> tag, String msg) {
		if (Const.DEBUG)
			Log.i(tag.getSimpleName(), msg);
	}

	public static void d(Class<?> tag, String msg) {
		if (Const.DEBUG)
			Log.d(tag.getSimpleName(), msg);
	}

	public static void e(Class<?> tag, String msg) {
		if (Const.DEBUG)
			Log.e(tag.getSimpleName(), msg);
	}

	public static void v(Class<?> tag, String msg) {
		if (Const.DEBUG)
			Log.v(tag.getSimpleName(), msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (Const.DEBUG)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (Const.DEBUG)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (Const.DEBUG)
			Log.e(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (Const.DEBUG)
			Log.v(tag, msg);
	}

	// 下面是传入类名作为tag的函数
	public static void i(Context tag, String msg) {
		i(tag.getClass(), msg);
	}

	public static void d(Context tag, String msg) {
		d(tag.getClass(), msg);
	}

	public static void e(Context tag, String msg) {
		e(tag.getClass(), msg);
	}

	public static void v(Context tag, String msg) {
		v(tag.getClass(), msg);
	}

	public static void printStackTrace(Throwable e) {
		if (Const.DEBUG) {
			e.printStackTrace();
		} else {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			String detail = stringWriter.toString();
			L.i("Exception:", detail);
			// TODO:
		}

	}
}