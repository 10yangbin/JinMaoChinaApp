package com.guomao.propertyservice.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
	/**
	 * 初始化GSON
	 * 
	 * @return
	 */
	public static Gson gson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
		return builder.create();
	}

}
