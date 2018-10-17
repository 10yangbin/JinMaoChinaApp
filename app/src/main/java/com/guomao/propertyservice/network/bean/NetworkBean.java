package com.guomao.propertyservice.network.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;


/**
 * 业务请求返回json对应的数据接收处理
 * @author Administrator
 *
 */
public class NetworkBean implements Serializable {

	private static final long serialVersionUID = 7113186670868480938L;
//	private int statusCode;
	private String message;
	private JSONObject data;
	private boolean isSucc;

	public NetworkBean() {

	}

	public NetworkBean(String requestResult) {
		if (!TextUtils.isEmpty(requestResult)) {
			try {
				JSONObject json = new JSONObject(requestResult);
//				this.statusCode = json.getInt("resultcode");
				this.message = json.getString("msg");
				this.isSucc=json.getBoolean("ret");

				try {
					String dataString = json.getString("retdata");
					if (!dataString.equals("null")
							&& !TextUtils.isEmpty(dataString)) {
						this.data = json.getJSONObject("retdata");
					}
				} catch (Exception e) {
					Log.d("NetworkBean", "数据解析异常");
					e.printStackTrace();
				}
				Log.d("NetworkBean", "数据解析成功");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.d("NetworkBean", "没有解析到数据");
		}
	}

	// public int getStatusCode() {
	// return statusCode;
	// }
	//
	// public void setStatusCode(int statusCode) {
	// this.statusCode = statusCode;
	// }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public boolean isSucc() {
		return isSucc;
	}

	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}

}
