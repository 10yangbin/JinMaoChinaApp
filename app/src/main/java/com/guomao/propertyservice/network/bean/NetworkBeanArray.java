package com.guomao.propertyservice.network.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class NetworkBeanArray implements Serializable {

	private static final long serialVersionUID = -2690959393405671076L;
//	private int statusCode;
	private boolean isSucc;
	private String message;
	private String result;

	public NetworkBeanArray() {

	}

	public NetworkBeanArray(String requestResult) {
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
						this.result = dataString;
					}
				} catch (Exception e) {
					Log.d("NetworkBeanArray", "数据解析异常");
					e.printStackTrace();
				}
				Log.d("NetworkBeanArray", "数据解析成功");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.d("NetworkBeanArray", "没有解析到数据");
		}
	}

	// public int getStatusCode() {
	// return statusCode;
	// }
	//
	// public void setStatusCode(int statusCode) {
	// this.statusCode = statusCode;
	// }

	public boolean isSucc() {
		return isSucc;
	}

	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}