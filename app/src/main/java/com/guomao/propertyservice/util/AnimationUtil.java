package com.guomao.propertyservice.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationUtil {

	public static void anim(Context context, View v, int animId) {
		Animation shake = AnimationUtils.loadAnimation(context, animId);
		v.startAnimation(shake);
	}

}
