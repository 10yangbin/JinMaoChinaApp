package com.guomao.propertyservice.widget;

import com.jinmaochina.propertyservice.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MyProgressDialog {

	public static Dialog show(Context context, String msg, boolean cancelAble,
			OnCancelListener onCancelListener) {
		Dialog dialog = new Dialog(context, R.style.loading_dialog);
		dialog.setCancelable(cancelAble);
		dialog.setCanceledOnTouchOutside(false);
		if (onCancelListener != null) {
			dialog.setOnCancelListener(onCancelListener);
		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.my_loading_dialog, null);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
		tipTextView.setText(msg);// 设置加载信息

		dialog.setContentView(view);
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}
}
