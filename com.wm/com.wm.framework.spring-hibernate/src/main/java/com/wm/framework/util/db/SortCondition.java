/**
 * SortCondition.java
 *
 * 功  能： 这是一个与所查询数据的排序有关的类
 * 类名： SortCondition
*/
package com.wm.framework.util.db;



/**
 * 本类是与所查询数据的排序有关的类
 * 
 * @author  
 * @version 
 */
public class SortCondition {
	private String sortColumn;//排序的列
	private SortOrder sortOrder;//排序方式
	
	public SortCondition(String sortColumn, SortOrder sortOrder){
		this.sortColumn = sortColumn;
		this.sortOrder = sortOrder;
	}
	
	public String getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
}
