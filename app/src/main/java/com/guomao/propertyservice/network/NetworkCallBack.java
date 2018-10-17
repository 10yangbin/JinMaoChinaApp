package com.guomao.propertyservice.network;


/**
 * 网络请求的回调接口
 * @author Administrator
 *
 */
public interface NetworkCallBack {
	void onLoadSuccess(Object obj);

	void onLoadFail();

	void onLoading(int progress);
}
