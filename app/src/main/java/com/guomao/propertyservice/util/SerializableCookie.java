package com.guomao.propertyservice.util;

import java.io.Serializable;
import java.util.Date;

import org.apache.http.cookie.Cookie;

public class SerializableCookie implements Cookie, Serializable {
	private static final long serialVersionUID = 1L;
	private String comment;
	private String commentURL;
	private String domain;
	private Date expiryDate;
	private String name;
	private String path;
	private int[] ports;
	private String value;
	private int version;
	private boolean isPersistent;
	private boolean isSecure;
	
	
	public SerializableCookie(Cookie cookie){
		if(cookie!=null){
			this.comment = cookie.getComment();
			this.commentURL = cookie.getCommentURL();
			this.domain = cookie.getDomain();
			this.expiryDate = cookie.getExpiryDate();
			this.name = cookie.getName();
			this.path = cookie.getPath();
			this.ports = cookie.getPorts();
			this.value = cookie.getValue();
			this.version = cookie.getVersion();
			this.isPersistent = cookie.isPersistent();
			this.isSecure = cookie.isSecure();
		}
	}
	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public String getCommentURL() {
		return commentURL;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public Date getExpiryDate() {
		return expiryDate;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public int[] getPorts() {
		return ports;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public boolean isExpired(Date date) {
		if(expiryDate == null ||date == null){ 
			return false;
		}else{
			return date.getTime()>expiryDate.getTime();
		}
	}

	@Override
	public boolean isPersistent() {
		return isPersistent;
	}

	@Override
	public boolean isSecure() {
		return isSecure;
	}
	public Cookie getCookie() {
		return this;
	}
}
