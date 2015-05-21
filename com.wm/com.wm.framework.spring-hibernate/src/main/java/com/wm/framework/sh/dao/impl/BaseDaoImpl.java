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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.wm.common.util.ObjectUtil;
import com.wm.common.util.Pagination;
import com.wm.framework.sh.dao.BaseDao;
import com.wm.framework.sh.entity.BaseEntity;
import com.wm.framework.util.db.CompareOperator;
import com.wm.framework.util.db.SearchCondition;
import com.wm.framework.util.db.SortCondition;
import com.wm.framework.util.json.JSONUtil;

/**
 * 本类是所有dao实现类都要继承的基类，实现基本的增删改查。
 * 
 * @author
 * @version
 */
/**描述：<br>
 * 作者：王叶 <br>
 * 修改日期：2014年12月23日上午11:26:13 <br>
 * E-mail:  <br>
 * @param <T>
 * @param <PK> 
 */
@SuppressWarnings("unchecked")
public class BaseDaoImpl<T extends BaseEntity, PK extends Serializable> extends
		HibernateDaoSupport implements BaseDao<T, PK> {

	private static final Logger log = LogManager.getLogger(BaseDaoImpl.class);
	private static Configuration hibernateConf;

	@Resource
	public void setSessionFactory0(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	protected Configuration getHibernateConf() {
		if (hibernateConf == null) {
			hibernateConf = new Configuration().configure();// *.hbm.xml方式
		}
		return hibernateConf;
	}

	protected PersistentClass getPersistentClass(Class<?> clazz) {
		PersistentClass pc = getHibernateConf()
				.getClassMapping(clazz.getName());
		return pc;
	}

	protected String getColumnName(Class<?> clazz, String propertyName) {
		PersistentClass persistentClass = getPersistentClass(clazz);
		Property property = persistentClass.getProperty(propertyName);
		Iterator<?> it = property.getColumnIterator();
		if (it.hasNext()) {
			Column column = (Column) it.next();
			return column.getName();
		}
		return null;
	}

	/**
	 * 根据id获取对应的实体
	 * 
	 * @param id
	 *            实体的ID
	 * @return T 得到相应的实体
	 * @throws TariffRateException
	 *             获取实体失败
	 */
	public T get(PK id) {
		return (T) this.getHibernateTemplate().get(getGenericType(0), id);
	}

	/**
	 * 保存当前实体
	 * 
	 * @param entity
	 *            要保存的实体
	 * @throws TariffRateException
	 *             保存失败
	 */
	public PK save(T entity) {
		return (PK) this.getHibernateTemplate().save(entity);
	}

	/**
	 * 保存集合中的实体
	 * 
	 * @param entitys
	 *            要保存的实体对象
	 * @throws TariffRateException
	 *             保存失败
	 */
	public Collection<PK> saveAll(Collection<T> entitys) {
		log.debug("saveAll start");
		Collection<PK> result = new ArrayList<PK>();
		if (entitys != null) {
			for (T entity : entitys) {
				PK pk = save(entity);
				result.add(pk);
			}
		}
		log.debug("saveAll end");
		return result;
	}

	/**
	 * 更新当前实体对象
	 * 
	 * @param entity
	 *            要更新的对象【持久态】
	 * @throws TariffRateException
	 *             更新失败
	 */
	public void update(T entity) {
		this.getHibernateTemplate().update(entity);
	}

	/**
	 * 逻辑删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void delete(T entity) {
		BaseEntity base = (BaseEntity) entity;
//		base.setDelUserId(1L);
//		base.setDelTime(new Timestamp(System.currentTimeMillis()));
		base.setDelMark(1);
		update(entity);
	}

	/**
	 * 物理删除当前实体对象
	 * 
	 * @param entity
	 *            要删除的对象
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void deleteFromDB(T entity) {
		this.getHibernateTemplate().delete(entity);
	}

	protected String buildSearchCondition(String prefix,
			List<SearchCondition> searchConditions) {
		if (searchConditions == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		int index = 0;
		for (SearchCondition searchCondition : searchConditions) {
			if (null == searchCondition) {
				continue;
			}
			builder.append(" AND ");
			builder.append(prefix);
			builder.append(".");
			builder.append(searchCondition.getField());
			if (searchCondition.getOperator() == CompareOperator.Like) {
				builder.append(" like :param");
			} else if (searchCondition.getOperator() == CompareOperator.In) {
				builder.append(" in (:param");
			} else if (searchCondition.getOperator() == CompareOperator.NotIn
					&& searchCondition.getValue().equals("[]")) {
				builder.append(" not in (:param");
			} else if (searchCondition.getOperator() == CompareOperator.MoreThan) {
				builder.append(" > :param");
			} else if (searchCondition.getOperator() == CompareOperator.LessThan) {
				builder.append(" < :param");
			} else if (searchCondition.getOperator() == CompareOperator.IsNull) {
				if ((boolean) searchCondition.getValue()) {
					builder.append(" is null");
				} else {
					builder.append(" is not null");
				}
			} else if (searchCondition.getOperator() == CompareOperator.NotEqual) {
				builder.append(" != :param");
			} else {
				builder.append(" = :param");
			}
			if (searchCondition.getOperator() != CompareOperator.IsNull) {
				builder.append(index++);
			}
			if (searchCondition.getOperator() == CompareOperator.In) {
				builder.append(")");
			}
			if (searchCondition.getOperator() == CompareOperator.NotIn
					&& searchCondition.getValue().equals("[]")) {
				builder.append(")");
			}
		}
		return builder.toString();
	}

	protected String buildSortCondition(String prefix,
			List<SortCondition> sortConditions) {
		return buildSortCondition(prefix, sortConditions, false, null);
	}

	protected String buildSortCondition(String prefix,
			List<SortCondition> sortConditions, boolean useSql,
			Class<?> entityClazz) {
//		StringBuilder builder = new StringBuilder();
		StringBuffer order = new StringBuffer();
		if (sortConditions != null && sortConditions.size() > 0) {
			for (SortCondition sort : sortConditions) {
				if (null == sort) {
					continue;
				}
				order.append(" ");
				if (prefix != null && !prefix.isEmpty()) {
					order.append(prefix);
					order.append(".");
				}
				if (useSql) {
					order.append(getColumnName(entityClazz,
							sort.getSortColumn()));
				} else {
					order.append(sort.getSortColumn());
				}
				order.append(" ");
				order.append(sort.getSortOrder().toString());
				if (sortConditions.indexOf(sort) != sortConditions.size() - 1) {
					order.append(",");
				}
			}
			if (!StringUtils.isEmpty(order.toString())) {
				order.insert(0, " order by");
			}
		}
		return order.toString();
	}

	@SuppressWarnings("rawtypes")
	protected void prepareQueryParams(Query query,
			List<SearchCondition> searchConditions) {
		if (searchConditions == null) {
			return;
		}
		int index = 0;
		for (SearchCondition searchCondition : searchConditions) {
			if(null == searchCondition){
				continue;
			}
			if (searchCondition.getOperator() == CompareOperator.Like) {
				query.setParameter("param" + index++,
						"%" + searchCondition.getValue() + "%");
			} else if (searchCondition.getOperator() == CompareOperator.In) {
				query.setParameterList("param" + index++,
						(Collection) searchCondition.getValue());
			} else if (searchCondition.getOperator() == CompareOperator.IsNull) {

			} else {
				query.setParameter("param" + index++,
						searchCondition.getValue());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void findPageData(final List<SearchCondition> searchConditions,
			final Pagination<T> pagination,
			final List<SortCondition> sortConditions) {
		StringBuilder builder = new StringBuilder("from ");
		builder.append(getGenericType(0).getSimpleName());
		builder.append(" t where t.delMark = 0");
		String condition = buildSearchCondition("t", searchConditions);
		if (condition != null) {
			builder.append(condition);
		}
		builder.append(buildSortCondition("t", sortConditions));
		final String hql = builder.toString();
		List<T> data = (List<T>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						prepareQueryParams(query, searchConditions);
						query.setFirstResult((pagination.getPageNo() - 1)
								* pagination.getPageSize());
						query.setMaxResults(pagination.getPageSize());
						return query.list();
					}
				});
		pagination.setData(data);
		Long recordTotal = this.getTotalCount(searchConditions);
		pagination.setTotalCount(recordTotal);
	}

	/**
	 * 方法名称：findPageDataCms <br>
	 * 描述： <br>
	 * 作者：王叶 <br>
	 * 修改日期：2014年10月13日下午6:40:40
	 * 
	 * @see com.wm.framework.sh.dao.BaseDao#findPageDataCms(java.util.List,
	 *      com.fklvtu.framework.sh.util.Pagination, java.util.List)
	 * @param searchConditions
	 * @param pagination
	 * @param sortConditions
	 */
	@SuppressWarnings("rawtypes")
	public void findPageDataCms(final List<SearchCondition> searchConditions,
			final Pagination<T> pagination,
			final List<SortCondition> sortConditions) {
		StringBuilder builder = new StringBuilder("from ");
		builder.append(getGenericType(0).getSimpleName());
		builder.append(" t where 1=1");
		String condition = buildSearchCondition("t", searchConditions);
		if (condition != null) {
			builder.append(condition);
		}
		builder.append(buildSortCondition("t", sortConditions));
		final String hql = builder.toString();
		List<T> data = (List<T>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						prepareQueryParams(query, searchConditions);
						query.setFirstResult((pagination.getPageNo() - 1)
								* pagination.getPageSize());
						query.setMaxResults(pagination.getPageSize());
						return query.list();
					}
				});
		pagination.setData(data);
		Long recordTotal = this.getTotalCountCms(searchConditions);
		pagination.setTotalCount(recordTotal);
	}

	public void findPageData(final String hql, Pagination<Object> pagination,
			final Object... parameters) {
		final int pageNo = pagination.getPageNo();
		final int pageSize = pagination.getPageSize();
		List<Object> result = (List<Object>) super.getHibernateTemplate()
				.execute(new HibernateCallback<List<Object>>() {
					public List<Object> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						if (null != parameters) {
							for (int i = 0; i < parameters.length; i++) {
								query.setParameter(i, parameters[i]);
							}
						}
						query.setFirstResult((pageNo - 1) * pageSize);
						query.setMaxResults(pageSize);
						return query.list();
					}
				});
		String[] fields = this.buildFields(hql.toString());

		pagination.setFields(fields);
		pagination.setData(result);
	}

	public void findPageDataWithTotal(final String hql,
			Pagination<Object> pagination, final Object... parameters) {
		this.findPageData(hql, pagination, parameters);

		this.getTotalCount(hql, pagination, parameters);
	}

	public void findPJsonData(final String hql,
			Pagination<JSONObject> pagination, final Object... parameters) {
		final int pageNo = pagination.getPageNo();
		final int pageSize = pagination.getPageSize();
		List<Object> list = (List<Object>) super.getHibernateTemplate()
				.execute(new HibernateCallback<List<Object>>() {
					public List<Object> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						queryAttachParams(query, parameters);
						query.setFirstResult((pageNo - 1) * pageSize);
						query.setMaxResults(pageSize);
						return query.list();
					}
				});

		String[] fields = this.buildFields(hql.toString());
		JSONArray ja = this.buildJSONArray(fields, list);

		pagination.setFields(fields);
		pagination.setData(ja);

	}

	public void findPJsonDataWithTotal(final String hql,
			Pagination<JSONObject> pagination, final Object... parameters) {
		this.findPJsonData(hql, pagination, parameters);
		this.getTotalCount(hql, pagination, parameters);
	}

	/**
	 * 根据检索条件，排序条件获取对应实体的的列表
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
	@SuppressWarnings("rawtypes")
	public List<T> findAllData(final List<SearchCondition> searchConditions,
			final List<SortCondition> sortConditions) {
		StringBuilder builder = new StringBuilder("from ");
		builder.append(getGenericType(0).getSimpleName());
		builder.append(" t where t.delMark = 0");
		String condition = buildSearchCondition("t", searchConditions);
		if (condition != null) {
			builder.append(condition);
		}
		builder.append(buildSortCondition("t", sortConditions));
		final String hql = builder.toString();
		return (List<T>) super.getHibernateTemplate().execute(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						prepareQueryParams(query, searchConditions);
						return query.list();
					}
				});
	}

	@SuppressWarnings("rawtypes")
	public List<T> findAllDataCms(final List<SearchCondition> searchConditions,
			final List<SortCondition> sortConditions) {
		StringBuilder builder = new StringBuilder("from ");
		builder.append(getGenericType(0).getSimpleName());
		builder.append(" t where 1=1 ");
		String condition = buildSearchCondition("t", searchConditions);
		if (condition != null) {
			builder.append(condition);
		}
		builder.append(buildSortCondition("t", sortConditions));
		final String hql = builder.toString();
		return (List<T>) super.getHibernateTemplate().execute(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						prepareQueryParams(query, searchConditions);
						return query.list();
					}
				});
	}
	



	/**
	 * 根据检索条件获取总数
	 * 
	 * @param searchConditions
	 *            检索条件
	 * @return long 总数
	 * @throws TariffRateException
	 *             获取失败
	 */
	@SuppressWarnings("rawtypes")
	public long getTotalCount(final List<SearchCondition> searchConditions) {
		StringBuilder builder = new StringBuilder("select count(*) from ");
		builder.append(getGenericType(0).getSimpleName());
		builder.append(" t where t.delMark = 0");
		String condition = buildSearchCondition("t", searchConditions);
		if (condition != null) {
			builder.append(condition);
		}
		final String hql = builder.toString();
		return super.getHibernateTemplate().execute(
				new HibernateCallback<Long>() {
					public Long doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						prepareQueryParams(query, searchConditions);
						List lstResult = query.list();
						if (lstResult.size() > 0) {
							String total = lstResult.get(0).toString();
							return Long.parseLong(total);
						} else {
							return 0L;
						}
					}
				});
	}

	/**
	 * 方法名称: getTotalCountCms<br>
	 * 描述：根据检索条件获取总数 作者: 王叶 修改日期：2014年10月13日下午7:30:34
	 * 
	 * @param searchConditions
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public long getTotalCountCms(final List<SearchCondition> searchConditions) {
		StringBuilder builder = new StringBuilder("select count(*) from ");
		builder.append(getGenericType(0).getSimpleName());
		builder.append(" t where t.havehtml=1");
		String condition = buildSearchCondition("t", searchConditions);
		if (condition != null) {
			builder.append(condition);
		}
		final String hql = builder.toString();
		return super.getHibernateTemplate().execute(
				new HibernateCallback<Long>() {
					public Long doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						prepareQueryParams(query, searchConditions);
						List lstResult = query.list();
						if (lstResult.size() > 0) {
							String total = lstResult.get(0).toString();
							return Long.parseLong(total);
						} else {
							return 0L;
						}
					}
				});
	}

	/**
	 * 根据获得的参数删除对应的数据，是逻辑删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void deleteAll(Collection<PK> ids) {
		for (PK id : ids) {
			T entity = get(id);
			delete(entity);
		}
	}

	/**
	 * 根据获得的参数删除对应的数据，是物理删除
	 * 
	 * @param ids
	 *            要删除数据的id集合
	 * @throws TariffRateException
	 *             删除失败
	 */
	public void deleteAllFromDB(Collection<PK> ids) {
		for (PK id : ids) {
			T entity = get(id);
			deleteFromDB(entity);
		}
	}

	@SuppressWarnings("rawtypes")
	protected Class getGenericType(int index) {
		Type genType = getClass().getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			throw new RuntimeException("Index outof bounds");
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}

	// public List<T> findAllDataDelete(
	// final List<SearchCondition> searchConditions,
	// final List<SortCondition> sortConditions) {
	// StringBuilder builder = new StringBuilder("from ");
	// builder.append(getGenericType(0).getSimpleName());
	// builder.append(" t where t.delMark = 1");
	// String condition = buildSearchCondition("t", searchConditions);
	// if (condition != null) {
	// builder.append(condition);
	// }
	// builder.append(buildSortCondition("t", sortConditions));
	// final String hql = builder.toString();
	// return (List<T>) super.getHibernateTemplate().execute(
	// new HibernateCallback<List<T>>() {
	// public List<T> doInHibernate(Session session)
	// throws HibernateException {
	// Query query = session.createQuery(hql);
	// prepareQueryParams(query, searchConditions);
	// return query.list();
	// }
	// });
	// }

	// @SuppressWarnings("rawtypes")
	// public List<T> findAllDataDB(final List<SearchCondition>
	// searchConditions, final List<SortCondition> sortConditions){
	// StringBuilder builder = new StringBuilder("from ");
	// builder.append(getGenericType(0).getSimpleName());
	// builder.append(" t where 1=1");
	// String condition = buildSearchCondition("t", searchConditions);
	// if(condition != null){
	// builder.append(condition);
	// }
	// builder.append(buildSortCondition("t", sortConditions));
	// final String hql = builder.toString();
	// return (List<T>)getHibernateTemplate().execute(new HibernateCallback(){
	// public Object doInHibernate(Session session) throws HibernateException,
	// SQLException {
	// Query query = session.createQuery(hql);
	// prepareQueryParams(query, searchConditions);
	// return query.list();
	// }
	// });
	// }

	/**
	 * 方法名称: findByPagination<br>
	 * 描述：根据hql执行分页搜索 作者: 王猛 修改日期：2014年7月25日下午3:08:35
	 * 
	 * @param hql
	 * @param pagination
	 * @param parameters
	 */
	public void findByPagination(final String hql, Pagination<T> pagination,
			final Object... parameters) {
		// final String hqlEx = "select * from ("+ hql +
		// ") t and t.delMark = 0";
		final int pageNo = pagination.getPageNo();
		final int pageSize = pagination.getPageSize();
		List<T> result = (List<T>) super.getHibernateTemplate().execute(
				new HibernateCallback<List<T>>() {
					public List<T> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						if (null != parameters) {
							for (int i = 0; i < parameters.length; i++) {
								query.setParameter(i, parameters[i]);
							}
						}
						query.setFirstResult((pageNo - 1) * pageSize);
						query.setMaxResults(pageSize);
						return query.list();
					}
				});
		pagination.setData(result);
	}

	public void findByPaginationWithTotal(final String hql,
			Pagination<T> pagination, final Object... parameters) {
		this.findByPagination(hql, pagination, parameters);

		getTotalCount(hql, pagination, parameters);
	}

	private void getTotalCount(final String hql, Pagination<?> pagination,
			final Object... parameters) {
		int fromIndex = hql.toLowerCase().lastIndexOf(" from ");
		String lastHql = hql.substring(fromIndex);

		final StringBuilder hqlCount = new StringBuilder("select count(*) ");
		hqlCount.append(lastHql);

//		List<Long> totals = (List<Long>) super.getHibernateTemplate().find(
//				hqlCount.toString(), parameters);
		List<Long> totals = super.getHibernateTemplate().execute(
				new HibernateCallback<List<Long>>() {
					public List<Long> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hqlCount.toString());
						if (parameters.length==1) {
							Object param = parameters;
							if (param instanceof List) {
								List<Object> paramList = (List<Object>) param;
								queryAttachParams(query, paramList);
							}else{
								queryAttachParams(query, parameters);
							}
						}
						return query.list();
					}
				});
		
		if (totals.size() > 0) {
			pagination.setTotalCount(totals.get(0));
		}
	}

	public List<?> find(final String hql, final Object... parameters) {
		List<?> result = (List<?>) super.getHibernateTemplate().execute(
				new HibernateCallback<List<?>>() {
					public List<?> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						if (null != parameters) {
							for (int i = 0; i < parameters.length; i++) {
								query.setParameter(i, parameters[i]);
							}
						}
						return query.list();
					}
				});
		return result;
	}


	public List<T> findAll(final String hql, final Object... params) {
		List<T> result = (List<T>) super.getHibernateTemplate().execute(
				new HibernateCallback<List<T>>() {
					public List<T> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						if (params.length==1) {
							Object param = params;
							if (param instanceof List) {
								List<Object> paramList = (List<Object>) param;
								queryAttachParams(query, paramList);
							}else{
								queryAttachParams(query, params);
							}
						}
						
						return query.list();
					}
				});
		return result;
	}
	

	public void findBean(String beanName, Pagination<Object> pagination) {
		beanName = "com.fklvtu.travel.controller.view." + beanName;
		Object obj = null;
		try {
			obj = Class.forName(beanName).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			log.error(e);
			return;
		}

		ObjectUtil ou = new ObjectUtil();
		String[] fieldNames = ou.getFiledNames(obj);
		StringBuffer hqlFields = new StringBuffer();
		for (int i = 0; i < fieldNames.length; i++) {
			if (i > 0) {
				hqlFields.append(",");
			}
			hqlFields.append(fieldNames[i]);
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select ");
		hql.append(hqlFields);
		hql.append(" from ").append(getGenericType(0).getSimpleName());

		final String hqlStr = hql.toString();
		final int pageNo = pagination.getPageNo();
		final int pageSize = pagination.getPageSize();
		List<Object> valuesList = super.getHibernateTemplate().execute(
				new HibernateCallback<List<Object>>() {
					public List<Object> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hqlStr);
						// if (null != parameters) {
						// for (int i = 1; i < parameters.length; i++) {
						// query.setParameter(i, parameters[i]);
						// }
						// }
						query.setFirstResult((pageNo - 1) * pageSize);
						query.setMaxResults(pageSize);
						return query.list();
					}
				});

		if (valuesList.size() == 0) {
			return;
		}
		List<Object> result = new ArrayList<Object>();
		try {
			for (int i = 0; i < valuesList.size(); i++) {
				Object[] values = (Object[]) valuesList.get(i);
				Object item = Class.forName(beanName).newInstance();
				ou.setFiledValues(item, fieldNames, values);
				result.add(item);
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			log.error(e);
		}
		pagination.setData(result);
	}

	/**
	 * 方法名称: getJson<br>
	 * 描述：查询一条记录 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年10月17日上午11:12:32
	 * 
	 * @param hql
	 *            hql语句
	 * @param values
	 *            参数值
	 * @return
	 */
	public JSONObject getJson(String hql, Object... values) {
		JSONObject result = null;
		List<?> list = this.getHibernateTemplate().find(hql, values);
		if (list.size() > 0) {
			String[] fields = this.buildFields(hql.toString());
			Object[] row = (Object[]) list.get(0);
			result = buildJSONObject(fields, row);
		}
		return result;
	}

	/**
	 * 方法名称: findAllJson<br>
	 * 描述：查询所有 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年10月17日上午11:51:25
	 * 
	 * @param hql
	 * @param values
	 *            可以是 list 或者 多个可变值参数
	 * @return 结果列表。查询不到返回空集合
	 */
	public JSONArray findAllJson(final String hql, final Object... values) {
		log.debug("hql: " + hql);
		List<Object> list = (List<Object>) super.getHibernateTemplate()
				.execute(new HibernateCallback<List<Object>>() {
					public List<Object> doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						queryAttachParams(query, values);
//						query.setFirstResult((pageNo - 1) * pageSize);
//						query.setMaxResults(pageSize);
						return query.list();
					}
				});

		String[] fields = this.buildFields(hql.toString());
		log.debug("fields"+JSONArray.fromObject(fields));
		JSONArray result = buildJSONArray(fields, list);
		
		log.debug("findAllJson: " + JSONUtil.convertJSONArray(result));
		return result;
	}

	/**
	 * 方法名称: buildFields<br>
	 * 描述：根据sql构建查询的字段。定义字段别名必须用as <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年10月14日下午2:17:18
	 * 
	 * @param sql
	 * @return
	 */
	private String[] buildFields(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select ") + 6;
		int fromIndex = sql.toLowerCase().lastIndexOf(" from ");

		String columnsStr = sql.substring(selectIndex, fromIndex).trim();
		// System.out.println(columnsStr);
		String[] columnStrArr = columnsStr.split(", ");
		String[] fields = new String[columnStrArr.length];
		for (int i = 0; i < columnStrArr.length; i++) {
			String columnStr = columnStrArr[i].trim();
			int asIndex = columnStr.lastIndexOf(" as ");
			if (-1 != asIndex) {
				columnStr = columnStr.substring(asIndex + 4);
			} else {
				int dIndex = columnStr.lastIndexOf(".");
				if (-1 != dIndex) {
					columnStr = columnStr.substring(dIndex + 1);
				}
			}
			int indexFirstSpace = columnStr.indexOf(" ");
			if (-1!=indexFirstSpace) {
				columnStr = columnStr.substring(0, indexFirstSpace);
			}
			fields[i] = columnStr;
			// System.out.println(columnStr);
		}
		return fields;
	}

	/**
	 * 方法名称: buildJsonObject<br>
	 * 描述：将一条记录构建成json格式 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年10月17日上午10:39:43
	 * 
	 * @param fields
	 *            字段数组
	 * @param data
	 *            查询结果数组或对象
	 * @return
	 */
	
	private JSONObject buildJSONObject(String[] fields, Object data) {
		if (null == fields || null == data) {
			return null;
		}

		JSONObject jo = new JSONObject();
		if (data instanceof Object[]) {
			Object[] row = (Object[]) data;
			for (int j = 0; j < fields.length; j++) {// 循环列
				String field = fields[j];
				Object value = row[j];
				jo.put(field, value);
			}
		}else {
			jo.put(fields[0], data);
		}
		
		return jo;
	}

	private JSONArray buildJSONArray(String[] fields, List<?> list) {
		JSONArray result = new JSONArray();
		if (null == fields || null == list) {
			return result;
		} else if (list.size() == 0) {
			return result;
		}

		for (int i = 0; i < list.size(); i++) {
			JSONObject jo = new JSONObject();
			Object data = list.get(i);
			
			jo = buildJSONObject(fields, data);
			result.add(jo);
		}
		return result;
	}

	/**
	 * 方法名称: executeHQLUpdate<br>
	 * 描述：执行hql批量更新语句 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月19日上午10:17:35
	 * 
	 * @param hqlStr
	 *            hql语句
	 * @param params
	 *            可变参数
	 * @return 影响的行数
	 */
	public int executeHQLUpdate(String hqlStr, Object... params) {
		log.debug("executeHQLUpdate start");
		int affected = super.getHibernateTemplate().bulkUpdate(hqlStr, params);
		log.debug("executeHQLUpdate end 更新条数：" + affected);
		return affected;
	}

	/**
	 * 方法名称: executeSQLUpdate<br>
	 * 描述：执行SQL更新语句 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月19日上午10:20:29
	 * 
	 * @param sqlStr
	 *            sql语句
	 * @param params
	 *            可变参数
	 * @return 影响行数
	 */
	public int executeSQLUpdate(String sqlStr, Object... params) {
		log.debug("executeSQLUpdate start");
		SQLQuery sqlQuery = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sqlStr);
		this.queryAttachParams(sqlQuery, params);
		int affected = sqlQuery.executeUpdate();
		log.debug("executeSQLUpdate end 更新条数：" + affected);
		return affected;
	}
	
	
	
	/**方法名称：executeSQLFindByPagination <br>
	 * 描述：SQL查询 可以分页 <br>
	 * 作者：王叶 <br>
	 * 修改日期：2014年12月23日上午11:26:15 
	 * @see com.wm.framework.sh.dao.BaseDao#executeSQLFindByPagination(java.lang.String, com.fklvtu.framework.sh.util.Pagination, java.lang.Object[]) 
	 * @param sql
	 * @param pagination
	 * @param params
	 * @return
	 */
	public void executeSQLFindByPagination(String sql,String sqlTotalCount, Pagination<JSONObject> pagination, Object... params) {
		log.debug("executeSQLFind start");
		SQLQuery sqlQuery = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sql);
		this.queryAttachParams(sqlQuery, params);
		int pageNo = pagination.getPageNo();
		int pageSize = pagination.getPageSize();
		sqlQuery.setFirstResult((pageNo - 1) * pageSize);
		sqlQuery.setMaxResults(pageSize);
		List<JSONObject> affected = sqlQuery.list();
		//
		String[] fields = this.buildFields(sqlTotalCount);
		//String[] fields = this.buildFields(sql);
		JSONArray ja = this.buildJSONArray(fields, affected);
		pagination.setFields(fields);
		pagination.setData(ja);
		
		this.sqlGetTotalCount(sql, pagination, params);
		log.debug("executeSQLFind end 查询结果：" + affected);
		
	}
	
	private void sqlGetTotalCount(String sql, Pagination<?> pagination,
			Object... params) {
		StringBuilder hqlCount = new StringBuilder("select count(*) from (");
		hqlCount.append(sql).append(") as allData");

		SQLQuery sqlQuery = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(hqlCount.toString());
		this.queryAttachParams(sqlQuery, params);
		Object ur = sqlQuery.uniqueResult();
		pagination.setTotalCount(Long.parseLong(ur.toString()));
//		List<?> totals = sqlQuery.list();
//		if (totals.size() > 0) {
//			long total = Long.parseLong(totals.get(0).toString());
//			pagination.setRecordTotal(total);
//		}
	}

	@Override
	public void executeSQLFindByPagination2(String sql, String sqlTotalCount,
			Pagination<JSONObject> pagination, Object... params) {
		log.debug("executeSQLFind start");
		SQLQuery sqlQuery = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sql);
		System.out.println("sql---->"+sql);
		this.queryAttachParams(sqlQuery, params);
		int pageNo = pagination.getPageNo();
		int pageSize = pagination.getPageSize();
		sqlQuery.setFirstResult((pageNo - 1) * pageSize);
		sqlQuery.setMaxResults(pageSize);
		List<JSONObject> affected = sqlQuery.list();
		//
		String[] fields = this.buildFields(sqlTotalCount);
		JSONArray ja = this.buildJSONArray(fields, affected);
		pagination.setFields(fields);
		pagination.setData(ja);		
		log.debug("executeSQLFind end 查询结果：" + affected);
		
	}
	
	private void queryAttachParams(Query query, Object... params) {
		if (null != params) {
			
			if (params.length==1) {
				Object param = params[0];
				if (param instanceof List) {
					List<Object> paramList = (List<Object>) param;
					for (int i = 0; i < paramList.size(); i++) {
						Object parameter = paramList.get(i);
						if (null != parameter) {
		 					query.setParameter(i, parameter);
						}
					}
					return;
				}
			}
			
			for (int i = 0; i < params.length; i++) {
				Object parameter = params[i];
				if (null != parameter) {
					query.setParameter(i, parameter);
				}
			}
			
			
			
		}
	}
	
	/**
	 * 方法名称: delete<br>
	 * 描述：逻辑删除 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2014年11月25日下午4:01:35
	 * @param id
	 */
	public int delete(PK id) {
		StringBuffer hql = new StringBuffer();
		hql.append("update ").append(getGenericType(0).getSimpleName());
		hql.append(" set delMark=1");
		hql.append(" where id=?");
		return this.executeHQLUpdate(hql.toString(), id);
	}

	
	
	
	
}
