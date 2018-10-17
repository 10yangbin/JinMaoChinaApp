package com.guomao.propertyservice.model.offlineoperate;



/**
 * 离线报事的联网接口
 * @author Administrator
 *
 */
public interface OfflineOperateCom {
	/**
	 * 
	 * 将离线的数据发送至服务器   
	 * 
	 * @param operate
	 *            离线的实体
	 */
	String sendOfflineData2Server(Operation operate)throws Exception;

}
