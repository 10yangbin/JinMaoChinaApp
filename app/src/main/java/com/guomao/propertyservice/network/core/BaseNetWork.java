package com.guomao.propertyservice.network.core;

import org.apache.http.impl.client.DefaultHttpClient;

import com.guomao.propertyservice.communcation.WebClient;
import com.lidroid.xutils.HttpUtils;


/**
 * 
 * 数据请求的基类,用来保存CookieStore();
 *
 */
public class BaseNetWork {
	protected static void useCookie( HttpUtils http){
		if(http != null)
			http.configCookieStore(WebClient.getInstance().getCookieStore());
	}
	protected static void saveCookie(HttpUtils http){

		if(http != null && http.getHttpClient() instanceof DefaultHttpClient){
			DefaultHttpClient dh = (DefaultHttpClient) http.getHttpClient();
			WebClient.getInstance().saveCookie(dh);
		}
	}
}
