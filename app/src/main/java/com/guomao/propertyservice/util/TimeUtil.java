package com.guomao.propertyservice.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.net.ParseException;

public class TimeUtil {

	public static long string2Timestamp(String dateString)
			throws ParseException {
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(dateString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		long temp = date1.getTime();// JAVA鐨勬椂闂存埑闀垮害鏄�13浣�
		return temp;
	}

	public static byte[] getBytes(InputStream is) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		is.close();
		bos.flush();
		byte[] result = bos.toByteArray();
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public String getTwoTimeDifference(String time1, String time2) {
		String difference = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(time1);
			Date d2 = df.parse(time2);
			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
					* (1000 * 60 * 60))
					/ (1000 * 60);
			difference = "" + days + "天" + hours + "小时" + minutes + "分";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}

	@SuppressLint("SimpleDateFormat")
	public static long getTwoTimeDifferenceSecond(String time1, String time2) {
		long minutes = 0;
		long diff = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date d1 = df.parse(time1);
			Date d2 = df.parse(time2);
			diff = (d1.getTime() - d2.getTime()) / 1000;// 这样得到的差值是微秒级别
			// long days = diff / (1000 * 60 * 60 * 24);
			//
			// long hours = (diff - days * (1000 * 60 * 60 * 24))
			// / (1000 * 60 * 60);
			//
			// minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
			// * (1000 * 60 * 60))
			// / (1000 * 60);
			// difference = "" + days + "天" + hours + "小时" + minutes + "分";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diff;
	}

	public static String getSeconds(int second) {
		int h = 0;
		int d = 0;
		int s = 0;
		int temp = second % 3600;
		if (second > 3600) {
			h = second / 3600;
			if (temp != 0) {
				if (temp > 60) {
					d = temp / 60;
					if (temp % 60 != 0) {
						s = temp % 60;
					}
				} else {
					s = temp;
				}
			}
		} else {
			d = second / 60;
			if (second % 60 != 0) {
				s = second % 60;
			}
		}
		return d + "分" + s + "秒";
	}
}
