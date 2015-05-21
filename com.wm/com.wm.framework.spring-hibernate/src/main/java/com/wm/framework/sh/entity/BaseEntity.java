package com.wm.framework.sh.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 本类是所有实体类的SuperEntity
 * 
 * @author
 * @version
 */
public class BaseEntity implements Serializable {

	/*
	 * 未删除
	 */
	public static final Integer delMark_no = 0;
	/*
	 * 已删除
	 */
	public static final Integer delMark_yes = 1;
	/**
	 * BaseEntity.java
	 */
	private static final long serialVersionUID = 5827106886522450446L;
	private Integer state; // 状态
	private Long createUserId; // 创建人 默认当前用户ID
	private Timestamp createTime; // 创建时间 默认当前时间
	private Long updateUserId; // 修改人 默认当前用户ID
	private Timestamp updateTime; // 修改时间 默认当前时间
	private Long delUserId; // 删除人 默认当前用户ID
	private Timestamp delTime; // 删除时间 默认当前时间
	private Integer delMark = 0; // 删除标记 0：正常 1：删除

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Long getDelUserId() {
		return delUserId;
	}

	public void setDelUserId(Long delUserId) {
		this.delUserId = delUserId;
	}

	public Timestamp getDelTime() {
		return delTime;
	}

	public void setDelTime(Timestamp delTime) {
		this.delTime = delTime;
	}

	public Integer getDelMark() {
		return delMark;
	}

	public void setDelMark(Integer delMark) {
		this.delMark = delMark;
	}

}
