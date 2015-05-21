package com.wm.framework.sn.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class BaseInfo implements Serializable {
	private static final long serialVersionUID = 1707559457400100565L;
	
	private String createUserId; // 创建人 默认当前用户ID
	private Timestamp createTime; // 创建时间 默认当前时间
	private String updateUserId; // 修改人 默认当前用户ID
	private Timestamp updateTime; // 修改时间 默认当前时间
	private String delUserId; // 删除人 默认当前用户ID
	private Timestamp delTime; // 删除时间 默认当前时间

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getDelUserId() {
		return delUserId;
	}

	public void setDelUserId(String delUserId) {
		this.delUserId = delUserId;
	}

	public Timestamp getDelTime() {
		return delTime;
	}

	public void setDelTime(Timestamp delTime) {
		this.delTime = delTime;
	}

}
