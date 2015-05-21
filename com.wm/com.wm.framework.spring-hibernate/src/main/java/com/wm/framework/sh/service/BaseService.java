/**
 * BaseService.java
 *
 * 功  能： 这是一个所有service接口都要是实现的接口
 * 接口名： BaseService
 *
 *   ver        变更日              担当者     变更内容
 * ──────────────────────────────────

 *
 * Copyright (c) 2012, 2013 CMCC All Rights Reserved.
 * LICENSE INFORMATION
 */
package com.wm.framework.sh.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;

import com.wm.common.exception.DBException;
import com.wm.common.util.Pagination;
import com.wm.framework.util.db.SearchCondition;
import com.wm.framework.util.db.SortCondition;

/**
 * 本接口是所有service接口都要是实现的接口，实现基本的增删改查。
 * 
 * @author
 * @version
 */
public interface BaseService<T, PK extends Serializable> {
	/**
	 * 根据id获取对应的实体
	 * 
	 * @param id
	 *            实体的ID
	 * @return T 得到相应的实体
	 * @throws DBException
	 *             获取实体失败
	 */
	public T get(PK id) throws DBException;

	/**
	 * 保存当前实体
	 * 
	 * @param entity
	 *            要保存的实体
	 * @throws DBException
	 *             保存失败
	 */
	public PK save(T entity) throws DBException;

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
	public PK save(T entity, Long creator) throws DBException;

	/**
	 * 保存集合中的实体
	 * 
	 * @param entitys
	 *            要保存的实体对象
	 * @throws DBException
	 *             保存失败
	 */
	public Collection<PK> saveAll(Collection<T> entitys) throws DBException;

	/**
	 * 更新当前实体对象
	 * 
	 * @param entity
	 *            要更新的对象
	 * @throws DBException
	 *             更新失败
	 */
	public void update(T entity) throws DBException;

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
	public void update(T entity, Long updateUser) throws DBException;

	/**
	 * 逻辑删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws DBException
	 *             删除失败
	 */
	public void delete(T entity) throws DBException;

	/**
	 * 方法名称: delete<br>
	 * 描述：逻辑删除 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月25日下午4:07:38
	 * 
	 * @param id
	 * @throws DBException
	 */
	public void delete(PK id) throws DBException;

	/**
	 * 物理删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws DBException
	 *             删除失败
	 */
	public void deleteFromDB(T entity) throws DBException;

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
			throws DBException;

	/**
	 * 方法名称: findPageDataCms<br>
	 * 描述：根据检索条件，排序条件以及分页条件获取对应实体的一页的列表 Cms 作者: 王叶
	 * 
	 * @param searchConditions
	 * @param pagination
	 * @param sortConditions
	 * @throws DBException
	 */
	public void findPageDataCms(List<SearchCondition> searchConditions,
			Pagination<T> pagination, List<SortCondition> sortConditions)
			throws DBException;

	public void findPageData(Pagination<T> pagination, SearchCondition sc)
			throws DBException;

	public void findPageData(Pagination<T> pagination,
			SearchCondition searchCondition, SortCondition sortCondition)
			throws DBException;

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
	public List<T> findAllData(final List<SearchCondition> searchConditions,
			List<SortCondition> sortConditions) throws DBException;

	public JSONArray findAllJson(final String hql, final Object... values);

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
			throws DBException;

	/**
	 * 根据获得的参数删除对应的数据，是逻辑删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws DBException
	 *             删除失败
	 */
	public void deleteAll(Collection<PK> ids) throws DBException;

	/**
	 * 根据获得的参数删除对应的数据，是物理删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws DBException
	 *             删除失败
	 */
	public void deleteAllFromDB(Collection<PK> ids) throws DBException;

	// public List<T> findAllDataDB(final List<SearchCondition>
	// searchConditions, List<SortCondition> sortConditions) throws
	// DBException;

	/**
	 * 方法名称: findBean<br>
	 * 描述：查询当前entity下查询指定的字段 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年8月23日下午5:40:21
	 * 
	 * @param beanName
	 * @param pagination
	 */
	public void findBean(String beanName, Pagination<Object> pagination);
}
