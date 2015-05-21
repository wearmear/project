package com.wm.framework.sh.controller.permission;

@SuppressWarnings("serial")
public class PagePermissionException extends PermissionException {

	public PagePermissionException(String noPermissionRedirectUrl) {
		super(noPermissionRedirectUrl);
		
		this.noPermissionRedirectUrl = noPermissionRedirectUrl;
	}

	private String noPermissionRedirectUrl;

	public String getNoPermissionRedirectUrl() {
		return noPermissionRedirectUrl;
	}

	public void setNoPermissionRedirectUrl(String noPermissionRedirectUrl) {
		this.noPermissionRedirectUrl = noPermissionRedirectUrl;
	}
}
