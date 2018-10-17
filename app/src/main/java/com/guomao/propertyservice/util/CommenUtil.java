package com.guomao.propertyservice.util;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class CommenUtil {
	public static String showImei(Context context) {

		String onlymark = null;
		if(isPadBySize(context)){
			 WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			onlymark =wifi.getConnectionInfo().getMacAddress();
		}else {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			onlymark = tm.getDeviceId();
		}  
		
		return onlymark;

	}
	
	/**
	 * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
	 * @param context
	 * @return 平板返回 True，手机返回 False
	 */
	public static boolean isPadBySize(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK) 
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public static boolean isPadByCall(Context context) {
	    TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE;
	}
	
	
	/*
	  生成不重复随机字符串包括字母数字

	  @param len
	 * @return
	 */
	/*public static String generateRandomStr(int len) {
	    //字符源，可以根据需要删减
	    String generateSource = "0123456789abcdefghigklmnopqrstuvwxyz";
	    String rtnStr = "";
	    for (int i = 0; i < len; i++) {
	        //循环随机获得当次字符，并移走选出的字符
	        String nowStr = String.valueOf(generateSource.charAt((int) Math.floor(Math.random() * generateSource.length())));
	        rtnStr += nowStr;
	        generateSource = generateSource.replaceAll(nowStr, "");
	    }
	    return rtnStr;
	}

	public static void main(String[] args) {
	    for (int i = 0; i < 10; i++) {
	        System.out.println(generateRandomStr(8));
	    }
	}*/

}
