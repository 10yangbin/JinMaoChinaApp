package com.guomao.propertyservice.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;

public class MyProgress {
	static Dialog show;

	public static void show(String text, Context context) {
		dismiss();
		show = MyProgressDialog.show(context, text, true, null);
	}

	public static void show(int resID, Context context) {
		dismiss();
		show = MyProgressDialog.show(context,
				context.getResources().getString(resID), true, null);
	}

	public static void show(String text, Context context, boolean cancelAble) {
		dismiss();
		show = MyProgressDialog.show(context, text, cancelAble, null);
	}

	public static void show(String text, Context context, boolean cancelAble,
			OnCancelListener onCancelListener) {
		dismiss();
		show = MyProgressDialog.show(context, text, cancelAble,
				onCancelListener);
	}

	public static void dismiss() {
		try {
			if (show != null && show.isShowing()) {
				show.dismiss();
				show = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
