package com.guomao.propertyservice.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.guomao.propertyservice.util.GsonUtil;

public class Site implements Serializable {

	private static final long serialVersionUID = 4591270579316853082L;
	private String site_id;
	private String site_name;
	private String is_monitor;
	private int staus;// 0:未下载离线库 、1:离线库正常 、2:有更新 、 3:正在下载 4:下载失败
	private boolean isBaseDownloading;
	private boolean isBusinessDownloading;
	private String basePath;
	private String businessPath;
	private String baseVer;
	private String businessVer;
	
	public String getIs_monitor() {
		return is_monitor;
	}

	public void setIs_monitor(String is_monitor) {
		this.is_monitor = is_monitor;
	}

	public String getSite_id() {
		return site_id;
	}

	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}


	public String getSite_name() {
		return site_name;
	}

	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}

	public int getStaus() {
		return staus;
	}

	public void setStaus(int staus) {
		this.staus = staus;
	}

	public boolean isBaseDownloading() {
		return isBaseDownloading;
	}

	public void setBaseDownloading(boolean isBaseDownloading) {
		this.isBaseDownloading = isBaseDownloading;
	}

	public boolean isBusinessDownloading() {
		return isBusinessDownloading;
	}

	public void setBusinessDownloading(boolean isBusinessDownloading) {
		this.isBusinessDownloading = isBusinessDownloading;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBusinessPath() {
		return businessPath;
	}

	public void setBusinessPath(String businessPath) {
		this.businessPath = businessPath;
	}

	public String getBaseVer() {
		return baseVer;
	}

	public void setBaseVer(String baseVer) {
		this.baseVer = baseVer;
	}

	public String getBusinessVer() {
		return businessVer;
	}

	public void setBusinessVer(String businessVer) {
		this.businessVer = businessVer;
	}

	public List<Site> getSiteList(String str) {
		List<Site> siteList = new ArrayList<Site>();
		try {
			siteList = GsonUtil.gson().fromJson(str,
					new TypeToken<List<Site>>() {
					}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return siteList;
	}
}
