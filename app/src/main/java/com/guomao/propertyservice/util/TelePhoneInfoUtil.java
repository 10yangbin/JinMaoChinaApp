package com.guomao.propertyservice.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取手机信息工具类
 * 
 * @author Administrator
 * 
 */
public class TelePhoneInfoUtil {

	private static final String TAG = "TelePhoneInfoUtil";

	/**
	 * 获取手机识唯一识别码
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * 获取手机sdk版本
	 * 
	 * 
	 * @return
	 */
	public static String getPhoneVersion() {
		return Build.VERSION.SDK_INT + "";
	}

	/**
	 * 获取屏幕分辨率
	 * 
	 * @param activity
	 * @return
	 */
	public static String getRatio(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		String ratio = dm.widthPixels + "*" + dm.heightPixels;
		return ratio;
	}

	/**
	 * 获取手机型号
	 * 
	 * 
	 * @return
	 */
	public static String getPhoneType() {
		return Build.DEVICE;
	}

	/**
	 * 获取用户IP
	 * 
	 * @return
	 */
	public static String getUserIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * 获取网络信息
	 * 
	 * @return
	 */
	public static String getNetwork(Context context) {
		String netWork = null;
		boolean isWifi = false;
		boolean isConect = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (connectivity != null) {
			info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if(info[i].isConnected()){
						isConect = true;
						if (info[i].getTypeName().equals("WIFI")
								&& info[i].isConnected()) {
							isWifi = true;
							netWork = "wifi";
							break;
						}
					}
				}
			}
		}
		if (!isWifi && isConect) {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			int type = telephonyManager.getNetworkType();
			// NETWORK_TYPE_GPRS GPRS网络 1
			// NETWORK_TYPE_EDGE EDGE网络 2
			// NETWORK_TYPE_UMTS UMTS网络 3
			// NETWORK_TYPE_HSDPA HSDPA网络 8
			// NETWORK_TYPE_HSUPA HSUPA网络 9
			// NETWORK_TYPE_HSPA HSPA网络 10
			// NETWORK_TYPE_CDMA CDMA网络,IS95A �?IS95B. 4
			// NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5
			// NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6
			// NETWORK_TYPE_1xRTT 1xRTT网络 7
			switch (type) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
				netWork = "2G";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				netWork = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:  //（13）  所对应的就是4G网络
				netWork = "4G";
				break;
			default:
				netWork = "other";
			}
		}
		return netWork;
	}

	/**
	 * 获取用户手机号码
	 */
	public static String getPhoneNumber(Context context) {
		String number = "";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		number = telephonyManager.getLine1Number();
		return number;
	}
}
