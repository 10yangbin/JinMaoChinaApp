package com.guomao.propertyservice.eventbus;

public class OnSiteChanged {
	private String siteName;

	public OnSiteChanged(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteName() {
		return siteName;
	}

}
