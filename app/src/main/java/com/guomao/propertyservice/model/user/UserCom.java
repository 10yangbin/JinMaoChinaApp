package com.guomao.propertyservice.model.user;

import com.guomao.propertyservice.network.OperateCallBack;

public interface UserCom {
	User login(String username, String password, OperateCallBack operateCallBack)
			throws Exception;

	void logout(String user_id, OperateCallBack operateCallBack)
			throws Exception;

	
	String  getDeviceInfo(String url, String qrCode) throws Exception;
}
