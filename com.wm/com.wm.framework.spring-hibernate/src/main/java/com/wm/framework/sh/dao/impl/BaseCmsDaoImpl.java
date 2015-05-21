/**
 * BaseDaoImpl.java
 *
 * 功  能： 这是一个所有dao实现类都要继承的基类
 * 类名： BaseDaoImpl

 *
 * Copyright (c) 2012, 2013 CMCC All Rights Reserved.
 * LICENSE INFORMATION
 */
package com.wm.framework.sh.dao.impl;

import java.io.Serializable;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;

import com.wm.framework.sh.entity.BaseEntity;

/**
 * 本类是所有dao实现类都要继承的基类，实现基本的增删改查。
 * 
 * @author
 * @version
 */
public abstract class BaseCmsDaoImpl<T extends BaseEntity, PK extends Serializable>
		extends BaseDaoImpl<T, PK> {

	@Resource(name = "sessionFactoryCms")
	public void setSessionFactory0(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
