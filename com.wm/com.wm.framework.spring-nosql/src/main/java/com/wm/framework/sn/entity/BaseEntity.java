package com.wm.framework.sn.entity;


/**
 * 描述：本类是所有实体类的SuperEntity<br>
 * 作者：王猛 <br>
 * 修改日期：2015年3月20日上午10:26:01 <br>
 * E-mail: <br>
 */
public class BaseEntity {
	/**
	 * 唯一标示
	 */
	private Long id;

	private Long createUserId; // 创建人 默认当前用户ID
	private Long createTime; // 创建时间 默认当前时间
	private Long updateUserId; // 修改人 默认当前用户ID
	private Long updateTime; // 修改时间 默认当前时间
//	private Long delUserId; // 删除人 默认当前用户ID
//	private Long delTime; // 删除时间 默认当前时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
}
