package com.wm.framework.sh.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.wm.common.util.Pagination;
import com.wm.framework.util.db.SearchCondition;
import com.wm.framework.util.db.SortCondition;

public interface BaseDao<T, PK extends Serializable> {
	/**
	 * 根据id获取对应的实体
	 * 
	 * @param id
	 *            实体的ID
	 * @return T 得到相应的实体
	 * @throws TariffRateException
	 *             获取实体失败
	 */
	public T get(PK id);

	/**
	 * 方法名称: save<br>
	 * 描述：执行添加 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年10月10日下午4:32:52
	 * @param entity
	 * @return 添加后的id
	 */
	public PK save(T entity);

	/**
	 * 保存集合中的实体
	 * 
	 * @param entitys
	 *            要保存的实体对象
	 * @throws TariffRateException
	 *             保存失败
	 */
	public Collection<PK> saveAll(Collection<T> entitys);

	/**
	 * 更新当前实体对象
	 * 
	 * @param entity
	 *            要更新的对象
	 * @throws TariffRateException
	 *             更新失败
	 */
	public void update(T entity);

	/**
	 * 逻辑删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void delete(T entity);
	public int delete(PK id);

	/**
	 * 物理删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void deleteFromDB(T entity);

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
	 * @throws TariffRateException
	 *             获取失败
	 */
	public void findPageData(final List<SearchCondition> searchConditions,
			final Pagination<T> pagination,
			final List<SortCondition> sortConditions);
	
	/**方法名称: findPageDataCms<br>
	 * 描述：根据检索条件，排序条件以及分页条件获取对应实体的一页的列表CMS
	 * 作者: 王叶
	 * 修改日期：2014年10月13日下午6:37:51
	 * @param searchConditions
	 * @param pagination
	 * @param sortConditions
	 */
	public void findPageDataCms(final List<SearchCondition> searchConditions,
			final Pagination<T> pagination,
			final List<SortCondition> sortConditions) ;

	public void findPageData(final String hql, Pagination<Object> pagination,
			final Object... parameters);
	
	public void findPJsonData(final String hql, Pagination<JSONObject> pagination,
			final Object... parameters);
	
	/**
	 * 根据检索条件获取总数
	 * 
	 * @param searchConditions
	 *            检索条件
	 * @return long 总数
	 * @throws TariffRateException
	 *             获取失败
	 */
	public long getTotalCount(final List<SearchCondition> searchConditions);

	/**
	 * 根据检索条件，排序条件获取该实体的列表
	 * 
	 * @param searchConditions
	 *            检索条件
	 * @param sortConditions
	 *            排序条件
	 * @return List 该实体的列表
	 * @throws TariffRateException
	 *             获取列表失败
	 */
	public List<T> findAllData(final List<SearchCondition> searchConditions,
			final List<SortCondition> sortConditions);

	/**方法名称: findAllDataCms<br>
	 * 描述：根据检索条件，排序条件获取该实体的列表Cms
	 * 作者: 王叶
	 * 修改日期：2014年10月13日上午11:19:28
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 */
	public List<T> findAllDataCms(final List<SearchCondition> searchConditions,
			final List<SortCondition> sortConditions);
	
	
	/**
	 * 根据获得的参数删除对应的数据，是逻辑删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void deleteAll(Collection<PK> ids);

	/**
	 * 根据获得的参数删除对应的数据，是物理删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void deleteAllFromDB(Collection<PK> ids);

	// public List<T> findAllDataDelete(final List<SearchCondition>
	// searchConditions, final List<SortCondition> sortConditions);

	// public List<T> findAllDataDB(final List<SearchCondition>
	// searchConditions, final List<SortCondition> sortConditions);

	/**
	 * 方法名称: findByPagination<br>
	 * 描述：分页查询，结果会存储在pagination.data里 作者: 王猛 修改日期：2014年8月19日下午2:10:22
	 * 
	 * @param hql
	 * @param pagination
	 * @param parameters
	 */
	public void findByPagination(final String hql, Pagination<T> pagination,
			final Object... parameters);

	/**
	 * 方法名称: findByPagination<br>
	 * 描述：查询所有，结果会存储在pagination.data里 作者: 王猛 修改日期：2014年8月19日下午2:10:22
	 * 
	 * @param hql
	 * @param pagination
	 * @param parameters
	 */
	public List<T> findAll(final String hql, final Object... parameters);
	
	public JSONArray findAllJson(final String hql, final Object... values);

	/**
	 * 方法名称: findBean<br>
	 * 描述：查询当前entity下查询指定的字段 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年8月23日下午5:40:21
	 * @param beanName
	 * @param pagination
	 */
	public void findBean(String beanName, Pagination<Object> pagination);
	
	/**方法名称: executeSQLFindByPagination<br>
	 * 描述：SQL查询 可以分页
	 * 作者: 王叶
	 * 修改日期：2014年12月23日上午11:25:53
	 * @param sqlStr
	 * @param pagination
	 * @param params
	 * @return
	 */
	public void executeSQLFindByPagination(String sqlStr, String sqlTotalCount, Pagination<JSONObject> pagination, Object... params);

	/**方法名称: executeSQLFindByPagination2<br>
	 * 描述：多表union 查询sql 分页 不返回总条数
	 * 作者: 王叶
	 * 修改日期：2015年3月4日下午3:20:20
	 * @param sql
	 * @param sqlTotalCount
	 * @param pagination
	 * @param params
	 */
	public void executeSQLFindByPagination2(String sql,String sqlTotalCount, Pagination<JSONObject> pagination, Object... params);


}
