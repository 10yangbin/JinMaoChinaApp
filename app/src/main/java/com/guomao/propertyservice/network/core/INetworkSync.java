package com.guomao.propertyservice.network.core;

import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.request.RequestVo;


/**
 * 发送基础和业务数据的 同步请求 的统一方法的接口
 * @author Administrator
 *
 */
public interface INetworkSync {
	String getData(RequestVo requestVo, OperateCallBack operateCallBack)
			throws Exception;
}
