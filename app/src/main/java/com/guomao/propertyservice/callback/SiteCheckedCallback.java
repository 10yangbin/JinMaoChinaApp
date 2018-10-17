package com.guomao.propertyservice.callback;

public interface SiteCheckedCallback {
	void OnChecked(String siteId, String isMonitor);

	void OnCancel();

}
