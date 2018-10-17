package com.guomao.propertyservice.network.core;

import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.request.RequestVo;


/**
 * 发送基础和业务数据的异步请求的统一方法的接口
 */
public interface INetwork {
	void getData(RequestVo requestVo, OperateCallBack operateCallBack)
			throws Exception;
	
}
