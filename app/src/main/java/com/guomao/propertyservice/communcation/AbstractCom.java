package com.guomao.propertyservice.communcation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guomao.propertyservice.util.GsonUtil;

/**
 * 抽象的联网基类，构建Webclient,实现网页数据的响应
 * 
 * @author Administrator
 * 
 */
public abstract class AbstractCom {
	protected WebClient webClient;
	protected Gson gson;
	protected SimpleDateFormat formator = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public AbstractCom(WebClient webClient) {
		this.webClient = webClient;
		this.gson = GsonUtil.gson();
		formator.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

	}

	/**
	 * 获取网页中的数据
	 * 
	 * @param data
	 * @return 泛型的dataList
	 * @throws Exception
	 */
	protected <T> List<T> getListFromData1(String data) throws Exception {

		List<T> dataList = null;
		if (data != null) {
			try {
				dataList = gson.fromJson(data, new TypeToken<List<T>>() {
				}.getType());
			} catch (Exception e) {
				throw new Exception("数据格式异常");
			}
		}
		return dataList;
	}

}
