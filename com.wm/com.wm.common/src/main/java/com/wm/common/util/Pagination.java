package com.wm.common.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsDateJsonBeanProcessor;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 描述：分页对象<br>
 * 作者：王猛 <br>
 * 修改日期：2015年3月23日下午4:18:25 <br>
 * E-mail: <br>
 * 
 * @param <T>
 */
public class Pagination<T> {
	public Pagination() {
	}

	/**
	 * @param pageNo
	 *            当前页码
	 * @param pageSize
	 *            每页显示个数
	 */
	public Pagination(Integer pageNo, Integer pageSize) {
		if (null != pageNo) {
			this.pageNo = pageNo;
		}
		if (null != pageSize) {
			this.pageSize = pageSize;
		}
	}

	/**
	 * 当前页码
	 */
	private int pageNo = 1;
	/**
	 * 每页显示个数
	 */
	private int pageSize = 20;
	/**
	 * 当前页查询返回的数据个数
	 */
	private int resultCount = 0;
	/**
	 * 总页数
	 */
	private int pageCount = 0;
	/**
	 * 总记录数
	 */
	private long totalCount = 0;

	/**
	 * 查询的结果集合
	 */
	private List<T> data = new ArrayList<T>();

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		this.pageCount = Integer
				.parseInt(((totalCount + this.pageSize - 1) / this.pageSize)
						+ "");
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
		if (null != data) {
			this.setResultCount(data.size());
		}
	}

	@Override
	public String toString() {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonBeanProcessor(java.sql.Date.class,
				new JsDateJsonBeanProcessor());

		JSONObject jo = JSONObject.fromObject(this);
		return jo.toString();
	}

	// public String convertToEasyUI() {
	// JSONObject result=null;
	// String jsonData = null;
	// List<T> entityList= this.getData();
	// long total=this.recordTotal;
	// Map pageMap = new HashMap();
	// pageMap.put("total", total);
	//
	// List dataList = new ArrayList();
	// for(int i=0; i<entityList.size(); i++){
	// Map dataMap = getMapFromEntity(entityList.get(i));
	// dataList.add(dataMap);
	// }
	// pageMap.put("rows", dataList);
	// result = JSONUtil.convertJSONObject(pageMap);
	// jsonData=result.toString();
	// return jsonData;
	// }
	//
	// public String convertToEasyUIJsonObject() {
	// JSONObject result=null;
	// String jsonData = null;
	// List<JSONObject> entityList= (List<JSONObject>) this.getData();
	// long total=this.recordTotal;
	// Map pageMap = new HashMap();
	// pageMap.put("total", total);
	//
	// List dataList = new ArrayList();
	// for(int i=0; i<entityList.size(); i++){
	// Map dataMap = entityList.get(i);
	// dataList.add(dataMap);
	// }
	// pageMap.put("rows", dataList);
	// result = JSONUtil.convertJSONObject(pageMap);
	// jsonData=result.toString();
	// return jsonData;
	// }
	//
	// @SuppressWarnings("rawtypes")
	// protected Map getMapFromEntity(Object entity){
	// Map map = new HashMap();
	// if (entity instanceof JSONObject) {
	// JSONObject jo = (JSONObject) entity;
	// map = (Map) JSONObject.toBean(jo, Map.class);
	// }else{
	// Class<?> clazz = entity.getClass();
	// for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
	// Field[] fds = clazz.getDeclaredFields();
	// for (Field fd : fds) {
	// try {
	// if(!fd.getType().getName().startsWith("java.util.Set") &&
	// !fd.getName().equals("serialVersionUID")){
	// String methodName = "get" + fd.getName().toUpperCase().charAt(0) +
	// fd.getName().substring(1);
	// Method method = entity.getClass().getMethod(methodName, new Class[] {});
	// if(fd.getType().getName().equals("java.util.Date")){
	// Date d = (Date) method.invoke(entity, new Object[] {});
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// if(d!=null){
	// map.put(fd.getName(), formatter.format(d));
	// }else{
	// map.put(fd.getName(), "");
	// }
	// }
	// else
	// {
	// map.put(fd.getName(), method.invoke(entity, new Object[] {}));
	// }
	// }
	// } catch (IllegalArgumentException e) {
	//
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	// }
	// return map;
	// }

	/**
	 * 方法名称: getOffset<br>
	 * 描述：获取跳过的记录数 <br>
	 * 作者: 王猛 <br>
	 * 修改日期：2015年3月24日下午6:21:06
	 * @return
	 */
	public int getOffset() {
		return (this.getPageNo() - 1) * this.getPageSize();
	}

}
