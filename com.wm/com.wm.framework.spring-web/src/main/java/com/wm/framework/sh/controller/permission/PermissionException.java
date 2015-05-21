package com.wm.framework.sh.controller.permission;

import com.wm.common.exception.CustomException;

@SuppressWarnings("serial")
public class PermissionException extends CustomException {
	public PermissionException(String ms) {
		super(ms);
	}
}
