package com.guomao.propertyservice.network;


/**
 * 业务操作的回调
 * @author Administrator
 *
 */
public interface OperateCallBack {

	void onLoadSuccess(Object obj);

	void onTokenInvalid();

	void onLoadFail(String msg);

}
