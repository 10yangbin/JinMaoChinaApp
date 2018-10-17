package com.guomao.propertyservice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;

public class StringUtil {
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
        return str == null || TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str) || "".equals(str) || "undefined".equalsIgnoreCase(str);
    }

	/**
	 * 将多张图片的路径用？连接后返回
	 * 
	 * @param paths
	 *            图片的路径
	 * @return “” if paths is empty
	 */
	public static String convertImageList2String(List<String> paths) {
		if (paths == null || paths.isEmpty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (String str : paths) {
			builder.append(str);
			builder.append("?");
		}
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	/**
	 * 将以,拼接的路径拆分为一个List
	 * 
	 * @param str
	 *            带拆分的字符串
	 * @return 拆分后的集合
	 */
	public static List<String> convertString2ImageList(String str) {
		ArrayList<String> result = null;
		if (!StringUtil.isNull(str)) {
			String[] strs = str.split(",");
			if (strs != null && strs.length > 0) {
				result = new ArrayList<String>();
				for (String s : strs) {
					if (s.startsWith("file://")) {
						s = s.substring("file://".length());
					} else if (s.startsWith("http://")) {
						continue;
					}
					if(!StringUtil.isNull(s)){
						result.add(s);
					}
				}
			}
		}
		return result;
	}
	public static String convertList2String(List<String> data,String conectString){
		if(data == null || data.isEmpty()){
			return null;
		}
		if(conectString == null){
			conectString = ",";
		}
		StringBuilder sb = new StringBuilder(data.get(0));
		for(int i = 1;i<data.size();i++){
			sb.append(conectString).append(data.get(i));
		}
		return sb.toString();
	}
	public static List<String>convertString2List(String data,String splitString){
		if(splitString == null){
			splitString = ",";
		}
		if(isNull(data))return null;
		String[] splitResult = data.split(splitString);
		return Arrays.asList(splitResult);
	}
}
