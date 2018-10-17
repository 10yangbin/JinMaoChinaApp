package com.guomao.propertyservice.model.user;

import java.util.List;

import com.guomao.propertyservice.util.StringUtil;

public class User {
	/**
	 * 用户名 等同于用户id
	 */
	private String user_name;
	private String user_pwd;
	private String user_id;
	/**
	 * 员工姓名
	 */
	private String user_alias;
	/**
	 * 是否班长 班长1 普通0
	 */
	private String is_monitor;
	/**
	 * 原因大类
	 */
	private List<String> tag;

	private String site_id;

	private String mobile;
	private String siteList;
	private String permissions="1,1-1,1-2,1-3,1-4,1-5,1-6,1-7,1-8,1-9,1-10,1-11,1-12,1-13,2,2-1,2-2,2-3,3,3-1,3-2,3-3,3-4,3-5";
	private int cf_type;
	
	public int getCf_type() {
		return cf_type;
	}

	public void setCf_type(int cf_type) {
		this.cf_type = cf_type;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_alias() {
		return user_alias;
	}

	public void setUser_alias(String user_alias) {
		this.user_alias = user_alias;
	}

	public String getIs_monitor() {
		return is_monitor;
	}

	public void setIs_monitor(String is_monitor) {
		this.is_monitor = is_monitor;
	}

	public List<String> getTag() {
		return tag;
	}

	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	public String getSite_id() {
		return site_id;
	}

	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}

	public String getSiteList() {
		return siteList;
	}

	public void setSiteList(String siteList) {
		this.siteList = siteList;
	}
	public boolean hasPermission(String code){
		
		//is_monitor = permissions;
		
		if(StringUtil.isNull(code) || StringUtil.isNull(is_monitor)){
			return false;
		}
		if(is_monitor.contains(",")){
			return is_monitor.startsWith(code+",")||is_monitor.endsWith(","+code) || is_monitor.contains(","+code+",");
		}else{
			return is_monitor.equals(code);
		}
	}
}
