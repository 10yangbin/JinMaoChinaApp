package com.guomao.propertyservice.config;

import com.guomao.propertyservice.main.MainApplication;


/**
 * 请求服务器的地址  
 * @author YANGBIN
 *
 */
public class CommenUrl {

	/**
	 * 根URL
	 */

	public static String baseUrl = MainApplication.finalUrl;

	/**
	 * 报事
	 */
	public static final String baoshiAction = "/task/save";
	/**
	 * 抢单
	 */
	public static final String qdAction = "/process/get/task";
	/**
	 * 派单
	 */
	public static final String pdAction = "/process/assign";
	
	/**
	 * 退回
	 */
	public static final String thAction = "/process/back";
	/**
	 * 备注
	 */
	public static final String tzAction = "/process/record";
	/**
	 * 完成任务
	 */
	public static final String finishAction = "/process/complete";
	/**
	 * 登录
	 */
	public static final String loginAction = "/sys/login/valid";
	/**
	 * 注销
	 */
	public static final String logOutAction = "/sys/logout";
	/**
	 * 基础数据
	 */
	public static final String baseData = "/data/base/down";
	/**
	 * 业务数据
	 */

	public static final String businessData = "/data/business/worksheet";
	/**
	 * 二装业务数据的刷新返回
	 */

	public static final String erzhuangbusinessData = "/data/business/decoration";
	
	/**
	 * 收发函业务数据的刷新返回
	 */

	public static final String leteersData = "/data/business/letters";
	
	/**
	 * 预警数据的刷新返回
	 */

	public static final String waringsData = "/data/business/warning";
	
	/**
	 * 通知数据的刷新返回
	 */

	public static final String noticeData = "/data/business/notice";
	
	public static final String homepageData = "/data/business/homepage";
	
	/**
	 * 车辆数据的刷新返回
	 */

	public static final String vehicleData = "/data/business/vehicle";
	
	/**
	 * 客户信息数据的刷新返回
	 */

	public static final String customerData = "/data/business/customer";
	
	/**
	 * 开始处理
	 */
	public static final String Processing = "/process/exec";
	/**
	 * 图片下载
	 */
	
	public static final String downloadImg = MainApplication.finalUrl+"/data/img/down";
	
	/**
	 * 二装相关图片下载
	 */
	
	public static final String downloadImages = MainApplication.ImageDownUrl;
	
	/**
	 * apk升级检查更新
	 */
	public static final String checkUpdate = "/get/vn";
	/**
	 * DB检查更新
	 */
	public static final String checkDbUpdate =MainApplication.finalUrl+"/get/db/scan";


	/**
	 * 转换web请求的url
	 *
	 * @param action
	 * @return
	 */
	public static String getActionUrl(String action) {
		String actionUrl = null;
		if (action.contains("/")) {
			actionUrl = baseUrl + action;
		}else if(action.equals("P")){
			actionUrl = MainApplication.planUpdata;
		}else {
			String url = covert(action);
			actionUrl = baseUrl + url;
		}
		
		return actionUrl;
	}

	public static String covert(String action) {

		if (CommenAction.ACTION_MATEDATA_DOWNLOAD
				.equalsIgnoreCase(action)) {
			return CommenUrl.baseData;
		} else if (CommenAction.ACTION_NEW_JOBDATA.equalsIgnoreCase(action)) {

		} else if (CommenAction.ACTION_WO_BS.equalsIgnoreCase(action)) {
			return CommenUrl.baoshiAction;
		} else if (CommenAction.ACTION_WO_JS.equalsIgnoreCase(action)) {
			return CommenUrl.Processing;
		} else if (CommenAction.ACTION_WO_PF.equalsIgnoreCase(action)) {
			return CommenUrl.pdAction;
		} else if (CommenAction.ACTION_WO_QD.equalsIgnoreCase(action)) {
			return CommenUrl.qdAction;
		} else if (CommenAction.ACTION_WO_TH.equalsIgnoreCase(action)) {
			return CommenUrl.thAction;
		} else if (CommenAction.ACTION_WO_WC.equalsIgnoreCase(action)) {
			return CommenUrl.finishAction;
		}else if (CommenAction.ACTION_WO_ZXTJ.equalsIgnoreCase(action)) {
			return CommenUrl.tzAction;
		}

		return null;
	}
}
