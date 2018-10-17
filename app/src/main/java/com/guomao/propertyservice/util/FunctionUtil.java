package com.guomao.propertyservice.util;

import java.text.DecimalFormat;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class FunctionUtil {
	public static final String HTTP_WAP = "wap";

	private static long lastClickTime;

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static int parseIntByString(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			LogUtil.logException(e);
			return 0;
		}
	}

	public static double parseDoubleByString(String string) {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			LogUtil.logException(e);
			return 0.00;
		} catch (Exception e) {
			LogUtil.logException(e);
			return 0.00;
		}
	}

	public static String parseNull(String string) {
		return null == string ? "" : string;
	}

	public static String getDouble(double data) {
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(data);
	}

	public static String getDouble(String data) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(parseDoubleByString(data));
	}

	public static String showImei(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		// StringBuilder sb = new StringBuilder();
		//
		// sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		// sb.append("\nDeviceSoftwareVersion = " +
		// tm.getDeviceSoftwareVersion());
		// sb.append("\nLine1Number = " + tm.getLine1Number());
		// sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		// sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		// sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		// sb.append("\nNetworkType = " + tm.getNetworkType());
		// sb.append("\nPhoneType = " + tm.getPhoneType());
		// sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		// sb.append("\nSimOperator = " + tm.getSimOperator());
		// sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		// sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		// sb.append("\nSimState = " + tm.getSimState());
		// sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		// sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());

		return tm.getDeviceId();

	}

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
		return (int) px;
	}

	public static int getRelativeTop(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	public static int getRelativeLeft(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	public static int getVersion(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					"com.jinmaochina.propertyservice", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;

	}

	public static String getVersionName(Context context) {
		String verName = "1.0.0";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.jinmaochina.propertyservice", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;

	}

	public static void setListViewEmptyView(ListView listview, Context context,
			int resId) {
		ImageView emptyView = new ImageView(context);
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		emptyView.setImageResource(resId);
		emptyView.setVisibility(View.GONE);
		((ViewGroup) listview.getParent()).addView(emptyView);
		listview.setEmptyView(emptyView);

	}

	public static boolean isNetConn(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
            return !topActivity.getPackageName().equals(context.getPackageName());
		}
		return false;

	}

	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：com.longfor.propertyservice.main.
	 *            OfflineData2Service）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

}