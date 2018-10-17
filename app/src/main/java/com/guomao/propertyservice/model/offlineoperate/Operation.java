package com.guomao.propertyservice.model.offlineoperate;

import java.io.Serializable;

public class Operation implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer operate_id;
	private String operate_taskId;
	private String operate_a;
	private String operate_p;
	private String operate_files;
	private int operate_status;
	private String operate_result;
	private String create_time;
	private String submit_time;

	public Integer getOperate_id() {
		return operate_id;
	}

	public void setOperate_id(Integer operate_id) {
		this.operate_id = operate_id;
	}

	public String getOperate_taskId() {
		return operate_taskId;
	}

	public void setOperate_taskId(String operate_taskId) {
		this.operate_taskId = operate_taskId;
	}

	public String getOperate_a() {
		return operate_a;
	}

	public void setOperate_a(String operate_a) {
		this.operate_a = operate_a;
	}

	public String getOperate_p() {
		return operate_p;
	}

	public void setOperate_p(String operate_p) {
		this.operate_p = operate_p;
	}

	public String getOperate_files() {
		return operate_files;
	}

	public void setOperate_files(String operate_files) {
		this.operate_files = operate_files;
	}

	public int getOperate_status() {
		return operate_status;
	}

	public void setOperate_status(int operate_status) {
		this.operate_status = operate_status;
	}

	public String getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getSubmit_time() {
		return submit_time;
	}

	public void setSubmit_time(String submit_time) {
		this.submit_time = submit_time;
	}

}
