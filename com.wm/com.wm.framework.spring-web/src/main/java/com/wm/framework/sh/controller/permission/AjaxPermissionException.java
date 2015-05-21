package com.wm.framework.sh.controller.permission;

@SuppressWarnings("serial")
public class AjaxPermissionException extends PermissionException {

	public AjaxPermissionException(String noPermissionMs) {
		super(noPermissionMs);
		this.noPermissionMs = noPermissionMs;
	}
	
	private String noPermissionMs;

	public String getNoPermissionMs() {
		return noPermissionMs;
	}

	public void setNoPermissionMs(String noPermissionMs) {
		this.noPermissionMs = noPermissionMs;
	}
	
}
