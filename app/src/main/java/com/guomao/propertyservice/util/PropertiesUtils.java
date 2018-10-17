package com.guomao.propertyservice.util;

import java.io.IOException;
import java.util.Properties;

import android.content.Context;

public class PropertiesUtils {
	private static final String TAG = "PropertiesUtils";
	private static Properties properties;
	private static Properties urlProperties;

	public synchronized static Properties getProperties(Context c) {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(c.getAssets().open("dms365.properties"));
			} catch (IOException e) {
				L.e(TAG, "load properties fail");
			}
		}
		return properties;
	}

	public synchronized static Properties getUrlProperties(Context c) {
		if (urlProperties == null) {
			urlProperties = new Properties();
			try {
				urlProperties.load(c.getAssets().open("url.properties"));
			} catch (IOException e) {
				L.e(TAG, "load properties fail");
			}
		}
		return urlProperties;
	}

	public static String getValue(Context c, String tag) {
		return (String) getProperties(c).get(tag);
	}

	public static boolean writeToValue(Context c, String tag, String value) {
		try {
			getProperties(c).setProperty(tag, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getUrlValue(Context c, String tag) {
		return (String) getUrlProperties(c).get(tag);
	}

}
