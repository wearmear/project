package com.wm.common.entity;

/***
 * 返回客户端json数据载体类
 * 
 * @author meng
 * 
 */
public class JsonResult {
	/*
	 * 是否成功
	 */
	private boolean success = false;
	/*
	 * 提示消息
	 */
	private String ms = null;
	/*
	 * 数据对象
	 */
	private Object data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
