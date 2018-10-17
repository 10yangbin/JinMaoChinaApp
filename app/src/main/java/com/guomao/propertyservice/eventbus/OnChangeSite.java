package com.guomao.propertyservice.eventbus;

public class OnChangeSite {

	private String site_id;
	private String isMonitor;

	public OnChangeSite(String site_id, String isMonitor) {
		this.site_id = site_id;
		this.isMonitor = isMonitor;
	}

	public String getSite_id() {
		return site_id;
	}

	public String getIsMonitor() {
		return isMonitor;
	}

}
