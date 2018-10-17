package com.guomao.propertyservice.util;

import com.guomao.propertyservice.main.MainApplication;
import com.jinmaochina.propertyservice.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class MyNotification {
	public NotificationManager manager;
	public Notification notification;
	public NotificationCompat.Builder builder;

	public void setUpNotification(Context context, String title, int progress) {
		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ly).setContentTitle(title)
				.setContentText(String.valueOf(progress));
		builder.setAutoCancel(true);
		builder.setSound(Uri.parse("android.resource://" + MainApplication.getInstance().getPackageName() + "/" + R.raw.sound_8868));
		// builder.setProgress(100, 0, false);
		builder.setContentIntent(getDefalutIntent(context,
				PendingIntent.FLAG_CANCEL_CURRENT));
		manager.notify(0, builder.build());
	}

	public PendingIntent getDefalutIntent(Context context, int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
				new Intent(), flags);
		return pendingIntent;
	}
}
