package com.guomao.propertyservice.javascripinterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.guomao.propertyservice.communcation.WebClient;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;


/**
 * 计划工单上传的web调用方法
 * @author Administrator
 *
 */
public class FileUpload {
	public static final String URL = MainApplication.planUpdata;
	public static final String UPDATE_URL = URL;
	private Context context;

    public FileUpload(WebView webView) {
		super();
		this.context = webView.getContext();
        WebView webView1 = webView;
	}

	@JavascriptInterface
	public String fileUpload(String param ,final String files) {
		SharedPrefUtil.Log("param:"+param);
		String resp = null;
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		WebClient client = WebClient.getInstance();
		String image=null;
		List<String> fileList = null;
		try {
			if (!StringUtil.isNull(files)) {
				fileList = StringUtil.convertString2ImageList(files);
			}
			pairs.add(new BasicNameValuePair("P", param.toString()));
			resp = client.doPost(URL, pairs, fileList);
			JSONObject result = new JSONObject(resp);
			image= result.optString("success");
		} catch (Exception e) {
			handleException(e, false);
		}

		return image;
	}

	private void handleException(Exception e, boolean needOnline) {

		String text = e.getMessage();
		if (needOnline) {
			if (e instanceof IOException) {
				text = "您的网络不给力~";
			}
		}
		if (e instanceof JSONException) {
			text = "服务端响应异常，请稍候重试...";
		}
		if (StringUtil.isNull(text)) {
			return;
		}
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}

