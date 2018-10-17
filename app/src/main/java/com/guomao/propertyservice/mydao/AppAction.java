package com.guomao.propertyservice.mydao;

import com.guomao.propertyservice.network.NetworkCallBack;
import com.guomao.propertyservice.network.request.DownLoadFileVo;

import android.app.Activity;

public interface AppAction {
	/**
	 * 下载文件
	 * 
	 * @param context
	 * @param downLoadFileVo
	 * @param networkCallBack
	 */
    void downLoadFile(Activity context, DownLoadFileVo downLoadFileVo,
                      NetworkCallBack networkCallBack);

	/**
	 * 检查更新
	 * 
	 * @param context
	 *            上下文
	 * @param networkCallBack
	 *            回调
	 */
    void checkUpdate(Activity contextxt);

	void checkDbUpdate(Activity context, String siteId);
}
