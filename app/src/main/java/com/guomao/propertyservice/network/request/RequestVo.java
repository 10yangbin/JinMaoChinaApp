package com.guomao.propertyservice.network.request;

import java.util.List;

import org.json.JSONObject;

import com.guomao.propertyservice.config.CommenUrl;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;


/**
 * 请求头对象
 * @author Administrator
 *
 */
public class RequestVo {
	private String requestUrl;
	private HttpMethod requestMethod;
	private RequestParams requestParams;
	private JSONObject json;
	private List<String> filesPath;
	private static RequestVo instance = null;
	
	public synchronized static RequestVo getInstance() {
		if (instance == null) {
			instance = new RequestVo();
		}
		return instance;
	}
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public HttpMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(HttpMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public RequestParams getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(RequestParams requestParams) {
		this.requestParams = requestParams;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public List<String> getFilesPath() {
		return filesPath;
	}

	public void setFilesPath(List<String> filesPath) {
		this.filesPath = filesPath;
	}

}
