package com.guomao.propertyservice.config;

import com.jinmaochina.propertyservice.BuildConfig;

public class AppConfig {
	public static final boolean DEBUG = BuildConfig.DEBUG;
	// 设置多久之前的垃圾文件将被删除，以月为单位
	public static final int DEL_DATA_BEFORE_DATE = -1;
}
