package com.wm.framework.sn.dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.sequoiadb.base.CollectionSpace;
import com.sequoiadb.base.DBCollection;
import com.sequoiadb.base.DBCursor;
import com.sequoiadb.base.DBQuery;
import com.sequoiadb.base.Sequoiadb;
import com.sequoiadb.exception.BaseException;
import com.wm.common.exception.DBException;
import com.wm.common.util.ObjectUtil;
import com.wm.common.util.Pagination;
import com.wm.framework.sn.entity.ATable;
import com.wm.framework.sn.entity.BaseEntity;
import com.wm.framework.util.db.CompareType;
import com.wm.framework.util.db.SearchCondition;
import com.wm.framework.util.db.SortCondition;
import com.wm.framework.util.json.JSONUtil;

/**
 * 数据操作base类
 * 
 * @author mengxg
 *
 * @param <T>
 */
public abstract class BaseDao<T extends BaseEntity> {

	private static final Logger log = LogManager.getLogger(BaseDao.class);

	private Class<T> tClass;

	@SuppressWarnings("unchecked")
	private Class<? extends BaseEntity> getTClass() {
		tClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		return tClass;
	}

	/************************ 本类使用私有方法 *******************************/

	private String buildIDIndexName() {
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		return aTable.space() + "_" + aTable.name() + "_id";
	}

	private List<T> convert(DBCursor cursor) throws Exception {
		List<T> list = null;
		if (null != cursor) {
			list = new ArrayList<T>();
			while (cursor.hasNext()) {
				BSONObject bson = cursor.getNext();
				// T t = null;
				@SuppressWarnings("unchecked")
				T t = (T) bson.as(this.getTClass());
				list.add(t);
			}
		}
		cursor.close();
		return list;
	}

	private JSONArray convertToJson(DBCursor cursor) {
		JSONArray ja = null;
		if (null != cursor) {
			ja = new JSONArray();
			while (cursor.hasNext()) {
				BSONObject bson = cursor.getNext();
				JSONObject jo = JSONObject.fromObject(bson);
				ja.add(jo);
			}
		}
		cursor.close();
		return ja;
	}

	private List<JSONObject> convertToListJson(DBCursor cursor) {
		List<JSONObject> ja = null;
		if (null != cursor) {
			ja = new ArrayList<JSONObject>();
			while (cursor.hasNext()) {
				BSONObject bson = cursor.getNext();
				bson.removeField("_id");
				JSONObject jo = JSONUtil.convertJSONObject(bson);
				ja.add(jo);
			}
		}
		cursor.close();
		return ja;
	}

	private BSONObject convertForUpdate(T entity) {
		BSONObject m = new BasicBSONObject();
		ObjectUtil ou = new ObjectUtil();
		List<String> fieldNames = ou.getFiledNames(entity.getClass(), 1);
		for (int i = 0; i < fieldNames.size(); i++) {
			String fieldName = fieldNames.get(i);
			Object value = ou.getFieldValueByName(fieldName, entity);
			if (null != value) {
				m.put(fieldName, value);
			}
		}
		return m;
	}

	/****************************** end *********************************/

	/*
	 * private void execTran(){ Sequoiadb conn=null; try {
	 * conn=DBManager.getTrancationConnection(); updateTran(id, bson, conn);
	 * deleteTran(id, conn); saveTran(entity, conn); conn.commit(); } catch
	 * (Exception e) { // TODO: handle exception conn.rollback(); } finally{
	 * DBManager.closeConnection(conn); }
	 * 
	 * }
	 */

	/******************** 数据库空间和表操作 ***************************/
	public CollectionSpace createDB(Sequoiadb conn) throws DBException {
		CollectionSpace collectionSpace = null;
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		collectionSpace = conn.createCollectionSpace(aTable.space());
		log.debug("createDB: " + aTable.space());
		return collectionSpace;
	}

	public void dropDB() throws DBException {
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		Sequoiadb conn = DBManager.getConnection();
		conn.dropCollectionSpace(aTable.space());
		DBManager.closeConnection(conn);
		log.debug("dropDB: " + aTable.space());
	}

	private CollectionSpace getDB(Sequoiadb conn) throws DBException {
		CollectionSpace db = null;
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		if (!conn.isCollectionSpaceExist(aTable.space())) {
			db = this.createDB(conn);
		} else {
			db = conn.getCollectionSpace(aTable.space());
		}
		return db;
	}

	public DBCollection createTable(Sequoiadb conn) throws DBException {
		DBCollection table = null;
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		CollectionSpace db = null;
		if (!conn.isCollectionSpaceExist(aTable.space())) {
			db = this.createDB(conn);
		} else {
			db = conn.getCollectionSpace(aTable.space());
		}
		table = db.createCollection(aTable.name());
		log.debug("createTable: " + aTable.space() + ">" + aTable.name());

		String indexName = this.buildIDIndexName();
		// create index key, index on attribute 'id' by ASC(1)/DESC(-1)
		BSONObject key = new BasicBSONObject();
		key.put("id", 1);
		boolean isUnique = true;
		boolean enforced = true;
		table.createIndex(indexName, key, isUnique, enforced);
		log.debug("createIndex: " + indexName);
		return table;
	}

	public void dropTable() throws DBException {
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		this.dropTable(aTable.name());
	}

	public void dropTable(String tableName) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		CollectionSpace db = this.getDB(conn);
		if (null != db) {
			db.dropCollection(tableName);
			log.debug("dropTable: " + db.getName() + ">" + tableName);
		}
		DBManager.closeConnection(conn);
	}

	private DBCollection getTable(Sequoiadb conn) throws DBException {
		CollectionSpace db = this.getDB(conn);
		ATable aTable = this.getTClass().getAnnotation(ATable.class);
		if (!db.isCollectionExist(aTable.name())) {
			this.createTable(conn);
		}

		DBCollection table = db.getCollection(aTable.name());
		return table;
	}

	/************************* end **********************************/

	/****************************** end *********************************/

	/****************************** 事务操作 *****************************/
	public void saveTran(T entity, Sequoiadb conn) throws DBException {
		DBCollection table = this.getTable(conn);

		if (null == entity.getId()) {
			entity.setId(DBUtil.buildLongID());
		}

		if (null == entity.getCreateTime()) {
			entity.setCreateTime(System.currentTimeMillis());
		}

		table.save(entity);
		log.debug("saveTran:" + JSONObject.fromObject(entity));
	}

	public void deleteTran(Collection<Long> ids, Sequoiadb conn)
			throws DBException {
		for (Long id : ids) {
			this.deleteTran(id, conn);
		}
	}
	
	public void deleteTran(Long id, Sequoiadb conn) throws DBException {
		DBCollection table = this.getTable(conn);
		BSONObject condition = DBUtil.buildCondition(new SearchCondition("id",
				CompareType.Equal, id));
		log.debug("deleteTran:" + JSONObject.fromObject(condition));
		table.delete(condition);
	}


	public void deleteTran(BSONObject matcher, Sequoiadb conn)
			throws DBException {
		DBCollection table = this.getTable(conn);
		table.delete(matcher);
		log.debug("deleteTran:" + JSONObject.fromObject(matcher));
	}

	public void updateTran(Long id, BasicBSONObject bson, Sequoiadb conn)
			throws Exception {
		DBCollection table = this.getTable(conn);
		BSONObject matcher = new BasicBSONObject();
		matcher.put("id", id);
		BSONObject modifier = new BasicBSONObject();
		modifier.put("$set", bson);
		BSONObject hint = null;
		table.update(matcher, modifier, hint);
		log.debug("updateTran:" + JSONObject.fromObject(bson));
	}

	public void updateTran(T entity, Sequoiadb conn) throws DBException {
		if (null == entity.getUpdateTime()) {
			entity.setUpdateTime(System.currentTimeMillis());
		}

		DBCollection table = this.getTable(conn);
		BSONObject matcher = new BasicBSONObject();
		matcher.put("id", entity.getId());
		BSONObject modifier = new BasicBSONObject();
		BSONObject m = this.convertForUpdate(entity);
		modifier.put("$set", m);
		BSONObject hint = null;

		table.update(matcher, modifier, hint);
		log.debug("updateTran:" + JSONObject.fromObject(m));
	}
	/*
	 * private void execTran(){ Sequoiadb conn=null; try {
	 * conn=DBManager.getTrancationConnection(); updateTran(id, bson, conn);
	 * deleteTran(id, conn); saveTran(entity, conn); conn.commit(); } catch
	 * (Exception e) { // TODO: handle exception conn.rollback(); } finally{
	 * DBManager.closeConnection(conn); }
	 * 
	 * }
	 */

	/************************* end **********************************/


	/********************* 增删改 **********************************/
	public void save(T entity) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection table = this.getTable(conn);

		if (null == entity.getId()) {
			entity.setId(DBUtil.buildLongID());
		}

		if (null == entity.getCreateTime()) {
			entity.setCreateTime(System.currentTimeMillis());
		}

		table.save(entity);
		DBManager.closeConnection(conn);
		log.debug("save:" + JSONObject.fromObject(entity));
	}

	public void save(List<T> entitys) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection table = this.getTable(conn);
		for (int i = 0; i < entitys.size(); i++) {
			T entity = entitys.get(i);

			if (null == entity.getId()) {
				entity.setId(DBUtil.buildLongID());
			}

			if (null == entity.getCreateTime()) {
				entity.setCreateTime(System.currentTimeMillis());
			}
		}
		table.save(entitys);
		DBManager.closeConnection(conn);
		log.debug("save:" + JSONArray.fromObject(entitys));
	}

	public void delete(Long... ids) throws DBException {
		Sequoiadb conn = null;
		try {
			conn = DBManager.getConnection();
			DBCollection table = this.getTable(conn);
			for (int i = 0; i < ids.length; i++) {
				Long id = ids[i];
				BSONObject condition = DBUtil
						.buildCondition(new SearchCondition("id",
								CompareType.Equal, id));
				table.delete(condition);
				log.debug("delete:" + JSONObject.fromObject(condition));
			}
			conn.commit();
		} catch (BaseException e) {
			conn.rollback();
			throw e;
		} finally {
			DBManager.closeConnection(conn);
		}
	}

	public void delete(Collection<Long> ids) throws DBException {
		Sequoiadb conn = null;
		try {
			conn = DBManager.getTrancationConnection();
			for (Long id : ids) {
				deleteTran(id, conn);
			}
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
		} finally {
			DBManager.closeConnection(conn);
		}
	}

	public void delete(BSONObject matcher) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		this.deleteTran(matcher, conn);
		DBManager.closeConnection(conn);
		log.debug("delete:" + JSONObject.fromObject(matcher));
	}

	public void update(T entity) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		this.updateTran(entity, conn);
		DBManager.closeConnection(conn);
	}

	public void update(Long id, BasicBSONObject bson) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection table = this.getTable(conn);
		BSONObject matcher = new BasicBSONObject();
		matcher.put("id", id);
		BSONObject modifier = new BasicBSONObject();
		modifier.put("$set", bson);
		BSONObject hint = null;
		table.update(matcher, modifier, hint);
		DBManager.closeConnection(conn);
		log.debug("update:" + JSONObject.fromObject(bson));
	}

	public void update(BSONObject matcher, BSONObject modifier)
			throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection table = this.getTable(conn);
		BSONObject hint = null;
		BSONObject bson = new BasicBSONObject();
		bson.put("$set", modifier);
		table.update(matcher, bson, hint);
		DBManager.closeConnection(conn);
		log.debug("update:" + JSONObject.fromObject(modifier));
	}

	/***************************** end ***************************************/

	public T get(Long id) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection table = this.getTable(conn);
		BSONObject bsonCondition = DBUtil.buildCondition(new SearchCondition(
				"id", CompareType.Equal, id));
		BSONObject bsonObject = table.queryOne(bsonCondition, null, null, null,
				DBQuery.FLG_QUERY_FORCE_HINT);
		DBManager.closeConnection(conn);
		if (null == bsonObject) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			T t = (T) bsonObject.as(this.getTClass());
			return t;
		}
	}

	public T get(SearchCondition searchCondition) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		searchConditions.add(searchCondition);
		T t = this.get(searchConditions, conn);
		DBManager.closeConnection(conn);
		return t;
	}

	public T get(List<SearchCondition> searchConditions) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		T t = this.get(searchConditions, conn);
		DBManager.closeConnection(conn);
		return t;
	}

	public T get(List<SearchCondition> searchConditions, Sequoiadb conn)
			throws Exception {

		BSONObject bsonMatcher = DBUtil.buildCondition(searchConditions);

		BSONObject bsonSelector = null;

		BSONObject bsonOrderBy = null;

		BSONObject hint = null;// table.getIndex(Constants.INDEX_NAME);

		DBCollection table = this.getTable(conn);
		BSONObject bsonObject = table.queryOne(bsonMatcher, bsonSelector,
				bsonOrderBy, hint, 0);
		if (null == bsonObject) {
			return null;
		}else{
			@SuppressWarnings("unchecked")
			T t = (T) bsonObject.as(this.getTClass());
			return t;
		}
	}

	public long getTotalCount(Sequoiadb conn) throws DBException {
		SearchCondition searchCondition = null;
		return this.getTotalCount(searchCondition, conn);
	}

	public long getTotalCount(SearchCondition searchCondition)
			throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		long count = this.getTotalCount(searchCondition, conn);
		DBManager.closeConnection(conn);
		return count;
	}

	public long getTotalCount(SearchCondition searchCondition, Sequoiadb conn)
			throws DBException {
		List<SearchCondition> searchConditions = null;
		if (null != searchCondition) {
			searchConditions = new ArrayList<SearchCondition>();
			searchConditions.add(searchCondition);
		}
		return this.getTotalCount(searchConditions, conn);
	}

	public long getTotalCount(List<SearchCondition> searchConditions)
			throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		long count = this.getTotalCount(searchConditions, conn);
		DBManager.closeConnection(conn);
		return count;
	}

	public long getTotalCount(List<SearchCondition> searchConditions,
			Sequoiadb conn) throws DBException {
		DBCollection table = this.getTable(conn);
		BSONObject matcher = DBUtil.buildCondition(searchConditions);
		return table.getCount(matcher);
	}

	public long getTotalCount(String sql, Pagination<?> pagination,
			Sequoiadb conn, Object... params) throws DBException {
		long totalCount = 0;
		StringBuilder sqlCount = new StringBuilder(
				"select count(allData.id) as totalCount from (");
		sqlCount.append(sql).append(") as allData");

		DBCursor cursor = this.execSql(sqlCount.toString(), conn, params);
		while (cursor.hasNext()) {
			BSONObject bson = cursor.getNext();
			totalCount = (long) bson.get("totalCount");
		}
		cursor.close();
		return totalCount;
	}

	public List<T> find() throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection collection = this.getTable(conn);
		DBCursor cursor = collection.query();
		List<T> t = this.convert(cursor);
		DBManager.closeConnection(conn);
		return t;
	}

	public List<T> find(SearchCondition searchCondition) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		searchConditions.add(searchCondition);
		List<T> t = this.find(searchConditions, conn);
		DBManager.closeConnection(conn);
		return t;
	}

	public List<T> find(List<SearchCondition> searchConditions, Sequoiadb conn)
			throws Exception {
		List<SortCondition> sortConditions = new ArrayList<SortCondition>();
		return this.find(null, searchConditions, sortConditions, conn);
	}

	public List<T> find(List<SearchCondition> searchConditions)
			throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		List<SortCondition> sortConditions = new ArrayList<SortCondition>();
		List<T> t = this.find(null, searchConditions, sortConditions, conn);
		DBManager.closeConnection(conn);
		return t;
	}

	public List<T> find(List<String> searchFields,
			List<SearchCondition> searchConditions,
			List<SortCondition> sortConditions, Sequoiadb conn)
			throws Exception {
		BSONObject bsonSelector = DBUtil.buildSelector(searchFields);

		BSONObject bsonMatcher = DBUtil.buildCondition(searchConditions);

		BSONObject bsonOrderBy = DBUtil.buildOrderBy(sortConditions);

		BSONObject hint = null;// table.getIndex(Constants.INDEX_NAME);

		DBCollection table = this.getTable(conn);
		DBCursor cursor = table.query(bsonMatcher, bsonSelector, bsonOrderBy,
				hint);

		List<T> t = this.convert(cursor);
		return t;
	}

	/**
	 * 根据条件返回对象列表
	 * 
	 * @param searchFields
	 * @param searchConditions
	 * @param sortConditions
	 * @return
	 * @throws Exception
	 */
	public List<T> find(List<String> searchFields,
			List<SearchCondition> searchConditions,
			List<SortCondition> sortConditions) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		BSONObject bsonSelector = DBUtil.buildSelector(searchFields);

		BSONObject bsonMatcher = DBUtil.buildCondition(searchConditions);

		BSONObject bsonOrderBy = DBUtil.buildOrderBy(sortConditions);

		BSONObject hint = null;// table.getIndex(Constants.INDEX_NAME);

		DBCollection table = this.getTable(conn);
		DBCursor cursor = table.query(bsonMatcher, bsonSelector, bsonOrderBy,
				hint);

		List<T> t = this.convert(cursor);
		DBManager.closeConnection(conn);
		return t;
	}

	/**
	 * 根据条件返回对象列表
	 * 
	 * @param searchCondition
	 * @param sortCondition
	 * @return
	 * @throws Exception
	 */
	public List<T> find(SearchCondition searchCondition,
			SortCondition sortCondition) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		searchConditions.add(searchCondition);
		List<SortCondition> sortConditions = new ArrayList<SortCondition>();
		sortConditions.add(sortCondition);
		List<T> t = this.find(null, searchConditions, sortConditions);
		DBManager.closeConnection(conn);
		return t;
	}

	/**
	 * 根据Sql返回对象数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<T> find(String sql, Object... params) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		DBCursor cursor = this.execSql(sql, conn, params);
		List<T> t = this.convert(cursor);
		DBManager.closeConnection(conn);
		return t;

	}

	/**
	 * 根据查询条件返回json数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws DBException
	 */
	public JSONArray findJson(String sql, Object... params) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		DBCursor cursor = this.execSql(sql, conn, params);
		JSONArray ja = this.convertToJson(cursor);
		DBManager.closeConnection(conn);
		return ja;
	}
	
	/**
	 * 根据查询条件返回json数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws DBException
	 */
	public JSONArray findJsonTwo(String sql,String sql2, Object... params) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		DBCursor cursor = this.execSql(sql, conn, params);
		JSONArray ja = this.convertToJson(cursor);
		cursor = this.execSql(sql2, conn, params);
		JSONArray ja2=this.convertToJson(cursor);
		for (int i = 0; i < ja2.size(); i++) {
			ja.add(ja2.get(i));
		}
		System.out.println(ja);
		DBManager.closeConnection(conn);
		return ja;
	}

	public void findP(Pagination<T> pagination) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		DBCollection table = this.getTable(conn);
		BSONObject bsonMatcher = null;
		BSONObject bsonSelector = null;
		BSONObject bsonOrderBy = null;
		BSONObject hint = null;// table.getIndex(Constants.INDEX_NAME);
		DBCursor cursor = table.query(bsonMatcher, bsonSelector, bsonOrderBy,
				hint, pagination.getOffset(), pagination.getPageSize());
		List<T> data = this.convert(cursor);
		DBManager.closeConnection(conn);
		pagination.setData(data);
	}

	private void findP(Pagination<T> pagination, Sequoiadb conn)
			throws Exception {
		DBCollection table = this.getTable(conn);
		BSONObject bsonMatcher = null;
		BSONObject bsonSelector = null;
		BSONObject bsonOrderBy = null;
		BSONObject hint = null;// table.getIndex(Constants.INDEX_NAME);
		DBCursor cursor = table.query(bsonMatcher, bsonSelector, bsonOrderBy,
				hint, pagination.getOffset(), pagination.getPageSize());
		List<T> data = this.convert(cursor);
		pagination.setData(data);
	}

	public List<T> findP(List<SearchCondition> searchConditions,
			Pagination<T> pagination, List<SortCondition> sortConditions)
			throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		List<T> list = this.findP(searchConditions, pagination, sortConditions,
				conn);
		DBManager.closeConnection(conn);
		return list;
	}

	public List<T> findP(List<SearchCondition> searchConditions,
			Pagination<T> pagination, List<SortCondition> sortConditions,
			Sequoiadb conn) throws Exception {
		// List<String> searchFields
		BSONObject bsonSelector = null;// buildSelector(searchFields);

		BSONObject bsonMatcher = DBUtil.buildCondition(searchConditions);

		BSONObject bsonOrderBy = DBUtil.buildOrderBy(sortConditions);

		BSONObject hint = null;// table.getIndex(Constants.INDEX_NAME);

		DBCollection table = this.getTable(conn);
		DBCursor cursor = table.query(bsonMatcher, bsonSelector, bsonOrderBy,
				hint, pagination.getOffset(), pagination.getPageSize());

		return this.convert(cursor);
	}

	public void findPJson(String sql, Pagination<JSONObject> pagination,
			Sequoiadb conn, Object... params) throws DBException {
		sql += " limit " + pagination.getPageSize() + " offset "
				+ pagination.getOffset();
		DBCursor cursor = this.execSql(sql, conn, params);
		List<JSONObject> list = this.convertToListJson(cursor);
		pagination.setData(list);
	}

	/**
	 * 根据查询条件分页
	 * 
	 * @param searchConditions
	 * @param pagination
	 * @throws Exception
	 */
	public void findPWithTotal(List<SearchCondition> searchConditions,
			Pagination<T> pagination) throws Exception {
		List<SortCondition> sortConditions = null;
		this.findPWithTotal(searchConditions, pagination, sortConditions);
	}

	/**
	 * 根据查询条件分页
	 * 
	 * @param searchConditions
	 * @param pagination
	 * @param sortCondition
	 * @throws Exception
	 */
	public void findPWithTotal(List<SearchCondition> searchConditions,
			Pagination<T> pagination, SortCondition sortCondition)
			throws Exception {
		List<SortCondition> sortConditions = new ArrayList<SortCondition>();
		sortConditions.add(sortCondition);
		this.findPWithTotal(searchConditions, pagination, sortConditions);
	}

	/**
	 * 根据查询条件,order by 分页
	 * 
	 * @param searchConditions
	 * @param pagination
	 * @param sortConditions
	 * @throws Exception
	 */
	public void findPWithTotal(List<SearchCondition> searchConditions,
			Pagination<T> pagination, List<SortCondition> sortConditions)
			throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		pagination.setData(this.findP(searchConditions, pagination,
				sortConditions, conn));
		long totalCount = this.getTotalCount(searchConditions, conn);
		DBManager.closeConnection(conn);
		pagination.setTotalCount(totalCount);
	}

	/**
	 * 根据SQL语句分页
	 * 
	 * @param sql
	 * @param pagination
	 * @throws Exception
	 */
	public void findPWithTotalBySql(String sql,
			Pagination<JSONObject> pagination) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		this.findPJson(sql, pagination, conn);
		long totalCount = this.getTotalCount(sql, pagination, conn);
		DBManager.closeConnection(conn);
		pagination.setTotalCount(totalCount);
	}

	/**
	 * 没有查询条件分页
	 * 
	 * @param pagination
	 * @throws Exception
	 */
	public void findPWithTotal(Pagination<T> pagination) throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		this.findP(pagination, conn);
		long totalCount = this.getTotalCount(conn);
		DBManager.closeConnection(conn);
		pagination.setTotalCount(totalCount);
	}

	/**
	 * 执行SQL语句分页
	 * 
	 * @param sql
	 * @param pagination
	 * @param params
	 * @throws Exception
	 */
	public void findPJsonWithTotal(String sql,
			Pagination<JSONObject> pagination, Object... params)
			throws Exception {
		Sequoiadb conn = DBManager.getConnection();
		this.findPJson(sql, pagination, conn, params);
		long totalCount = this.getTotalCount(sql, pagination, conn, params);
		DBManager.closeConnection(conn);
		pagination.setTotalCount(totalCount);
	}

	/**
	 * 执行SQL
	 * 
	 * @param sql
	 * @param conn
	 * @param params
	 * @return
	 * @throws DBException
	 */
	private DBCursor execSql(String sql, Sequoiadb conn, Object... params)
			throws DBException {
		String sqlFinal = DBUtil.processSql(sql, params);
		DBCursor cursor = conn.exec(sqlFinal);
		return cursor;
	}

	public void execSqlUpdate(String sql, Object... params) throws DBException {
		Sequoiadb conn = DBManager.getConnection();
		String sqlFinal = DBUtil.processSql(sql, params);
		conn.execUpdate(sqlFinal);
		DBManager.closeConnection(conn);
	}

}
