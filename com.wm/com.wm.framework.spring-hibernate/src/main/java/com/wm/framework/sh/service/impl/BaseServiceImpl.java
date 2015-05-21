/**
 
 *
 * 功  能： 这是一个所有service实现类都要继承的基类
 * 类名： BaseServiceImpl
 *
 *   ver        变更日              担当者     变更内容
 * ──────────────────────────────────
 *
 * Copyright (c) 2012, 2013 CMCC All Rights Reserved.
 * LICENSE INFORMATION
 */
package com.wm.framework.sh.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.wm.common.exception.DBException;
import com.wm.common.util.Pagination;
import com.wm.framework.sh.dao.BaseDao;
import com.wm.framework.sh.entity.BaseEntity;
import com.wm.framework.sh.service.BaseService;
import com.wm.framework.util.db.SearchCondition;
import com.wm.framework.util.db.SortCondition;

/**
 * 本类是所有service实现类都要继承的基类，实现基本的增删改查。
 * 
 * @author
 * @version
 */
@Transactional
public abstract class BaseServiceImpl<T extends BaseEntity, PK extends Serializable>
		implements BaseService<T, PK> {

	public abstract BaseDao<T, PK> getDao();

	/**
	 * 根据id获取对应的实体
	 * 
	 * @param id
	 *            实体的ID
	 * @return T 得到相应的实体
	 * @throws DBException
	 *             获取实体失败
	 */
	public T get(PK id) throws DBException {
		return getDao().get(id);
	}

	/**
	 * 方法名称：save <br>
	 * 描述：持久化实体 <br>
	 * 作者：王猛 <br>
	 * 修改日期：2015年2月4日下午5:02:48
	 * 
	 * @see com.wm.framework.sh.service.BaseService#save(java.lang.Object)
	 * @param entity
	 * @return 添加后的id
	 * @throws DBException
	 */
	public PK save(T entity) throws DBException {
		entity.setState(0);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		if (null == entity.getCreateTime()) {
			entity.setCreateTime(currentTime);
		}
		if (null == entity.getUpdateTime()) {
			entity.setUpdateTime(currentTime);
		}
		entity.setDelMark(0);
		return getDao().save(entity);
	}

	/**
	 * 保存当前实体
	 * 
	 * @param entity
	 *            要保存的实体
	 * @param creator
	 *            创建者id
	 * @throws DBException
	 *             保存失败
	 */
	public PK save(T entity, Long creator) throws DBException {
		entity.setCreateUserId(creator);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		entity.setCreateTime(currentTime);
		entity.setDelMark(0);
		return getDao().save(entity);
	}

	/**
	 * 保存集合中的实体
	 * 
	 * @param entitys
	 *            要保存的实体对象
	 * @throws DBException
	 *             保存失败
	 */
	public Collection<PK> saveAll(Collection<T> entitys) throws DBException {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		for (T entity : entitys) {
			// entity.setCreator(TariffRateSession.getUser().getId());
			entity.setCreateTime(currentTime);
			entity.setDelMark(0);
		}
		return getDao().saveAll(entitys);
	}

	/**
	 * 更新当前实体对象
	 * 
	 * @param entity
	 *            要更新的对象
	 * @throws DBException
	 *             更新失败
	 */
	public void update(T entity) throws DBException {
		// entity.setUpdateUser(TariffRateSession.getUser().getId());
		// entity.setUpdateUserId(-1L);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		entity.setUpdateTime(currentTime);
		getDao().update(entity);
	}

	/**
	 * 更新当前实体对象
	 * 
	 * @param entity
	 *            要更新的对象
	 * @param updateUser
	 *            更新者ID
	 * @throws DBException
	 *             更新失败
	 */
	public void update(T entity, Long updateUser) throws DBException {
		entity.setUpdateUserId(updateUser);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		entity.setUpdateTime(currentTime);
		getDao().update(entity);
	}

	/**
	 * 逻辑删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws DBException
	 *             删除失败
	 */
	public void delete(T entity) throws DBException {
		// entity.setDelUserId(-1L);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		entity.setDelTime(currentTime);
		getDao().delete(entity);
	}

	public void delete(PK id) throws DBException {
		this.getDao().delete(id);
	}

	/**
	 * 物理删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws DBException
	 *             删除失败
	 */
	public void deleteFromDB(T entity) throws DBException {
		getDao().deleteFromDB(entity);
	}

	/**
	 * 根据检索条件，排序条件以及分页条件获取对应实体的一页的列表
	 * 
	 * @param searchConditions
	 *            检索条件
	 * @param pageCondition
	 *            分页条件
	 * @param sortConditions
	 *            排序条件
	 * @return List 对应实体的一页的列表
	 * @throws DBException
	 *             获取失败
	 */
	public void findPageData(List<SearchCondition> searchConditions,
			Pagination<T> pagination, List<SortCondition> sortConditions)
			throws DBException {
		getDao().findPageData(searchConditions, pagination, sortConditions);
	}

	/**
	 * 方法名称: findPageDataCms<br>
	 * 描述：根据检索条件，排序条件以及分页条件获取对应实体的一页的列表Cms 作者: 王叶 修改日期：2014年10月13日下午6:43:38
	 * 
	 * @param searchConditions
	 * @param pagination
	 * @param sortConditions
	 * @throws DBException
	 */
	public void findPageDataCms(List<SearchCondition> searchConditions,
			Pagination<T> pagination, List<SortCondition> sortConditions)
			throws DBException {
		// getDao().findPageData(searchConditions, pagination, sortConditions);
		getDao().findPageDataCms(searchConditions, pagination, sortConditions);
	}

	public void findPageData(Pagination<T> pagination,
			SearchCondition searchCondition) throws DBException {
		this.findPageData(pagination, searchCondition, null);
	}

	public void findPageData(Pagination<T> pagination,
			SearchCondition searchCondition, SortCondition sortCondition)
			throws DBException {

		List<SearchCondition> searchConditions = null;
		if (null != searchCondition) {
			searchConditions = new ArrayList<SearchCondition>();
			searchConditions.add(searchCondition);
		}

		List<SortCondition> sortConditions = null;
		if (null != sortCondition) {
			sortConditions = new ArrayList<SortCondition>();
			sortConditions.add(sortCondition);
		}
		this.findPageData(searchConditions, pagination, sortConditions);
	}

	/**
	 * 根据检索条件，排序条件获取该实体的列表
	 * 
	 * @param searchConditions
	 *            检索条件
	 * @param sortConditions
	 *            排序条件
	 * @return List 该实体的列表
	 * @throws DBException
	 *             获取列表失败
	 */
	public List<T> findAllData(List<SearchCondition> searchConditions,
			List<SortCondition> sortConditions) throws DBException {
		return getDao().findAllData(searchConditions, sortConditions);
	}

	public List<T> findAllData(List<SearchCondition> searchConditions)
			throws DBException {
		return findAllData(searchConditions, null);
	}

	public List<T> findAllData(SearchCondition searchCondition)
			throws DBException {
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		searchConditions.add(searchCondition);
		return findAllData(searchConditions, null);
	}

	public List<T> findAllData() throws DBException {
		SearchCondition searchCondition = null;
		return findAllData(searchCondition);
	}

	public JSONArray findAllJson(final String hql, final Object... values) {
		return this.getDao().findAllJson(hql, values);
	}

	/**
	 * 根据检索条件获取总数
	 * 
	 * @param searchConditions
	 *            检索条件
	 * @return long 总数
	 * @throws DBException
	 *             获取失败
	 */
	public long getTotalCount(List<SearchCondition> searchConditions)
			throws DBException {
		return getDao().getTotalCount(searchConditions);
	}

	/**
	 * 根据获得的参数删除对应的数据，是逻辑删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws DBException
	 *             删除失败
	 */
	public void deleteAll(Collection<PK> ids) throws DBException {

		getDao().deleteAll(ids);
	}

	/**
	 * 根据获得的参数删除对应的数据，是物理删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws DBException
	 *             删除失败
	 */
	public void deleteAllFromDB(Collection<PK> ids) throws DBException {
		getDao().deleteAllFromDB(ids);
	}

	// public List<T> findAllDataDB(List<SearchCondition> searchConditions,
	// List<SortCondition> sortConditions) throws DBException {
	// return getDao().findAllDataDB(searchConditions, sortConditions);
	// }

	/**
	 * 推迟到一天的最后一秒钟。
	 * 
	 * @param endDate
	 *            要推迟的日期
	 * @return
	 */
	protected Date postponeToMidnight(Date endDate) {
		if (endDate == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	protected Date postponeToBeforeDawn(Date startDate) {
		if (startDate == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.set(Calendar.HOUR_OF_DAY, 00);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		return c.getTime();
	}

	public void findBean(String beanName, Pagination<Object> pagination) {
		getDao().findBean(beanName, pagination);
	}

	public void findPJsonDataWithTotal(String hqlStr,
			Pagination<JSONObject> pagination, List<Object> parameters) {
		this.getDao().findPJsonData(hqlStr, pagination, parameters);
	}
}
